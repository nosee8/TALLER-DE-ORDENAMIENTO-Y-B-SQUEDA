"""
Script para generar gráfico de barras comparativo de algoritmos de ordenamiento.
Compara tiempos de ejecución entre Python y Java.

Requiere: pip install matplotlib pandas
"""

import json
import os

# Intentar importar matplotlib, si no está instalado, mostrar mensaje
try:
    import matplotlib.pyplot as plt
    import matplotlib
    matplotlib.use('Agg')  # Para evitar problemas con backend en Windows
except ImportError:
    print("ERROR: Se requiere matplotlib. Instala con: pip install matplotlib pandas")
    exit(1)

import pandas as pd
import numpy as np


def cargar_resultados():
    """Carga los resultados desde archivos JSON."""
    resultados = []
    
    # Cargar Python
    python_json = 'resultados/benchmark_ordenamiento.json'
    if os.path.exists(python_json):
        with open(python_json, 'r') as f:
            python_data = json.load(f)
            resultados.extend(python_data)
            print(f"Cargados {len(python_data)} resultados de Python")
    else:
        print(f"ADVERTENCIA: No se encontró {python_json}")
        print("Ejecuta primero: python benchmark_python.py")
    
    # Cargar Java
    java_json = 'resultados/benchmark_java.json'
    if os.path.exists(java_json):
        with open(java_json, 'r') as f:
            java_data = json.load(f)
            resultados.extend(java_data)
            print(f"Cargados {len(java_data)} resultados de Java")
    else:
        print(f"ADVERTENCIA: No se encontró {java_json}")
        print("Ejecuta primero: javac BenchmarkJava.java && java BenchmarkJava")
    
    if not resultados:
        print("\nNo hay resultados para graficar.")
        print("Asegúrate de ejecutar ambos benchmarks primero.")
        exit(1)
    
    return pd.DataFrame(resultados)


def crear_grafico(df):
    """Crea el gráfico de barras comparativo."""
    
    # Orden de los algoritmos
    orden_algoritmos = ['HeapSort', 'MergeSort', 'RadixSort', 'DualPivotQuickSort', 'CocktailSort']
    orden_tamanos = ['10k', '100k', '1m']
    
    # Configuración
    fig, axes = plt.subplots(1, 3, figsize=(18, 6))
    fig.suptitle('Comparación de Algoritmos de Ordenamiento: Python vs Java', fontsize=14, fontweight='bold')
    
    colores = {'Python': '#3776AB', 'Java': '#B07219'}  # Python azul, Java naranja
    
    for idx, tamano in enumerate(orden_tamanos):
        ax = axes[idx]
        
        # Filtrar datos por tamaño
        df_tamano = df[df['tamano'] == tamano]
        
        if df_tamano.empty:
            ax.text(0.5, 0.5, f'No hay datos para {tamano}', 
                   ha='center', va='center', transform=ax.transAxes)
            ax.set_title(f'{tamano.upper()} elementos')
            continue
        
        x = np.arange(len(orden_algoritmos))
        width = 0.35
        
        tiempos_python = []
        tiempos_java = []
        
        for algo in orden_algoritmos:
            row = df_tamano[df_tamano['algoritmo'] == algo]
            py_time = row[row['lenguaje'] == 'Python']['tiempo_ms'].values
            java_time = row[row['lenguaje'] == 'Java']['tiempo_ms'].values
            
            tiempos_python.append(py_time[0] if len(py_time) > 0 else 0)
            tiempos_java.append(java_time[0] if len(java_time) > 0 else 0)
        
        # Crear barras
        bars1 = ax.bar(x - width/2, tiempos_python, width, label='Python', 
                      color=colores['Python'], edgecolor='black', linewidth=0.5)
        bars2 = ax.bar(x + width/2, tiempos_java, width, label='Java', 
                      color=colores['Java'], edgecolor='black', linewidth=0.5)
        
        # Agregar valores sobre las barras
        for bar, val in zip(bars1, tiempos_python):
            if val > 0:
                ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(), 
                       f'{val:.1f}', ha='center', va='bottom', fontsize=8, fontweight='bold')
        
        for bar, val in zip(bars2, tiempos_java):
            if val > 0:
                ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(), 
                       f'{val:.1f}', ha='center', va='bottom', fontsize=8, fontweight='bold')
        
        ax.set_xlabel('Algoritmo', fontsize=10)
        ax.set_ylabel('Tiempo (ms)', fontsize=10)
        ax.set_title(f'{tamano.upper()} elementos', fontsize=12, fontweight='bold')
        ax.set_xticks(x)
        ax.set_xticklabels(orden_algoritmos, rotation=30, ha='right', fontsize=9)
        ax.legend(loc='upper left', fontsize=9)
        ax.grid(axis='y', alpha=0.3)
        
        # Usar escala logarítmica si hay diferencias grandes
        max_time = max(max(tiempos_python), max(tiempos_java))
        if max_time > 10000:  # Si hay valores muy grandes
            ax.set_yscale('log')
    
    plt.tight_layout()
    
    # Guardar gráfico
    output_path = 'resultados/grafico_comparativo.png'
    plt.savefig(output_path, dpi=150, bbox_inches='tight', facecolor='white')
    print(f"\nGráfico guardado en: {output_path}")
    
    # También guardar como PDF para el informe
    pdf_path = 'resultados/grafico_comparativo.pdf'
    plt.savefig(pdf_path, format='pdf', bbox_inches='tight', facecolor='white')
    print(f"PDF guardado en: {pdf_path}")
    
    plt.close()


def mostrar_resumen(df):
    """Muestra un resumen de los resultados."""
    print("\n" + "="*60)
    print("RESUMEN DE RESULTADOS")
    print("="*60)
    
    pivot = df.pivot_table(
        values='tiempo_ms', 
        index='algoritmo', 
        columns=['tamano', 'lenguaje'],
        aggfunc='mean'
    )
    
    print(pivot.to_string())


def main():
    print("="*60)
    print("GENERADOR DE GRÁFICOS COMPARATIVOS")
    print("="*60 + "\n")
    
    # Crear directorio si no existe
    os.makedirs('resultados', exist_ok=True)
    
    # Cargar datos
    df = cargar_resultados()
    
    if df.empty:
        print("No hay datos para graficar.")
        return
    
    # Mostrar resumen
    mostrar_resumen(df)
    
    # Crear gráfico
    print("\nGenerando gráfico...")
    crear_grafico(df)
    
    print("\n¡Listo!")


if __name__ == '__main__':
    main()
