import java.io.*;
import java.lang.reflect.Array;
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
        
        int [] tamanos = {10000, 100000, 1000000};
        String [] archivos = {"data_10k.txt", "data_100k.txt", "data_1m.txt"};
        for (int i=0; i<tamanos.length; i++) {
            String archivo = "datos/" + archivos[i];
            File f = new File(archivo);
            if (!f.exists()) {
                System.out.println("Generando " + archivos[i] + " (" + tamanos[i] + " elementos)...");
                generarDatos(tamanos[i], archivo);
            } else {
                System.out.println(archivos[i] + " ya existe, omitiendo...");
            }
        }
    }
}
