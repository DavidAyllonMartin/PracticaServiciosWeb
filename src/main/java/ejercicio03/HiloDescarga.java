package org.infantaelena.ies.psp.UD3.ejercicios_voluntarios.ejercicio03;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiloDescarga extends Thread{
    private String url;
    private Path carpeta;

    public HiloDescarga(String url, Path carpeta) {
        this.url = url;
        this.carpeta = carpeta;
    }

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

    public void descargarArchivo() throws IOException {
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHoraActualStr = fechaHoraActual.format(formatter);

        URL url = new URL(getUrl());

        String nombreArchivoOriginal = Paths.get(url.getPath()).getFileName().toString();
        String nuevoNombreArchivo = fechaHoraActualStr + "_" + nombreArchivoOriginal;
        Path rutaCompleta = getCarpeta().resolve(nuevoNombreArchivo);

        Files.copy(url.openStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(url + " descargado correctamente");
    }

    @Override
    public void run() {
        try {
            descargarArchivo();
        } catch (IOException e) {
            System.err.println("Error al descargar el archivo");
        }
    }
}
