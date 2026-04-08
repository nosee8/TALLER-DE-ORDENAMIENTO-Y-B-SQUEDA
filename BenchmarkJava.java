/**
 * Benchmark para algoritmos de ordenamiento en Java.
 * Mide el tiempo de ejecución y exporta resultados a CSV/JSON.
 * 
 * Referencias:
 * - HeapSort: https://www.geeksforgeeks.org/heap-sort/
 * - MergeSort: https://www.geeksforgeeks.org/merge-sort/
 * - RadixSort: https://www.geeksforgeeks.org/radix-sort/
 * - DualPivotQuickSort: https://www.geeksforgeeks.org/dual-pivot-quicksort/
 * - CocktailSort: https://www.geeksforgeeks.org/cocktail-sort/
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class BenchmarkJava {
    
    // ==================== ALGORITMOS DE ORDENAMIENTO ====================
    
    // HeapSort - https://www.geeksforgeeks.org/heap-sort/
    static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[largest])
            largest = l;
        if (r < n && arr[r] > arr[largest])
            largest = r;

        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }

    static void heapSort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            heapify(arr, i, 0);
        }
    }

    // MergeSort - https://www.geeksforgeeks.org/merge-sort/
    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    // RadixSort - https://www.geeksforgeeks.org/radix-sort/
    static int getMax(int[] arr, int n) {
        int mx = arr[0];
        for (int i = 1; i < n; i++)
            if (arr[i] > mx)
                mx = arr[i];
        return mx;
    }

    static void countSort(int[] arr, int n, int exp) {
        int[] output = new int[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        for (int i = 0; i < n; i++)
            count[(arr[i] / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / exp) % 10] - 1] = arr[i];
            count[(arr[i] / exp) % 10]--;
        }

        for (int i = 0; i < n; i++)
            arr[i] = output[i];
    }

    static void radixSort(int[] arr) {
        int n = arr.length;
        int m = getMax(arr, n);
        for (int exp = 1; m / exp > 0; exp *= 10)
            countSort(arr, n, exp);
    }

    // DualPivotQuickSort - https://www.geeksforgeeks.org/dual-pivot-quicksort/
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static int[] partition(int[] arr, int low, int high) {
        if (arr[low] > arr[high])
            swap(arr, low, high);

        int j = low + 1;
        int g = high - 1, k = low + 1;
        int p = arr[low], q = arr[high];

        while (k <= g) {
            if (arr[k] < p) {
                swap(arr, k, j);
                j++;
            } else if (arr[k] >= q) {
                while (arr[g] > q && k < g)
                    g--;
                swap(arr, k, g);
                g--;
                if (arr[k] < p) {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;
        swap(arr, low, j);
        swap(arr, high, g);
        return new int[]{j, g};
    }

    static void dualPivotQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int[] piv = partition(arr, low, high);
            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }

    // CocktailSort (Shaker Sort) - https://www.geeksforgeeks.org/cocktail-sort/
    static void cocktailSort(int[] arr) {
        boolean swapped = true;
        int start = 0;
        int end = arr.length;

        while (swapped) {
            swapped = false;
            for (int i = start; i < end - 1; ++i) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            if (swapped == false)
                break;
            swapped = false;
            end = end - 1;
            for (int i = end - 1; i >= start; i--) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            start = start + 1;
        }
    }

    // ==================== BENCHMARK ====================

    static int[] copiarArreglo(int[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    static int[] cargarDatos(String archivo) throws IOException {
        List<Integer> datos = Files.readAllLines(Paths.get(archivo))
            .stream()
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        
        int[] arr = new int[datos.size()];
        for (int i = 0; i < datos.size(); i++) {
            arr[i] = datos.get(i);
        }
        return arr;
    }

    static double medirTiempo(Runnable algoritmo) {
        long start = System.nanoTime();
        algoritmo.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000.0; // milisegundos
    }

    public static void main(String[] args) throws IOException {
        System.out.println("============================================================");
        System.out.println("BENCHMARK ALGORITMOS DE ORDENAMIENTO - JAVA");
        System.out.println("============================================================");

        Map<String, String> tamanos = new LinkedHashMap<>();
        tamanos.put("10k", "datos/data_10k.txt");
        tamanos.put("100k", "datos/data_100k.txt");
        tamanos.put("1m", "datos/data_1m.txt");

        List<String[]> resultados = new ArrayList<>();
        resultados.add(new String[]{"algoritmo", "tamano", "tiempo_ms", "lenguaje"});

        for (Map.Entry<String, String> entry : tamanos.entrySet()) {
            String tamanoNombre = entry.getKey();
            String archivo = entry.getValue();
            
            System.out.println("\n=== " + tamanoNombre + " elementos ===");
            
            int[] datos = cargarDatos(archivo);
            System.out.println("Datos cargados: " + datos.length + " elementos");

            // HeapSort
            double tiempo = medirTiempo(() -> heapSort(copiarArreglo(datos)));
            System.out.println("  HeapSort: " + String.format("%.2f", tiempo) + " ms");
            resultados.add(new String[]{"HeapSort", tamanoNombre, String.format("%.2f", tiempo), "Java"});

            // MergeSort
            tiempo = medirTiempo(() -> mergeSort(copiarArreglo(datos), 0, datos.length - 1));
            System.out.println("  MergeSort: " + String.format("%.2f", tiempo) + " ms");
            resultados.add(new String[]{"MergeSort", tamanoNombre, String.format("%.2f", tiempo), "Java"});

            // RadixSort
            tiempo = medirTiempo(() -> radixSort(copiarArreglo(datos)));
            System.out.println("  RadixSort: " + String.format("%.2f", tiempo) + " ms");
            resultados.add(new String[]{"RadixSort", tamanoNombre, String.format("%.2f", tiempo), "Java"});

            // DualPivotQuickSort
            tiempo = medirTiempo(() -> dualPivotQuickSort(copiarArreglo(datos), 0, datos.length - 1));
            System.out.println("  DualPivotQuickSort: " + String.format("%.2f", tiempo) + " ms");
            resultados.add(new String[]{"DualPivotQuickSort", tamanoNombre, String.format("%.2f", tiempo), "Java"});

            // CocktailSort
            tiempo = medirTiempo(() -> cocktailSort(copiarArreglo(datos)));
            System.out.println("  CocktailSort: " + String.format("%.2f", tiempo) + " ms");
            resultados.add(new String[]{"CocktailSort", tamanoNombre, String.format("%.2f", tiempo), "Java"});
        }

        // Exportar a CSV
        Files.write(Paths.get("resultados/benchmark_java.csv"), 
            resultados.stream().map(r -> String.join(",", r)).collect(Collectors.toList()));
        System.out.println("\nCSV exportado: resultados/benchmark_java.csv");

        // Exportar a JSON
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 1; i < resultados.size(); i++) {
            String[] r = resultados.get(i);
            json.append("  {\n");
            json.append("    \"algoritmo\": \"").append(r[0]).append("\",\n");
            json.append("    \"tamano\": \"").append(r[1]).append("\",\n");
            json.append("    \"tiempo_ms\": ").append(r[2]).append(",\n");
            json.append("    \"lenguaje\": \"").append(r[3]).append("\"\n");
            json.append("  }");
            if (i < resultados.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        Files.write(Paths.get("resultados/benchmark_java.json"), json.toString().getBytes());
        System.out.println("JSON exportado: resultados/benchmark_java.json");

        System.out.println("\n=== Benchmark completado ===");
    }
}
