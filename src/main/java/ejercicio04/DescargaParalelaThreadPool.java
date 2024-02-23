package org.infantaelena.ies.psp.UD3.ejercicios_voluntarios.ejercicio04;

import org.infantaelena.ies.psp.UD3.ejercicios_voluntarios.ejercicio03.HiloDescarga;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DescargaParalelaThreadPool {

    private Path archivo;
    private Path carpeta;
    private int hilosSimultaneos;

    public DescargaParalelaThreadPool(Path archivo, Path carpeta) {
        setArchivo(archivo);
        setCarpeta(carpeta);
    }

    public DescargaParalelaThreadPool(Path archivo, Path carpeta, int hilosSimultaneos) {
        this(archivo, carpeta);
        setHilosSimultaneos(hilosSimultaneos);
    }

    public Path getArchivo() {
        return archivo;
    }

    public void setArchivo(Path archivo) throws IllegalArgumentException{
        if (archivo == null || !Files.isRegularFile(archivo)){
            throw new IllegalArgumentException("Archivo inválido");
        }
        this.archivo = archivo;
    }

    public Path getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(Path carpeta) throws IllegalArgumentException{
        try {
            Files.createDirectories(carpeta);
        } catch (IOException e) {
            System.err.println("No ha sido posible crear los directorios");
            throw new RuntimeException(e);
        }
        if (!Files.isDirectory(carpeta)){
            throw new IllegalArgumentException("Carpeta inválida");
        }
        this.carpeta = carpeta;
    }

    public int getHilosSimultaneos() {
        return hilosSimultaneos;
    }

    public void setHilosSimultaneos(int hilosSimultaneos) {
        if (hilosSimultaneos < 1){
            throw new IllegalArgumentException("Número de descargas inválido");
        }
        this.hilosSimultaneos = hilosSimultaneos;
    }

    public static void main(String[] args) {

        DescargaParalelaThreadPool programa = null;

        if (args.length == 2){

            try {
                programa = new DescargaParalelaThreadPool(Paths.get(args[0]), Paths.get(args[1]));
            }catch (IllegalArgumentException e){
                System.err.println(e.getMessage());
                throw new RuntimeException();
            }

            try {
                programa.procesarDescargasSinLimite();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }

        } else if (args.length == 3) {

            try {
                programa = new DescargaParalelaThreadPool(Paths.get(args[0]), Paths.get(args[1]), Integer.parseInt(args[2]));
            }catch (IllegalArgumentException e){
                System.err.println(e.getMessage());
                throw new RuntimeException();
            }

            try {
                programa.procesarDescargas();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void procesarDescargas() throws IOException{

        ExecutorService executorService = Executors.newFixedThreadPool(getHilosSimultaneos());

        try (BufferedReader br = Files.newBufferedReader(getArchivo())){
            String line = null;
            while ((line = br.readLine()) != null){
                executorService.execute(new HiloDescarga(line, getCarpeta()));
            }
        }finally {
            executorService.shutdown();
        }
    }

    private void procesarDescargasSinLimite() throws IOException{
        try (BufferedReader br = Files.newBufferedReader(getArchivo())){
            String line = null;
            while ((line = br.readLine()) != null){
                new HiloDescarga(line, getCarpeta()).start();
            }
        }
    }
}