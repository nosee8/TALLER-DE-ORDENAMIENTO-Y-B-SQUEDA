/**
 * Benchmark para algoritmos de búsqueda en Java.
 * Mide el tiempo de ejecución y exporta resultados a CSV/JSON.
 *
 * El arreglo se ordena previamente (sin medir ese tiempo).
 * El objetivo de búsqueda es el elemento central del arreglo ordenado.
 *
 * Referencias:
 * - BinarySearch:  https://www.geeksforgeeks.org/binary-search/
 * - TernarySearch: https://www.geeksforgeeks.org/ternary-search/
 * - JumpSearch:    https://www.geeksforgeeks.org/jump-search/
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import utilsJava.*;

public class BenchmarkBusquedaJava {

    private static final long TIMEOUT_SECONDS = 60;
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

    // Repeticiones para promediar y reducir ruido de medición (tiempos en microsegundos)
    private static final int REPETICIONES = 1000;

    static double medirTiempo(Runnable algoritmo, String nombre) {
        Future<Double> future = executor.submit(() -> {
            long start = System.nanoTime();
            for (int i = 0; i < REPETICIONES; i++) algoritmo.run();
            return (System.nanoTime() - start) / 1_000_000.0 / REPETICIONES;
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
        System.out.println("BENCHMARK ALGORITMOS DE BÚSQUEDA - JAVA");
        System.out.println("============================================================");

        Map<String, String> tamanos = new LinkedHashMap<>();
        tamanos.put("10k",  "datos/data_10k.txt");
        tamanos.put("100k", "datos/data_100k.txt");
        tamanos.put("1m",   "datos/data_1m.txt");

        List<String[]> resultados = new ArrayList<>();
        resultados.add(new String[]{"algoritmo", "tamano", "tiempo_ms", "lenguaje"});

        // Warmup: forzar compilación JIT antes de medir cualquier tamaño
        System.out.println("\n[Warmup JIT...]");
        int[] warmupArr = new int[1000];
        for (int i = 0; i < warmupArr.length; i++) warmupArr[i] = i;
        int warmupTarget = warmupArr[warmupArr.length / 2];
        for (int w = 0; w < 5; w++) {
            BinarySearch.binarySearch(warmupArr, warmupTarget);
            TernarySearch.ternarySearch(warmupArr, 0, warmupArr.length - 1, warmupTarget);
            JumpSearch.jumpSearch(warmupArr, warmupTarget);
        }
        System.out.println("[Warmup completado]\n");

        for (Map.Entry<String, String> entry : tamanos.entrySet()) {
            String tamano = entry.getKey();

            // Cargar y ordenar (el tiempo de ordenamiento NO se mide)
            int[] datos = cargarDatos(entry.getValue());
            Arrays.sort(datos);
            System.out.println("\n=== " + tamano + " elementos ===");
            System.out.println("Datos cargados y ordenados: " + datos.length);

            // Objetivo: elemento central (garantizado que existe)
            final int objetivo = datos[datos.length / 2];
            System.out.println("Objetivo de búsqueda: " + objetivo);

            registrar(resultados, tamano, "BinarySearch",
                medirTiempo(() -> BinarySearch.binarySearch(datos, objetivo), "BinarySearch"));

            registrar(resultados, tamano, "TernarySearch",
                medirTiempo(() -> TernarySearch.ternarySearch(datos, 0, datos.length - 1, objetivo), "TernarySearch"));

            registrar(resultados, tamano, "JumpSearch",
                medirTiempo(() -> JumpSearch.jumpSearch(datos, objetivo), "JumpSearch"));
        }

        exportarCSV(resultados, "resultados/benchmark_busqueda_java.csv");
        exportarJSON(resultados, "resultados/benchmark_busqueda_java.json");

        System.out.println("\n=== Benchmark completado ===");
        executor.shutdown();
    }

    static void registrar(List<String[]> resultados, String tamano, String algo, double tiempo) {
        if (tiempo > 0) {
            String t = String.format(Locale.US, "%.4f", tiempo);
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
