# Taller de Ordenamiento y Búsqueda

**Autores:**
- Daniel Stiven Perez Cordoba
- Cristhian Eduardo Osorio Restrepo

**Presentado a:** Ing. Carlos Andrés Flórez Villarraga Mg

---

## Propósito del taller

El objetivo de este taller es verificar experimentalmente que la teoría de complejidad algorítmica coincide con la realidad. En el curso se aprende que un algoritmo O(n log n) es mejor que uno O(n²), pero eso es matemática abstracta. El taller obliga a medirlo con datos reales y verificar si los números respaldan la teoría.

El segundo propósito es comparar un lenguaje compilado (Java) contra uno interpretado (Python) implementando la misma lógica, para entender cómo la capa de ejecución afecta el rendimiento independientemente del algoritmo elegido.

---

## Punto 1 — Análisis de Algoritmos de Ordenamiento

### Propósito

Se busca medir y comparar el tiempo de ejecución real de cinco algoritmos de ordenamiento sobre tres tamaños de arreglo distintos (10.000, 100.000 y 1.000.000 elementos), implementados en Java y Python, para verificar que su comportamiento experimental corresponde a su complejidad teórica.

Los algoritmos evaluados son:

| Algoritmo | Complejidad promedio | Complejidad peor caso |
|---|---|---|
| Shaker Sort (Cocktail Sort) | O(n²) | O(n²) |
| Dual-Pivot QuickSort | O(n log n) | O(n²) |
| Heap Sort | O(n log n) | O(n log n) |
| Merge Sort | O(n log n) | O(n log n) |
| Radix Sort | O(nk) | O(nk) |

### Qué hace el código

1. **Generación única de datos:** Los números aleatorios de 8 dígitos se generan una sola vez y se guardan en archivos de texto (`data_10k.txt`, `data_100k.txt`, `data_1m.txt`). En ejecuciones posteriores, el programa lee desde esos archivos sin regenerarlos. Esto garantiza que todos los algoritmos se evalúan con exactamente el mismo conjunto de datos, haciendo la comparación válida.

2. **Medición aislada:** El tiempo se mide únicamente alrededor de la ejecución del algoritmo, utilizando `System.nanoTime()` en Java y `time.perf_counter()` en Python. No se incluye el tiempo de lectura de archivos ni de copia del arreglo.

3. **Copia del arreglo antes de cada prueba:** Antes de ejecutar cada algoritmo se hace una copia del arreglo original. Sin esto, el segundo algoritmo recibiría datos ya ordenados y sus tiempos serían artificialmente bajos.

4. **Exportación de resultados:** Los tiempos se almacenan y exportan en formato CSV y JSON para su análisis, y se genera un gráfico de barras comparativo con los valores visibles sobre cada barra.

### Resultados obtenidos

| Algoritmo | Java 10k | Java 100k | Java 1M | Python 10k | Python 100k | Python 1M |
|---|---|---|---|---|---|---|
| HeapSort | 6.30 ms | 26.14 ms | 333.59 ms | 54.53 ms | 658.75 ms | 26,127 ms |
| MergeSort | 4.71 ms | 28.05 ms | 281.36 ms | 40.86 ms | 502.44 ms | 18,355 ms |
| RadixSort | 5.87 ms | 22.51 ms | 115.20 ms | 31.12 ms | 1,018 ms | 24,685 ms |
| DualPivotQuickSort | 4.72 ms | 16.17 ms | 177.07 ms | 19.90 ms | 621.52 ms | 15,759 ms |
| CocktailSort | 130.27 ms | 18,605 ms | TIMEOUT | 5,450 ms | TIMEOUT | TIMEOUT |

### Análisis de resultados

**CocktailSort confirma O(n²):** Al multiplicar el tamaño por 10 (de 10k a 100k), el tiempo en Java pasó de 130 ms a 18.605 ms, un factor de ×143. Matemáticamente O(n²) predice ×100. Que sea mayor indica que el peor caso se activa con mayor frecuencia en datos completamente aleatorios. A 1M de elementos el algoritmo no termina dentro del límite de 5 minutos, lo que confirma que O(n²) es inviable a gran escala.

**Los algoritmos O(n log n) escalan correctamente:** Al crecer de 10k a 1M (×100 en datos), los tiempos en Java crecieron aproximadamente ×53 para HeapSort, ×60 para MergeSort y ×37 para DualPivotQuickSort. Matemáticamente, O(n log n) a ×100 datos debería crecer alrededor de ×115. El hecho de que los valores sean menores se explica por las optimizaciones del JIT de Java, que compila el código caliente en instrucciones nativas durante la ejecución.

**RadixSort — el más rápido en Java a 1M, pero no en Python:** En Java lidera con 115 ms porque su complejidad O(nk) con k=8 dígitos fijos funciona en la práctica como O(n). Sin embargo, en Python es el más lento a 1M con 24.685 ms, porque hace muchas operaciones de memoria (arreglos auxiliares, conteo de dígitos) y Python paga overhead por cada acceso a lista con tipado dinámico. Esto demuestra que el algoritmo teóricamente más eficiente no siempre gana en todos los contextos de ejecución.

**DualPivotQuickSort — el más consistente:** Es el más rápido o segundo más rápido en casi todas las combinaciones. Su ventaja es la localidad de caché: opera in-place sin arreglos auxiliares y sus dos pivotes reducen las comparaciones promedio respecto al QuickSort clásico.

**Java vs Python:** El mismo algoritmo sobre el mismo arreglo muestra que Java es entre 65 y 214 veces más rápido que Python dependiendo del algoritmo. La razón es que Python interpreta cada instrucción en tiempo de ejecución con tipado dinámico, mientras que Java compila a bytecode y el JIT lo convierte a instrucciones nativas optimizadas para el procesador.

---

## Punto 2 — Análisis de Algoritmos de Búsqueda

### Propósito

Se busca medir y comparar el tiempo de ejecución de tres algoritmos de búsqueda sobre arreglos ordenados de 10.000, 100.000 y 1.000.000 elementos, implementados en Java y Python, verificando que su comportamiento experimental corresponde a su complejidad teórica.

Los algoritmos evaluados son:

| Algoritmo | Complejidad |
|---|---|
| Búsqueda Binaria | O(log n) |
| Búsqueda Ternaria | O(log₃ n) |
| Búsqueda por Saltos (Jump Search) | O(√n) |

### Qué hace el código

1. **Carga y ordenamiento previo:** Se cargan los mismos datos del Punto 1 y se ordenan con el método nativo del lenguaje (`Arrays.sort()` en Java, `list.sort()` en Python). Este tiempo de ordenamiento no se mide, ya que lo que se evalúa es únicamente el algoritmo de búsqueda.

2. **Selección del objetivo:** Se busca el elemento central del arreglo ordenado. Esta elección garantiza que el elemento existe y no favorece ni perjudica a ningún algoritmo (no está al inicio ni al final del arreglo).

3. **Medición aislada:** El tiempo se mide únicamente alrededor de la llamada al algoritmo de búsqueda, con la misma precisión que en el Punto 1.

4. **Exportación de resultados:** Los tiempos se exportan en CSV y JSON y se genera un gráfico de barras comparativo.

### Resultados obtenidos

| Algoritmo | Java 10k | Java 100k | Java 1M | Python 10k | Python 100k | Python 1M |
|---|---|---|---|---|---|---|
| BinarySearch | 0.69 ms | 0.006 ms | 0.006 ms | 0.016 ms | 0.026 ms | 0.024 ms |
| TernarySearch | 0.70 ms | 0.008 ms | 0.007 ms | 0.020 ms | 0.023 ms | 0.028 ms |
| JumpSearch | 1.08 ms | 0.187 ms | 0.763 ms | 0.037 ms | 0.178 ms | 0.315 ms |

### Análisis de resultados

**JVM warmup en Java a 10k:** Los tiempos de Java a 10k son anómalamente altos comparados con 100k y 1M en BinarySearch y TernarySearch. Esto se debe al calentamiento de la JVM: la primera ejecución es más lenta porque el JIT aún no ha compilado ese código. Es un comportamiento real del entorno de ejecución que confirma que los benchmarks con una sola iteración en Java no reflejan el rendimiento sostenido.

**BinarySearch y TernarySearch son equivalentes en la práctica:** BinarySearch realiza log₂(1.000.000) ≈ 20 comparaciones. TernarySearch realiza log₃(1.000.000) ≈ 13 iteraciones, pero hace 2 comparaciones por iteración, totalizando alrededor de 26. La diferencia matemática es marginal y el overhead de la recursión de TernarySearch cancela su ventaja teórica. BinarySearch es preferible por ser iterativo, más simple y con menor uso del stack.

**JumpSearch confirma O(√n):** Al crecer el arreglo de 10k a 1M (×100 en datos), el tiempo en Python creció de 0.037 ms a 0.315 ms, un factor de ×8.5. Para O(√n), √100 = 10, por lo que se esperaría ×10. El resultado experimental está muy cerca de la predicción teórica, lo que valida el análisis de complejidad. JumpSearch es útil en sistemas donde el acceso al arreglo tiene un costo fijo (como búsqueda en bloques de disco), pero en memoria RAM pierde siempre contra BinarySearch.

**Java vs Python en búsqueda:** A diferencia del ordenamiento, en búsqueda los tiempos son tan pequeños (microsegundos) que la diferencia entre Java y Python es menos pronunciada. A 1M, Python (0.024 ms) y Java (0.006 ms) ejecutan BinarySearch en tiempos del mismo orden de magnitud, con Java siendo aproximadamente ×4 más rápido. Esto demuestra que para operaciones de muy baja duración, el overhead de Python es menos significativo que en operaciones de larga duración como el ordenamiento.

---

## Estructura del proyecto

```
TALLER-DE-ORDENAMIENTO-Y-B-SQUEDA/
│
├── utilsJava/                      # Algoritmos implementados en Java
│   ├── HeapSort.java
│   ├── MergeSort.java
│   ├── RadixSort.java
│   ├── DualPivotQuickSort.java
│   ├── CocktailSort.java
│   ├── BinarySearch.java
│   ├── TernarySearch.java
│   └── JumpSearch.java
│
├── utilsPython/                    # Algoritmos implementados en Python
│   ├── heap_sort.py
│   ├── merge_sort.py
│   ├── radix_sort.py
│   ├── dual_pivot_sort.py
│   ├── cocktail_sort.py
│   ├── binary_search.py
│   ├── ternary_search.py
│   └── jump_search.py
│
├── BenchmarkJava.java              # Benchmark de ordenamiento en Java
├── BenchmarkBusquedaJava.java      # Benchmark de búsqueda en Java
├── benchmark_python.py             # Benchmark de ordenamiento en Python
├── benchmark_busqueda_python.py    # Benchmark de búsqueda en Python
├── generar_grafico.py              # Gráfico comparativo de ordenamiento
├── generar_grafico_busqueda.py     # Gráfico comparativo de búsqueda
│
├── datos/                          # Archivos de datos generados
│   ├── GeneradorDatos.java
│   ├── generador_datos.py
│   ├── data_10k.txt
│   ├── data_100k.txt
│   └── data_1m.txt
│
└── resultados/                     # Resultados exportados
    ├── benchmark_java.csv / .json
    ├── benchmark_ordenamiento.csv / .json
    ├── benchmark_busqueda_java.csv / .json
    ├── benchmark_busqueda_python.csv / .json
    ├── grafico_comparativo.png / .pdf
    └── grafico_busqueda.png / .pdf
```

## Cómo ejecutar

### Generar datos (solo la primera vez)
```bash
cd datos && javac GeneradorDatos.java && java GeneradorDatos && cd ..
# o con Python:
python3 datos/generador_datos.py
```

### Punto 1 — Ordenamiento
```bash
# Java
javac utilsJava/*.java BenchmarkJava.java && java BenchmarkJava

# Python
python3 benchmark_python.py

# Gráfico comparativo
python3 generar_grafico.py
```

### Punto 2 — Búsqueda
```bash
# Java
javac utilsJava/*.java BenchmarkBusquedaJava.java && java BenchmarkBusquedaJava

# Python
python3 benchmark_busqueda_python.py

# Gráfico comparativo
python3 generar_grafico_busqueda.py
```

## Fuentes

- Heap Sort: https://www.geeksforgeeks.org/heap-sort/
- Merge Sort: https://www.geeksforgeeks.org/merge-sort/
- Radix Sort: https://www.geeksforgeeks.org/radix-sort/
- Dual Pivot QuickSort: https://www.geeksforgeeks.org/dual-pivot-quicksort/
- Cocktail Sort: https://www.geeksforgeeks.org/cocktail-sort/
- Binary Search: https://www.geeksforgeeks.org/binary-search/
- Ternary Search: https://www.geeksforgeeks.org/ternary-search/
- Jump Search: https://www.geeksforgeeks.org/jump-search/
