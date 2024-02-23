package ejercicio03;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que extiende de Thread para descargar un archivo desde una URL dada y guardarla en una carpeta específica.
 */
public class HiloDescarga extends Thread{
    private String url; // URL del archivo a descargar.
    private Path carpeta; // Carpeta destino donde se guardará el archivo descargado.

    /**
     * Constructor para crear una instancia de HiloDescarga con la URL y la carpeta de destino especificadas.
     *
     * @param url La URL del archivo a descargar.
     * @param carpeta La carpeta destino donde se guardará el archivo.
     */
    public HiloDescarga(String url, Path carpeta) {
        this.url = url;
        this.carpeta = carpeta;
    }

    // Getters y setters para la URL y la carpeta de destino.

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Path getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(Path carpeta) {
        this.carpeta = carpeta;
    }

    /**
     * Método para descargar el archivo de la URL especificada y guardarlo en la carpeta destino.
     *
     * @throws IOException Si ocurre un error durante la descarga o el guardado del archivo.
     */
    public void descargarArchivo() throws IOException {
        // Obtener la fecha y hora actual para usarla en el nombre del archivo descargado.
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHoraActualStr = fechaHoraActual.format(formatter);

        // Crear un objeto URL y determinar el nombre del archivo a descargar.
        URL url = new URL(getUrl());
        String nombreArchivoOriginal = Paths.get(url.getPath()).getFileName().toString();
        String nuevoNombreArchivo = fechaHoraActualStr + "_" + nombreArchivoOriginal;
        Path rutaCompleta = getCarpeta().resolve(nuevoNombreArchivo);

        // Realizar la descarga del archivo y guardarlo en la ruta completa.
        Files.copy(url.openStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(url + " descargado correctamente");
    }

    /**
     * Método sobreescrito de la clase Thread que se ejecuta cuando el hilo inicia.
     * Intenta descargar el archivo y maneja posibles excepciones de E/S.
     */
    @Override
    public void run() {
        try {
            descargarArchivo();
        } catch (IOException e) {
            System.err.println("Error al descargar el archivo: " + e.getMessage());
        }
    }
}
