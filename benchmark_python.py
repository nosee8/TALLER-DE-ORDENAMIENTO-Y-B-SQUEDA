"""
Benchmark para algoritmos de ordenamiento.
Mide el tiempo de ejecución y exporta resultados a CSV/JSON.
"""

import json
import csv
import time
import sys
import os
import threading
from datetime import datetime

# Agregar Utils al path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'utilsPython'))

from heap_sort import heapSort
from merge_sort import mergeSort
from cocktail_sort import cocktailSort
from dual_pivot_sort import dualPivotQuickSort
from radix_sort import radixSort

# Timeout de 5 minutos (300 segundos) por algoritmo
TIMEOUT_SECONDS = 300

def cargar_datos(archivo):
    """Carga datos desde archivo de texto."""
    datos = []
    with open(archivo, 'r') as f:
        for linea in f:
            datos.append(int(linea.strip()))
    return datos

def copiar_lista(lista):
    """Crea una copia de la lista."""
    return lista.copy()

def medir_tiempo(func, arr, algo_nombre):
    """Mide el tiempo de ejecución de una función con timeout."""
    result = [None]
    error = [None]
    done = [False]

    def run_algorithm():
        try:
            arr_copy = copiar_lista(arr)

            # start DESPUÉS de copiar: medimos solo el algoritmo
            start = time.perf_counter()

            # Algunos algoritmos necesitan parámetros adicionales
            if algo_nombre == 'MergeSort':
                func(arr_copy, 0, len(arr_copy) - 1)
            elif algo_nombre == 'DualPivotQuickSort':
                func(arr_copy, 0, len(arr_copy) - 1)
            else:
                func(arr_copy)

            end = time.perf_counter()
            result[0] = (end - start) * 1000  # milisegundos
        except Exception as e:
            error[0] = e
        finally:
            done[0] = True

    # Ejecutar en un hilo separado con timeout
    thread = threading.Thread(target=run_algorithm)
    thread.daemon = True
    thread.start()
    thread.join(timeout=TIMEOUT_SECONDS)
    
    if not done[0]:
        print(f"  ⚠️ {algo_nombre}: EXCEDIÓ {TIMEOUT_SECONDS}s - demasiadas iteraciones!")
        return None
    
    if error[0]:
        print(f"  ❌ {algo_nombre}: Error - {error[0]}")
        return None
    
    return result[0]

def ejecutar_benchmark():
    """Ejecuta todos los benchmarks y retorna los resultados."""
    
    tamanos = {
        '10k': 'datos/data_10k.txt',
        '100k': 'datos/data_100k.txt',
        '1m': 'datos/data_1m.txt'
    }
    
    algoritmos = {
        'HeapSort': heapSort,
        'MergeSort': mergeSort,
        'CocktailSort': cocktailSort,
        'DualPivotQuickSort': dualPivotQuickSort,
        'RadixSort': radixSort
    }
    
    resultados = []
    
    for tamano_nombre, archivo in tamanos.items():
        print(f"\n=== {tamano_nombre} elementos ===")
        
        # Cargar datos una sola vez por tamaño
        datos = cargar_datos(archivo)
        print(f"Datos cargados: {len(datos)} elementos")
        
        for algo_nombre, algo_func in algoritmos.items():
            tiempo = medir_tiempo(algo_func, datos, algo_nombre)
            if tiempo is not None:
                print(f"  {algo_nombre}: {tiempo:.2f} ms")
                resultados.append({
                    'algoritmo': algo_nombre,
                    'tamano': tamano_nombre,
                    'tiempo_ms': round(tiempo, 2),
                    'lenguaje': 'Python'
                })
    
    return resultados

def exportar_csv(resultados, archivo='resultados/benchmark_ordenamiento.csv'):
    """Exporta resultados a CSV."""
    os.makedirs(os.path.dirname(archivo), exist_ok=True)
    
    with open(archivo, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=['algoritmo', 'tamano', 'tiempo_ms', 'lenguaje'])
        writer.writeheader()
        writer.writerows(resultados)
    
    print(f"\nCSV exportado: {archivo}")

def exportar_json(resultados, archivo='resultados/benchmark_ordenamiento.json'):
    """Exporta resultados a JSON."""
    os.makedirs(os.path.dirname(archivo), exist_ok=True)
    
    with open(archivo, 'w') as f:
        json.dump(resultados, f, indent=2)
    
    print(f"JSON exportado: {archivo}")

if __name__ == '__main__':
    print("=" * 60)
    print("BENCHMARK ALGORITMOS DE ORDENAMIENTO - PYTHON")
    print("=" * 60)
    
    resultados = ejecutar_benchmark()
    
    # Exportar a CSV y JSON
    exportar_csv(resultados)
    exportar_json(resultados)
    
    print("\n=== Benchmark completado ===")
