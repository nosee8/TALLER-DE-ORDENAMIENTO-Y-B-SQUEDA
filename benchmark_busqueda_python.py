"""
Benchmark para algoritmos de búsqueda.
Mide el tiempo de ejecución y exporta resultados a CSV/JSON.

El arreglo se ordena previamente (sin medir ese tiempo).
El objetivo de búsqueda es el elemento central del arreglo ordenado.
"""

import json
import csv
import time
import sys
import os
import threading

# Agregar Utils al path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'utilsPython'))

from binary_search import binarySearch
from ternary_search import ternarySearch
from jump_search import jumpSearch

TIMEOUT_SECONDS = 60


def cargar_datos(archivo):
    datos = []
    with open(archivo, 'r') as f:
        for linea in f:
            datos.append(int(linea.strip()))
    return datos


def medir_tiempo(func, algo_nombre):
    """Mide el tiempo de ejecución de una función con timeout."""
    result = [None]
    error = [None]
    done = [False]

    def run_algorithm():
        try:
            start = time.perf_counter()
            func()
            end = time.perf_counter()
            result[0] = (end - start) * 1000  # milisegundos
        except Exception as e:
            error[0] = e
        finally:
            done[0] = True

    thread = threading.Thread(target=run_algorithm)
    thread.daemon = True
    thread.start()
    thread.join(timeout=TIMEOUT_SECONDS)

    if not done[0]:
        print(f"  ⚠️ {algo_nombre}: EXCEDIÓ {TIMEOUT_SECONDS}s")
        return None

    if error[0]:
        print(f"  ❌ {algo_nombre}: Error - {error[0]}")
        return None

    return result[0]


def ejecutar_benchmark():
    tamanos = {
        '10k':  'datos/data_10k.txt',
        '100k': 'datos/data_100k.txt',
        '1m':   'datos/data_1m.txt',
    }

    resultados = []

    for tamano_nombre, archivo in tamanos.items():
        print(f"\n=== {tamano_nombre} elementos ===")

        # Cargar y ordenar (el tiempo de ordenamiento NO se mide)
        datos = cargar_datos(archivo)
        datos.sort()
        print(f"Datos cargados y ordenados: {len(datos)} elementos")

        # Objetivo: elemento central del arreglo ordenado (garantizado que existe)
        objetivo = datos[len(datos) // 2]
        n = len(datos)
        print(f"Objetivo de búsqueda: {objetivo}")

        algoritmos = {
            'BinarySearch':  lambda arr=datos, x=objetivo: binarySearch(arr, x),
            'TernarySearch': lambda arr=datos, x=objetivo: ternarySearch(arr, 0, len(arr) - 1, x),
            'JumpSearch':    lambda arr=datos, x=objetivo, n=n: jumpSearch(arr, x, n),
        }

        for algo_nombre, algo_func in algoritmos.items():
            tiempo = medir_tiempo(algo_func, algo_nombre)
            if tiempo is not None:
                print(f"  {algo_nombre}: {tiempo:.4f} ms")
                resultados.append({
                    'algoritmo': algo_nombre,
                    'tamano': tamano_nombre,
                    'tiempo_ms': round(tiempo, 4),
                    'lenguaje': 'Python'
                })

    return resultados


def exportar_csv(resultados, archivo='resultados/benchmark_busqueda_python.csv'):
    os.makedirs(os.path.dirname(archivo), exist_ok=True)
    with open(archivo, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=['algoritmo', 'tamano', 'tiempo_ms', 'lenguaje'])
        writer.writeheader()
        writer.writerows(resultados)
    print(f"\nCSV exportado: {archivo}")


def exportar_json(resultados, archivo='resultados/benchmark_busqueda_python.json'):
    os.makedirs(os.path.dirname(archivo), exist_ok=True)
    with open(archivo, 'w') as f:
        json.dump(resultados, f, indent=2)
    print(f"JSON exportado: {archivo}")


if __name__ == '__main__':
    print("=" * 60)
    print("BENCHMARK ALGORITMOS DE BÚSQUEDA - PYTHON")
    print("=" * 60)

    resultados = ejecutar_benchmark()

    exportar_csv(resultados)
    exportar_json(resultados)

    print("\n=== Benchmark completado ===")
