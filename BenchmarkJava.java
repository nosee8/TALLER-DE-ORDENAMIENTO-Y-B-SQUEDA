/**
 * Benchmark para algoritmos de ordenamiento en Java.
 * Mide el tiempo de ejecución y exporta resultados a CSV/JSON.
 *
 * Referencias:
 * - HeapSort:          https://www.geeksforgeeks.org/heap-sort/
 * - MergeSort:         https://www.geeksforgeeks.org/merge-sort/
 * - RadixSort:         https://www.geeksforgeeks.org/radix-sort/
 * - DualPivotQuickSort: https://www.geeksforgeeks.org/dual-pivot-quicksort/
 * - CocktailSort:      https://www.geeksforgeeks.org/cocktail-sort/
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import utilsJava.*;

public class BenchmarkJava {

    private static final long TIMEOUT_SECONDS = 300;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static int[] cargarDatos(String archivo) throws IOException {
        List<Integer> datos = Files.readAllLines(Paths.get(archivo))
            .stream()
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        int[] arr = new int[datos.size()];
        for (int i = 0; i < datos.size(); i++)
            arr[i] = datos.get(i);
        return arr;
    }

    static int[] copiar(int[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    static double medirTiempo(Runnable algoritmo, String nombre) {
        Future<Double> future = executor.submit(() -> {
            long start = System.nanoTime();
            algoritmo.run();
            return (System.nanoTime() - start) / 1_000_000.0;
        });

        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("  ⚠️ " + nombre + ": EXCEDIÓ " + TIMEOUT_SECONDS + "s");
            return -1;
        } catch (Exception e) {
            System.out.println("  ❌ " + nombre + ": Error - " + e.getMessage());
            return -2;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("============================================================");
        System.out.println("BENCHMARK ALGORITMOS DE ORDENAMIENTO - JAVA");
        System.out.println("============================================================");

        Map<String, String> tamanos = new LinkedHashMap<>();
        tamanos.put("10k",  "datos/data_10k.txt");
        tamanos.put("100k", "datos/data_100k.txt");
        tamanos.put("1m",   "datos/data_1m.txt");

        List<String[]> resultados = new ArrayList<>();
        resultados.add(new String[]{"algoritmo", "tamano", "tiempo_ms", "lenguaje"});

        // Warmup: forzar compilación JIT antes de medir cualquier tamaño
        System.out.println("\n[Warmup JIT...]");
        int[] warmupArr = new int[5000];
        for (int i = 0; i < warmupArr.length; i++) warmupArr[i] = (int)(Math.random() * 100000);
        for (int w = 0; w < 3; w++) {
            HeapSort.heapSort(Arrays.copyOf(warmupArr, warmupArr.length));
            MergeSort.mergeSort(Arrays.copyOf(warmupArr, warmupArr.length), 0, warmupArr.length - 1);
            RadixSort.radixSort(Arrays.copyOf(warmupArr, warmupArr.length));
            DualPivotQuickSort.dualPivotQuickSort(Arrays.copyOf(warmupArr, warmupArr.length), 0, warmupArr.length - 1);
            CocktailSort.cocktailSort(Arrays.copyOf(warmupArr, warmupArr.length));
        }
        System.out.println("[Warmup completado]\n");

        for (Map.Entry<String, String> entry : tamanos.entrySet()) {
            String tamano = entry.getKey();
            int[] datos = cargarDatos(entry.getValue());
            System.out.println("\n=== " + tamano + " elementos ===");
            System.out.println("Datos cargados: " + datos.length);

            registrar(resultados, tamano, "HeapSort",
                medirTiempo(() -> HeapSort.heapSort(copiar(datos)), "HeapSort"));

            registrar(resultados, tamano, "MergeSort",
                medirTiempo(() -> MergeSort.mergeSort(copiar(datos), 0, datos.length - 1), "MergeSort"));

            registrar(resultados, tamano, "RadixSort",
                medirTiempo(() -> RadixSort.radixSort(copiar(datos)), "RadixSort"));

            registrar(resultados, tamano, "DualPivotQuickSort",
                medirTiempo(() -> DualPivotQuickSort.dualPivotQuickSort(copiar(datos), 0, datos.length - 1), "DualPivotQuickSort"));

            registrar(resultados, tamano, "CocktailSort",
                medirTiempo(() -> CocktailSort.cocktailSort(copiar(datos)), "CocktailSort"));
        }

        exportarCSV(resultados, "resultados/benchmark_java.csv");
        exportarJSON(resultados, "resultados/benchmark_java.json");

        System.out.println("\n=== Benchmark completado ===");
        executor.shutdown();
    }

    static void registrar(List<String[]> resultados, String tamano, String algo, double tiempo) {
        if (tiempo > 0) {
            String t = String.format(Locale.US, "%.2f", tiempo);
            System.out.println("  " + algo + ": " + t + " ms");
            resultados.add(new String[]{algo, tamano, t, "Java"});
        }
    }

    static void exportarCSV(List<String[]> resultados, String ruta) throws IOException {
        Files.write(Paths.get(ruta),
            resultados.stream().map(r -> String.join(",", r)).collect(Collectors.toList()));
        System.out.println("\nCSV exportado: " + ruta);
    }

    static void exportarJSON(List<String[]> resultados, String ruta) throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 1; i < resultados.size(); i++) {
            String[] r = resultados.get(i);
            json.append("  {\"algoritmo\": \"").append(r[0])
                .append("\", \"tamano\": \"").append(r[1])
                .append("\", \"tiempo_ms\": ").append(r[2])
                .append(", \"lenguaje\": \"").append(r[3]).append("\"}");
            if (i < resultados.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        Files.write(Paths.get(ruta), json.toString().getBytes());
        System.out.println("JSON exportado: " + ruta);
    }
}
