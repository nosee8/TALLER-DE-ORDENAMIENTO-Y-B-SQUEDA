"""
Script para generar gráfico de barras comparativo de algoritmos de búsqueda.
Compara tiempos de ejecución entre Python y Java.

Requiere: pip install matplotlib pandas
"""

import json
import os

try:
    import matplotlib.pyplot as plt
    import matplotlib
    matplotlib.use('Agg')
except ImportError:
    print("ERROR: Se requiere matplotlib. Instala con: pip install matplotlib pandas")
    exit(1)

import pandas as pd
import numpy as np


def cargar_resultados():
    resultados = []

    python_json = 'resultados/benchmark_busqueda_python.json'
    if os.path.exists(python_json):
        with open(python_json, 'r') as f:
            data = json.load(f)
            resultados.extend(data)
            print(f"Cargados {len(data)} resultados de Python")
    else:
        print(f"ADVERTENCIA: No se encontró {python_json}")
        print("Ejecuta primero: python benchmark_busqueda_python.py")

    java_json = 'resultados/benchmark_busqueda_java.json'
    if os.path.exists(java_json):
        with open(java_json, 'r') as f:
            data = json.load(f)
            resultados.extend(data)
            print(f"Cargados {len(data)} resultados de Java")
    else:
        print(f"ADVERTENCIA: No se encontró {java_json}")
        print("Ejecuta primero: javac BenchmarkBusquedaJava.java && java BenchmarkBusquedaJava")

    if not resultados:
        print("\nNo hay resultados para graficar.")
        exit(1)

    return pd.DataFrame(resultados)


def crear_grafico(df):
    orden_algoritmos = ['BinarySearch', 'TernarySearch', 'JumpSearch']
    orden_tamanos = ['10k', '100k', '1m']

    fig, axes = plt.subplots(1, 3, figsize=(16, 6))
    fig.suptitle('Comparación de Algoritmos de Búsqueda: Python vs Java', fontsize=14, fontweight='bold')

    colores = {'Python': '#3776AB', 'Java': '#B07219'}

    for idx, tamano in enumerate(orden_tamanos):
        ax = axes[idx]
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

            # Usar None cuando no hay dato (0 falsearía la escala)
            tiempos_python.append(float(py_time[0]) if len(py_time) > 0 else None)
            tiempos_java.append(float(java_time[0]) if len(java_time) > 0 else None)

        # Reemplazar None con 0 solo para graficar (barra invisible); guardar originales para etiquetas
        py_plot = [v if v is not None else 0 for v in tiempos_python]
        java_plot = [v if v is not None else 0 for v in tiempos_java]

        bars1 = ax.bar(x - width/2, py_plot, width, label='Python',
                       color=colores['Python'], edgecolor='black', linewidth=0.5)
        bars2 = ax.bar(x + width/2, java_plot, width, label='Java',
                       color=colores['Java'], edgecolor='black', linewidth=0.5)

        for bar, val in zip(bars1, tiempos_python):
            if val is not None and val > 0:
                ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(),
                        f'{val:.4f}', ha='center', va='bottom', fontsize=8, fontweight='bold')

        for bar, val in zip(bars2, tiempos_java):
            if val is not None and val > 0:
                ax.text(bar.get_x() + bar.get_width()/2, bar.get_height(),
                        f'{val:.4f}', ha='center', va='bottom', fontsize=8, fontweight='bold')

        ax.set_xlabel('Algoritmo', fontsize=10)
        ax.set_ylabel('Tiempo (ms)', fontsize=10)
        ax.set_title(f'{tamano.upper()} elementos', fontsize=12, fontweight='bold')
        ax.set_xticks(x)
        ax.set_xticklabels(orden_algoritmos, rotation=15, ha='right', fontsize=9)
        ax.legend(loc='upper left', fontsize=9)
        ax.grid(axis='y', alpha=0.3)

        # Escala logarítmica si hay diferencias de más de un orden de magnitud
        valores_validos = [v for v in tiempos_python + tiempos_java if v is not None and v > 0]
        if valores_validos and max(valores_validos) / min(valores_validos) > 10:
            ax.set_yscale('log')

    plt.tight_layout()

    output_png = 'resultados/grafico_busqueda.png'
    plt.savefig(output_png, dpi=150, bbox_inches='tight', facecolor='white')
    print(f"\nGráfico guardado en: {output_png}")

    output_pdf = 'resultados/grafico_busqueda.pdf'
    plt.savefig(output_pdf, format='pdf', bbox_inches='tight', facecolor='white')
    print(f"PDF guardado en: {output_pdf}")

    plt.close()


def mostrar_resumen(df):
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
    print("GENERADOR DE GRÁFICOS - BÚSQUEDA")
    print("="*60 + "\n")

    os.makedirs('resultados', exist_ok=True)

    df = cargar_resultados()

    if df.empty:
        print("No hay datos para graficar.")
        return

    mostrar_resumen(df)
    print("\nGenerando gráfico...")
    crear_grafico(df)

    print("\n¡Listo!")


if __name__ == '__main__':
    main()
