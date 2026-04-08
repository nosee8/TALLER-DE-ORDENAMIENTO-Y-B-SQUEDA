import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GeneradorDatos {
    
    public static void generarDatos(int n, String archivo) throws IOException {
        Random rand = new Random();
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
        
        for (int i = 0; i < n; i++) {
            int numero = 10000000 + rand.nextInt(90000000);
            writer.write(numero + "\n");
        }
        
        writer.close();
    }
    
    public static int[] cargarDatos(String archivo) throws IOException {
        List<Integer> lista = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        
        String linea;
        while ((linea = reader.readLine()) != null) {
            lista.add(Integer.parseInt(linea.trim()));
        }
        reader.close();
        
        return lista.stream().mapToInt(Integer::intValue).toArray();
    }
    
    public static void main(String[] args) throws IOException {
        File carpeta = new File("datos");
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }
        
        int[][] tamanos = {{10000, "data_10k.txt"}, {100000, "data_100k.txt"}, {1000000, "data_1m.txt"}};
        
        for (int[] par : tamanos) {
            String archivo = "datos/" + par[1];
            File f = new File(archivo);
            if (!f.exists()) {
                System.out.println("Generando " + par[1] + " (" + par[0] + " elementos)...");
                generarDatos(par[0], archivo);
            } else {
                System.out.println(par[1] + " ya existe, omitiendo...");
            }
        }
    }
}
