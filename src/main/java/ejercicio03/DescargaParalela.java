package ejercicio03;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase principal para gestionar descargas paralelas de archivos desde URLs especificadas en un archivo.
 */
public class DescargaParalela {
    public static void main(String[] args) {

        // Verifica que se proporcionen exactamente dos argumentos: el archivo con las URLs y la carpeta de destino.
        if (args.length == 2){

            try {
                // Intenta crear objetos Path tanto para el archivo de URLs como para la carpeta de destino.
                Path archivo = Paths.get(args[0]);
                Path carpeta = Paths.get(args[1]);

                // Crea la carpeta de destino si no existe.
                Files.createDirectories(carpeta);

                // Verifica que el archivo de URLs sea un archivo regular y que la carpeta de destino exista.
                if (!Files.isRegularFile(archivo) || !Files.isDirectory(carpeta)){
                    throw new IllegalArgumentException("El archivo de URLs no existe o la carpeta de destino no es válida.");
                }

                // Abre el archivo de URLs para leerlo línea por línea.
                try (BufferedReader br = Files.newBufferedReader(archivo)){
                    String line = null;
                    while ((line = br.readLine()) != null){
                        // Para cada URL leída, inicia un nuevo hilo para realizar la descarga.
                        new HiloDescarga(line, carpeta).start();
                    }
                }

            } catch (InvalidPathException e) {
                // Captura excepciones relacionadas con rutas de archivo inválidas.
                throw new RuntimeException("La ruta del archivo o de la carpeta no es válida.", e);
            } catch (IOException e) {
                // Captura excepciones de E/S, como errores al leer el archivo o crear directorios.
                throw new RuntimeException("Error de E/S al leer el archivo de URLs o al crear la carpeta de destino.", e);
            }
        } else {
            // Si no se proporcionan exactamente dos argumentos, muestra un mensaje de error.
            System.err.println("Uso: DescargaParalela <archivo_urls> <carpeta_destino>");
        }
    }
}
