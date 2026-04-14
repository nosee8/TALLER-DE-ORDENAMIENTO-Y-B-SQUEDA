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

2. **Medición aislada:** El tiempo se mide únicamente alrededor de la ejecución del algoritmo. En Java se usa `System.nanoTime()` dentro del hilo de medición. En Python se usa `time.perf_counter()` iniciado **después** de copiar el arreglo y **antes** de llamar al algoritmo. No se incluye el tiempo de lectura de archivos ni de copia del arreglo.

3. **Copia del arreglo antes de cada prueba:** Antes de ejecutar cada algoritmo se hace una copia del arreglo original. Sin esto, el segundo algoritmo recibiría datos ya ordenados y sus tiempos serían artificialmente bajos.

4. **Warmup de la JVM:** El benchmark Java ejecuta tres pasadas de calentamiento sobre un arreglo de 5.000 elementos con todos los algoritmos antes de iniciar la medición real. Esto fuerza la compilación JIT del bytecode a código nativo, garantizando que los tiempos medidos reflejen el rendimiento sostenido de la JVM y no el costo de compilación de la primera ejecución.

5. **Exportación de resultados:** Los tiempos se almacenan y exportan en formato CSV y JSON para su análisis, y se genera un gráfico de barras comparativo con los valores visibles sobre cada barra.

### Resultados obtenidos

| Algoritmo | Java 10k | Java 100k | Java 1M | Python 10k | Python 100k | Python 1M |
|---|---|---|---|---|---|---|
| HeapSort | 3.46 ms | 29.63 ms | 323.17 ms | 55.77 ms | 822.69 ms | 30,856 ms |
| MergeSort | 2.77 ms | 29.35 ms | 507.99 ms | 42.68 ms | 629.54 ms | 21,010 ms |
| RadixSort | 2.46 ms | 12.07 ms | 153.13 ms | 42.41 ms | 1,252.57 ms | 22,987 ms |
| DualPivotQuickSort | 1.54 ms | 15.28 ms | 170.43 ms | 22.94 ms | 729.51 ms | 14,700 ms |
| CocktailSort | 174.09 ms | 20,466 ms | TIMEOUT | 5,787 ms | TIMEOUT | TIMEOUT |

> TIMEOUT = el algoritmo excedió el límite de 5 minutos (300 s). Comportamiento esperado para O(n²) a gran escala.

### Análisis de resultados

#### Ratios de crecimiento observados vs teóricos

Para validar la complejidad, se compara cuánto crece el tiempo cuando el tamaño se multiplica por 10.

**O(n log n) predice:** ×12,5 al pasar de 10k a 100k, ×12 al pasar de 100k a 1M.  
**O(nk) predice:** ×10 en ambos saltos (k es constante = 8 dígitos).  
**O(n²) predice:** ×100 en ambos saltos.

| Algoritmo | Java 100k→1M | Predicho | Java 10k→100k | Predicho |
|---|---|---|---|---|
| HeapSort | ×10.9 | ×12 ✓ | ×8.6 | ×12.5 |
| MergeSort | ×17.3 | ×12 ⚠ | ×10.6 | ×12.5 ✓ |
| DualPivotQuickSort | ×11.2 | ×12 ✓ | ×9.9 | ×12.5 ✓ |
| RadixSort | ×12.7 | ×10 ✓ | ×4.9 | ×10 ⚠ |
| CocktailSort | TIMEOUT | ×100 | ×117.6 | ×100 ✓ |

| Algoritmo | Python 100k→1M | Predicho | Python 10k→100k | Predicho |
|---|---|---|---|---|
| HeapSort | ×37.5 | ×12 ⚠ | ×14.8 | ×12.5 |
| MergeSort | ×33.4 | ×12 ⚠ | ×14.8 | ×12.5 |
| DualPivotQuickSort | ×20.2 | ×12 ⚠ | ×31.8 | ×12.5 |
| RadixSort | ×18.4 | ×10 ⚠ | ×29.5 | ×10 |
| CocktailSort | TIMEOUT | ×100 | — | ×100 |

---

#### CocktailSort confirma O(n²)

En Java, al multiplicar el tamaño por 10 (de 10k a 100k), el tiempo pasó de 174 ms a 20.466 ms: un factor ×117. La predicción de O(n²) es ×100. La diferencia se debe a que el arreglo es aleatorio, lo que activa el peor caso del algoritmo con mayor frecuencia de la que anticipa el análisis promedio. A 1M de elementos el algoritmo no termina en 5 minutos, confirmando que O(n²) es inviable a gran escala.

En Python, el timeout ocurre ya desde 100k, dado que Python paga un overhead de interpretación por cada intercambio que Java no tiene.

---

#### Java: los ratios 10k→100k son levemente menores de lo esperado

Incluso con warmup, el JIT de la JVM sigue refinando sus optimizaciones durante la ejecución real. El warmup con 5.000 elementos no activa todas las optimizaciones que aplica cuando ve 10.000 o 100.000 elementos en el benchmark real. Por eso los tiempos de 10k resultan algo más bajos de lo que predice la teoría pura, haciendo que el ratio 10k→100k sea inferior al esperado. Los ratios **100k→1M son los más confiables** porque a ese punto el JIT ya compiló todo el código caliente al nivel más alto de optimización (compilador C2 de la JVM).

DualPivotQuickSort es la excepción positiva: su ratio 10k→100k (×9.9) es el más cercano al teórico (×12.5), porque opera completamente in-place y el JIT optimiza sus bucles de partición muy rápido.

---

#### RadixSort Java: crecimiento sublineal de 10k a 100k

RadixSort 10k→100k creció solo ×4.9 cuando la teoría predice ×10. Esto no es un error: el counting sort interno tiene bucles secuenciales muy simples (`count[digit]++`, recorrido lineal del arreglo). El JIT aplica instrucciones SIMD (vectorización) de forma más agresiva cuando el bucle tiene 100.000 iteraciones que cuando tiene 10.000. El resultado es que el código es más eficiente por elemento a 100k que a 10k. En la ventana 100k→1M el ratio es ×12.7, ya cerca del ×10 teórico, porque el JIT ya alcanzó su máximo nivel de optimización desde los 100k.

---

#### Python: crecimiento super-lineal de 100k a 1M

Todos los algoritmos Python muestran ratios mucho mayores de lo esperado al pasar de 100k a 1M. La causa es estructural, no de código:

- En Python, cada entero en una lista es un objeto en el heap (~28 bytes de overhead por número).
- Un arreglo de 1M elementos en Python ocupa ~28 MB en RAM.
- El mismo arreglo en Java (`int[]`) ocupa solo ~4 MB.

A 1M elementos, el footprint de memoria de Python es 7 veces mayor que el de Java. Esto causa **cache misses severos**: los accesos al arreglo ya no caben en L2 (típicamente 256 KB–2 MB) y cada acceso puede ir a L3 o RAM. Los algoritmos como HeapSort o RadixSort, que acceden al arreglo de forma no secuencial, son especialmente sensibles. El tiempo extra no es del algoritmo, es del sistema de memoria.

RadixSort es el más afectado (×29.5 de 10k a 100k) porque en cada pasada crea un arreglo auxiliar `output` de tamaño n, duplicando el footprint de memoria.

---

#### MergeSort Java a 1M: ratio ×17.3 (mayor al esperado)

MergeSort es el único algoritmo de la lista que requiere O(n) de memoria adicional (el arreglo `output` para el merge). A 1M elementos en Java, esa memoria auxiliar es ~4 MB adicionales. Cuando Java necesita más memoria de la disponible en el heap actual, dispara el **Garbage Collector (GC)**, que pausa la ejecución para liberar memoria. Esa pausa se suma al tiempo medido aunque no es tiempo del algoritmo. El ratio ×17.3 (versus ×12 esperado) refleja una o más pausas de GC durante la medición.

---

#### Java vs Python: ¿cuánto más rápido es Java?

| Algoritmo | Factor Java/Python a 10k | Factor Java/Python a 100k | Factor Java/Python a 1M |
|---|---|---|---|
| HeapSort | ×16 | ×28 | ×95 |
| MergeSort | ×15 | ×21 | ×41 |
| RadixSort | ×17 | ×104 | ×150 |
| DualPivotQuickSort | ×15 | ×48 | ×86 |

La ventaja de Java crece con el tamaño del arreglo porque el JIT tiene más oportunidades de optimizar el código caliente, mientras Python paga el overhead de interpretación y cache misses en cada operación adicional. A 1M elementos, RadixSort en Java es 150 veces más rápido que en Python: misma lógica, mismo algoritmo, resultado radicalmente distinto por la capa de ejecución.

---

## Punto 2 — Análisis de Algoritmos de Búsqueda

### Propósito

Se busca medir y comparar el tiempo de ejecución de tres algoritmos de búsqueda sobre arreglos ordenados de 10.000, 100.000 y 1.000.000 elementos, implementados en Java y Python, verificando que su comportamiento experimental corresponde a su complejidad teórica.

Los algoritmos evaluados son:

| Algoritmo | Complejidad |
|---|---|
| Búsqueda Binaria | O(log₂ n) |
| Búsqueda Ternaria | O(log₃ n) |
| Búsqueda por Saltos (Jump Search) | O(√n) |

### Qué hace el código

1. **Carga y ordenamiento previo:** Se cargan los mismos datos del Punto 1 y se ordenan con el método nativo del lenguaje (`Arrays.sort()` en Java, `list.sort()` en Python). Este tiempo de ordenamiento no se mide, ya que lo que se evalúa es únicamente el algoritmo de búsqueda.

2. **Selección del objetivo:** Se busca el elemento central del arreglo ordenado. Esta elección garantiza que el elemento existe y no favorece ni perjudica a ningún algoritmo (no está al inicio ni al final del arreglo).

3. **Medición con 1.000 repeticiones promediadas:** Cada algoritmo se ejecuta 1.000 veces sobre el mismo arreglo y se reporta el promedio. Los algoritmos de búsqueda terminan en microsegundos: una sola medición es dominada por el ruido del sistema operativo (scheduling, interrupciones). Promediar 1.000 ejecuciones elimina ese ruido y produce tiempos representativos del algoritmo real.

4. **Warmup de la JVM:** Igual que en el Punto 1, se ejecutan pasadas de calentamiento antes de iniciar la medición para estabilizar la compilación JIT.

5. **Exportación de resultados:** Los tiempos se exportan en CSV y JSON y se genera un gráfico de barras comparativo.

### Resultados obtenidos

| Algoritmo | Java 10k | Java 100k | Java 1M | Python 10k | Python 100k | Python 1M |
|---|---|---|---|---|---|---|
| BinarySearch | 0.0009 ms | 0.0003 ms | 0.0002 ms | 0.0032 ms | 0.0039 ms | 0.0050 ms |
| TernarySearch | 0.0013 ms | 0.0002 ms | 0.0002 ms | 0.0045 ms | 0.0059 ms | 0.0057 ms |
| JumpSearch | 0.0040 ms | 0.0072 ms | 0.0092 ms | 0.0144 ms | 0.0508 ms | 0.1663 ms |

> Todos los tiempos son promedios de 1.000 ejecuciones consecutivas sobre el mismo arreglo.

### Análisis de resultados

#### JumpSearch confirma O(√n) con precisión

O(√n) predice que al multiplicar n por 10, el tiempo crece ×√10 = ×3.16.

| Ventana | Python real | Predicho | Error |
|---|---|---|---|
| 10k → 100k | ×3.53 | ×3.16 | +11.7% |
| 100k → 1M | ×3.27 | ×3.16 | +3.5% |

Los datos Python replican casi perfectamente la complejidad teórica. Esto valida tanto la implementación del algoritmo como la metodología de medición.

En Java, JumpSearch también crece correctamente (10k < 100k < 1M), pero los ratios son menores que los de Python porque el JIT aplica optimizaciones más agresivas a los bucles del algoritmo cuando tiene más iteraciones (más calor = más optimización), reduciendo el tiempo por elemento a medida que n crece.

---

#### BinarySearch y TernarySearch son O(log n) ≈ casi constante

O(log₂ n) predice que al pasar de 10k a 1M (×100 en datos), el tiempo crece solo ×log₂(1.000.000)/log₂(10.000) = ×1.50.

| Algoritmo | Python 10k→1M real | Predicho |
|---|---|---|
| BinarySearch | ×1.56 (0.0032→0.0050 ms) | ×1.50 ✓ |
| TernarySearch | ×1.27 (0.0045→0.0057 ms) | ×1.50 ✓ |

Los tiempos Python muestran el crecimiento esperado: muy lento, consistente con O(log n). Pasar de buscar en 10.000 a buscar en 1.000.000 elementos agrega apenas 13 comparaciones adicionales (log₂(10k)≈13 vs log₂(1M)≈20).

---

#### Java Binary/Ternary: los tiempos decrecen con n — por qué es esperado

En Java, BinarySearch y TernarySearch muestran tiempos que disminuyen al crecer n (10k: 0.0009 ms, 100k: 0.0003 ms, 1M: 0.0002 ms). Esto parece contradecir O(log n), pero tiene una explicación directa del comportamiento del JIT:

Con 1.000 repeticiones de medición, el total de iteraciones del bucle de búsqueda dentro del hilo es:
- n=10k: 1.000 × log₂(10.000) ≈ 13.300 iteraciones de bucle
- n=1M:  1.000 × log₂(1.000.000) ≈ 19.900 iteraciones de bucle

A mayor número de iteraciones del bucle, el compilador JIT C2 de la JVM aplica optimizaciones más agresivas (vectorización SIMD, eliminación de verificaciones de límites). A n=1M, el JIT genera código nativo 3–4× más eficiente por iteración. Como O(log n) crece apenas ×1.5 entre 10k y 1M, pero el JIT mejora ×3–4×, el tiempo observado decrece. Los tiempos de Python, que no tiene JIT, muestran el crecimiento algorítmico puro y son la referencia correcta de O(log n) en este caso.

---

#### BinarySearch vs TernarySearch: cuál es mejor en la práctica

BinarySearch realiza log₂(1.000.000) ≈ 20 comparaciones por búsqueda.  
TernarySearch realiza log₃(1.000.000) ≈ 13 iteraciones, pero ejecuta **2 comparaciones** por iteración, totalizando ~26 comparaciones.

TernarySearch hace **más trabajo** que BinarySearch en la práctica. Además, la versión aquí implementada es **recursiva**, lo que agrega overhead de llamadas al stack. BinarySearch es iterativo, más simple y siempre más rápido. Los datos lo confirman en Python: TernarySearch es ~40% más lento que BinarySearch a 10k (0.0045 ms vs 0.0032 ms).

---

#### JumpSearch vs BinarySearch: cuándo tiene sentido

JumpSearch es O(√n) vs O(log n) de BinarySearch. Para n=1M:
- JumpSearch Python: 0.1663 ms
- BinarySearch Python: 0.0050 ms

BinarySearch es **33 veces más rápido** a 1M elementos. JumpSearch solo tiene sentido cuando el "salto" de bloque en bloque es más barato que el acceso aleatorio (por ejemplo, búsqueda en bloques de disco o cintas magnéticas, donde retroceder es costoso). En RAM, BinarySearch siempre gana.

---

#### Java vs Python en búsqueda

| Algoritmo | Factor Java/Python a 1M |
|---|---|
| BinarySearch | ×25 |
| TernarySearch | ×29 |
| JumpSearch | ×18 |

La diferencia entre Java y Python es mucho menor que en el ordenamiento (×18–29 vs ×40–150). Esto es coherente: cuando el algoritmo hace muy pocas operaciones (log₂ de 1M = 20 comparaciones), el overhead fijo de inicialización de Python se amortiza poco y su diferencia con Java se reduce. El cuello de botella deja de ser el lenguaje y pasa a ser el propio algoritmo.

---

## Punto 3 — Análisis de Casos Específicos

El objetivo de este punto es diferente a los anteriores. No se mide el tiempo experimentalmente: se **calcula analíticamente** cuántas operaciones ejecuta cada algoritmo en función del tamaño de la entrada `n`. A ese cálculo se le llama `T(n)`, y de él se deriva la notación Big-O.

### Cómo se lee el cálculo de T(n)

Cada línea de código tiene un costo. El principio es contar cuántas veces se ejecuta cada instrucción:

- Una asignación simple (`int x = 0`) cuesta **1** sin importar el valor.
- Una condición de ciclo `i < n` se evalúa **n + 1** veces si el ciclo corre n iteraciones: n veces que da verdadero y 1 vez que da falso y sale.
- El incremento `i++` se ejecuta exactamente **n veces** (una por cada iteración que sí ocurre).
- Una instrucción **dentro** del ciclo se ejecuta **n veces** si el ciclo corre n iteraciones.
- Si hay un ciclo dentro de otro, el tiempo total del ciclo interno se **multiplica** por las iteraciones del externo.

Al final se suman todos los costos, se simplifica la expresión, y la potencia más alta de `n` determina la clase de complejidad.

---

### Algoritmo 1 — Ordenar lista enlazada de 0s, 1s y 2s

**Fuente:** https://www.geeksforgeeks.org/sort-a-linked-list-of-0s-1s-or-2s/

#### Qué problema resuelve

Se tiene una lista enlazada cuyos nodos contienen únicamente los valores 0, 1 o 2 en cualquier orden. El objetivo es dejarla ordenada: todos los 0s primero, luego los 1s, luego los 2s.

#### Cómo funciona el algoritmo

La solución es un algoritmo de **dos pasadas** sobre la lista:

**Primera pasada — contar:**
Se recorre la lista completa de principio a fin. Por cada nodo se mira su valor (0, 1 o 2) y se incrementa el contador correspondiente en un arreglo `cnt` de tres posiciones. Al terminar, `cnt[0]` dice cuántos ceros hay, `cnt[1]` cuántos unos y `cnt[2]` cuántos doses.

**Segunda pasada — sobreescribir:**
Se vuelve a recorrer la lista desde el principio. Se usa una variable `idx` que empieza en 0. Por cada nodo se escribe el valor `idx` en él. Cuando `cnt[idx]` llega a cero, significa que ya se colocaron todos los valores de ese tipo, y se avanza `idx` al siguiente (0 → 1 → 2). Se repite hasta recorrer todos los nodos.

> El punto clave es que **no se mueven los nodos** de lugar. Solo se sobreescriben los valores dentro de ellos. La lista mantiene su estructura física exacta, y la ordenación se logra mediante escritura directa guiada por los conteos.

**Ejemplo paso a paso con la lista:** `1 → 1 → 2 → 1 → 0`

Primera pasada:
- Nodo con 1: `cnt = [0, 1, 0]`
- Nodo con 1: `cnt = [0, 2, 0]`
- Nodo con 2: `cnt = [0, 2, 1]`
- Nodo con 1: `cnt = [0, 3, 1]`
- Nodo con 0: `cnt = [1, 3, 1]`

Segunda pasada con `idx = 0`:
- `cnt[0] = 1` → escribir 0 en el primer nodo. `cnt = [0, 3, 1]`
- `cnt[0] = 0` → avanzar `idx = 1`
- `cnt[1] = 3` → escribir 1, escribir 1, escribir 1. `cnt = [0, 0, 1]`
- `cnt[1] = 0` → avanzar `idx = 2`
- `cnt[2] = 1` → escribir 2. `cnt = [0, 0, 0]`

Resultado: `0 → 1 → 1 → 1 → 2`

#### Cálculo de T(n) — donde n = número de nodos en la lista

```
sortList(Node head):

int[] cnt = {0, 0, 0}              -- 3   (tres asignaciones, una por cada posición)
Node ptr = head                    -- 1

--- While 1 (primera pasada: contar) ---

Condición ptr != null              -- n + 1
    (el ciclo tiene n iteraciones: n veces verdadero, 1 vez falso al salir)

    cnt[ptr.data] += 1             -- n   (una vez por cada nodo)
    ptr = ptr.next                 -- n   (una vez por cada nodo)

Subtotal While 1 = (n+1) + n + n = 3n + 1

--- Después del ciclo 1 ---

int idx = 0                        -- 1
ptr = head                         -- 1

--- While 2 (segunda pasada: sobreescribir) ---

Condición ptr != null              -- n + 1
    (ptr avanza exactamente n veces, una por cada nodo)

    if (cnt[idx] == 0)             -- n   (se evalúa en cada iteración)
    idx += 1                       -- 2   (idx solo puede ir 0→1 y 1→2, máximo 2 veces)
    ptr.data = idx                 -- n   (se ejecuta cuando el else es verdadero, n veces en total)
    cnt[idx] -= 1                  -- n
    ptr = ptr.next                 -- n

Subtotal While 2 = (n+1) + n + 2 + n + n + n = 5n + 3

--- Suma total ---

T(n) = 3 + 1 + (3n + 1) + 1 + 1 + (5n + 3)
T(n) = 8n + 10
```

**Por qué `idx += 1` cuesta solo 2 y no n:**
En el ciclo, cada iteración hace UNA de dos cosas: o avanza `ptr` (rama `else`) o incrementa `idx` (rama `if`). `ptr` avanza n veces en total porque la lista tiene n nodos. `idx` solo puede cambiar de 0 a 1 y de 1 a 2, máximo dos cambios, independientemente de cuán grande sea n. Eso lo hace un costo constante.

**T(n) = 8n + 10 → O(n)**

---

### Algoritmo 2 — Ordenar arreglo según el orden de otro arreglo (Relative Sort)

**Fuente:** https://www.geeksforgeeks.org/sort-array-according-order-defined-another-array/

#### Qué problema resuelve

Se tienen dos arreglos: `a1` es el arreglo principal a ordenar, y `a2` es un arreglo de referencia que define el orden deseado. El resultado debe ser que los elementos de `a1` que aparecen en `a2` queden ordenados según la posición en que aparecen en `a2`, y los elementos de `a1` que no aparecen en `a2` se ubiquen al final en orden ascendente.

**Ejemplo:**
- `a1 = [2, 1, 2, 3, 4]`
- `a2 = [2, 1, 2]` (el 2 aparece dos veces en a2, lo que indica que debe ir primero y dos veces)
- Resultado: `[2, 2, 1, 3, 4]`
  - El 2 aparece dos veces en a2, entonces se colocan todos los 2s de a1 primero.
  - El 1 aparece en a2, entonces va después.
  - El 3 y 4 no están en a2, van al final ordenados ascendentemente.

#### Cómo funciona el algoritmo

**Paso 1 — Contar frecuencias:**
Se recorre `a1` completamente y se usa un `HashMap` para registrar cuántas veces aparece cada número. Si `a1 = [2, 1, 2, 3, 4]`, el mapa queda `{2→2, 1→1, 3→1, 4→1}`.

**Paso 2 — Colocar los elementos de a2 en orden:**
Se recorre `a2`. Por cada elemento que aparece en el mapa, se copia al inicio de `a1` tantas veces como su frecuencia indique y luego se elimina del mapa. Esto garantiza que el orden de `a2` se respete exactamente.

**Paso 3 — Recolectar y ordenar los sobrantes:**
Los elementos que quedaron en el mapa (los que no estaban en `a2`) se recolectan en una lista auxiliar `remaining`, se ordenan con `Collections.sort` y se añaden al final de `a1`.

> El `HashMap` es la pieza central: permite saber en tiempo constante cuántas veces aparece cada elemento, evitando búsquedas lineales repetidas.

#### Cálculo de T(n) — donde n = tamaño de `a1`, m = tamaño de `a2`

```
relativeSort(int[] a1, int[] a2):

int m = a1.length                  -- 1
int n = a2.length                  -- 1
Map freq = new HashMap<>()         -- 1

--- For 1 (contar frecuencias de a1) ---

i = 0                              -- 1
i < n  (usando n = a1.length)     -- n + 1
i++                                -- n
freq.put(a1[i], ...)              -- n
    (cada put en HashMap es O(1) amortizado)

Subtotal For 1 = 1 + (n+1) + n + n = 3n + 3

int index = 0                      -- 1

--- For 2 (colocar elementos de a2, m iteraciones externas) ---

i = 0                              -- 1
i < m                              -- m + 1
i++                                -- m
freq.remove(a2[i])                -- m

    --- While interno ---
    (en total, el while coloca exactamente los elementos de a1 que están en a2)
    (sea c el número de esos elementos: c ≤ n)

    freq.getOrDefault(...) > 0    -- c + m
        (c veces verdadero, m veces falso: una salida por iteración externa)
    a1[index++] = a2[i]          -- c
    freq.put(a2[i], ...)         -- c

Subtotal For 2 = 1 + (m+1) + m + m + (c+m) + c + c = 3c + 4m + 2

--- Recolectar sobrantes (r = n - c elementos que no estaban en a2) ---

List remaining = new ArrayList<>() -- 1
for (entry : freq.entrySet())      -- r + 1   (r verdaderos + 1 falso al salir)
    remaining.add(entry.getKey())  -- r

Subtotal collect = 2r + 2

--- Ordenar sobrantes ---

Collections.sort(remaining)        -- r·log(r)
    (usa TimSort internamente, complejidad O(r log r))

--- Añadir sobrantes al final de a1 ---

for (num : remaining)              -- r + 1
    a1[index++] = num             -- r

Subtotal append = 2r + 1

--- Suma total ---

T(n, m) = (1+1+1) + (3n+3) + 1 + (3c+4m+2) + (2r+2) + r·log(r) + (2r+1)
T(n, m) = 3n + 4m + r·log(r) + 4r + 3c + 11
```

Dado que `c + r = n` (todo elemento de a1 es colocado o es sobrante), podemos reemplazar `c = n - r`:

```
T(n, m) = 3n + 4m + r·log(r) + 4r + 3(n - r) + 11
T(n, m) = 6n + 4m + r·log(r) + r + 11
```

**Análisis por casos:**

**Peor caso** (r = n): ningún elemento de `a1` aparece en `a2`. El paso de colocar no hace nada útil, y todos los elementos de `a1` caen en `remaining`. El sort domina:
```
T(n) = 6n + 4m + n·log(n) + n + 11
T(n) ≈ n·log(n)
```
**O(n log n)**

**Mejor caso** (r = 0): todos los elementos de `a1` están en `a2`. No hay sobrantes, no hay sort. Solo el recorrido y la colocación:
```
T(n) = 6n + 4m + 0 + 0 + 11
T(n) ≈ n + m
```
**O(n + m)**

---

### Algoritmo 3 — Ordenar arreglo con valores en rango [0, n²-1]

**Fuente:** https://www.geeksforgeeks.org/sort-n-numbers-range-0-n2-1-linear-time/

#### Qué problema resuelve

Se tiene un arreglo de `n` elementos donde cada valor puede estar entre 0 y n²-1 (por ejemplo, si el arreglo tiene 7 elementos, los valores pueden ir de 0 a 48). El objetivo es ordenarlo en tiempo O(n), es decir, más rápido que los algoritmos de comparación tradicionales que son O(n log n).

#### Por qué es posible ordenar más rápido que O(n log n)

Los algoritmos como MergeSort u HeapSort comparan elementos entre sí y por eso tienen un límite teórico de O(n log n). Pero si se conoce el rango de los valores, se puede usar **Counting Sort**, que no compara elementos sino que los cuenta y los recoloca. Counting Sort es O(n + k) donde k es el rango de valores. El problema aquí es que el rango es n², así que un Counting Sort directo sería O(n + n²) = O(n²), que es peor.

La solución es aplicar **Radix Sort en base n**: representar cada número como si tuviera dos "dígitos" en base n. Cualquier número entre 0 y n²-1 puede representarse como `d1 * n + d0` donde `d0 = número % n` y `d1 = número / n`. Cada dígito está en el rango [0, n-1]. Luego se aplica Counting Sort dos veces: una para el dígito menos significativo (`d0`) y otra para el más significativo (`d1`). Cada Counting Sort es O(n), y como se hacen exactamente dos, el total es O(n).

#### Cómo funciona el algoritmo

El método `sort` simplemente llama a `countSort` dos veces:
- Primera llamada con `exp = 1`: ordena según el dígito menos significativo (`(arr[i] / 1) % n`).
- Segunda llamada con `exp = n`: ordena según el dígito más significativo (`(arr[i] / n) % n`).

Cada llamada a `countSort` ejecuta cuatro pasos internos:

**Paso 1 — Inicializar el arreglo de conteos a cero:**
Se crea un arreglo `count` de tamaño `n` con todos sus valores en 0.

**Paso 2 — Contar ocurrencias:**
Se recorre `arr` y por cada elemento se calcula su "dígito" en la base actual usando `(arr[i] / exp) % n`. Se incrementa `count` en esa posición.

**Paso 3 — Prefijo suma (prefix sum):**
Se transforma `count` para que cada posición indique no cuántos elementos tienen ese dígito, sino cuántos elementos tienen un dígito menor o igual. Esto convierte los conteos en posiciones finales dentro del arreglo de salida.

**Paso 4 — Construir el arreglo de salida:**
Se recorre `arr` de atrás hacia adelante. Para cada elemento se consulta su posición final en `count`, se coloca en esa posición en `output` y se decrementa el contador. Recorrer de atrás hacia adelante garantiza que el ordenamiento sea **estable** (los elementos con el mismo dígito mantienen su orden relativo, lo cual es esencial para que el segundo paso del Radix Sort sea correcto).

**Paso 5 — Copiar output de vuelta a arr.**

#### Cálculo de T(n) — análisis de `countSort` primero

```
countSort(int arr[], int n, int exp):

int output[] = new int[n]          -- n   (crear e inicializar n celdas)
int i                              -- 1
int count[] = new int[n]           -- n   (crear e inicializar n celdas)

--- Loop 0 (inicializar count a 0 explícitamente) ---

i = 0                              -- 1
i < n                              -- n + 1
i++                                -- n
count[i] = 0                       -- n

Subtotal Loop 0 = 1 + (n+1) + n + n = 3n + 2

--- Loop 1 (contar ocurrencias) ---

i = 0                              -- 1
i < n                              -- n + 1
i++                                -- n
count[(arr[i]/exp)%n]++           -- n

Subtotal Loop 1 = 3n + 2

--- Loop 2 (prefijo suma) ---

i = 1                              -- 1
    (el ciclo empieza en 1, no en 0, porque acumula desde el anterior)
i < n                              -- n
    (desde i=1 hasta i=n-1: son n-1 iteraciones verdaderas + 1 falsa = n evaluaciones)
i++                                -- n - 1
count[i] += count[i-1]            -- n - 1

Subtotal Loop 2 = 1 + n + (n-1) + (n-1) = 3n - 1

--- Loop 3 (construir output, recorre de n-1 hasta 0) ---

i = n - 1                          -- 1
i >= 0                             -- n + 1
    (desde i=n-1 hasta i=0: n iteraciones verdaderas + 1 falsa = n+1 evaluaciones)
i--                                -- n
output[count[(arr[i]/exp)%n]-1] = arr[i]   -- n
count[(arr[i]/exp)%n]--           -- n

Subtotal Loop 3 = 1 + (n+1) + n + n + n = 4n + 2

--- Loop 4 (copiar output a arr) ---

i = 0                              -- 1
i < n                              -- n + 1
i++                                -- n
arr[i] = output[i]                -- n

Subtotal Loop 4 = 3n + 2

--- Suma total de countSort ---

T_countSort(n) = (n + 1 + n) + (3n+2) + (3n+2) + (3n-1) + (4n+2) + (3n+2)
              = (2n+1) + (3n+2) + (3n+2) + (3n-1) + (4n+2) + (3n+2)
              = (2+3+3+3+4+3)n + (1+2+2-1+2+2)
T_countSort(n) = 18n + 8
```

Ahora el método `sort` llama a `countSort` dos veces:

```
sort(int arr[], int n):

countSort(arr, n, 1)               -- T_countSort(n) = 18n + 8
countSort(arr, n, n)               -- T_countSort(n) = 18n + 8

T_sort(n) = (18n + 8) + (18n + 8)
T_sort(n) = 36n + 16
```

**Por qué el número de llamadas es fijo en 2 y no en log(n):**
En un Radix Sort genérico se hacen tantas pasadas como dígitos tenga el número más grande. Aquí los valores están acotados a [0, n²-1]. En base n, cualquier número en ese rango tiene **exactamente 2 dígitos** (d1 y d0). Por eso siempre se hacen exactamente 2 llamadas a `countSort`, sin importar cuán grande sea n. Ese "2" es una constante que desaparece en la notación Big-O.

**T(n) = 36n + 16 → O(n)**

---

## Estructura del repositorio

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
# Java (incluye warmup JIT automático)
javac utilsJava/*.java BenchmarkJava.java && java BenchmarkJava

# Python (~11 min por los timeouts de CocktailSort en 100k y 1M)
python3 benchmark_python.py

# Gráfico comparativo
python3 generar_grafico.py
```

### Punto 2 — Búsqueda
```bash
# Java (incluye warmup JIT y promedia 1.000 repeticiones)
javac utilsJava/*.java BenchmarkBusquedaJava.java && java BenchmarkBusquedaJava

# Python (promedia 1.000 repeticiones)
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
