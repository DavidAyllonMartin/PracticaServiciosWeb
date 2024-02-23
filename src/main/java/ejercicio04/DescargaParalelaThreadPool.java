package ejercicio04;

import ejercicio03.HiloDescarga;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase para gestionar la descarga de archivos en paralelo usando un pool de hilos.
 */
public class DescargaParalelaThreadPool {

    private Path archivo; // Archivo que contiene las URLs a descargar.
    private Path carpeta; // Carpeta destino donde se guardarán los archivos descargados.
    private int hilosSimultaneos; // Número de hilos simultáneos para las descargas.

    /**
     * Constructor que inicializa la clase con el archivo de URLs y la carpeta destino.
     *
     * @param archivo Archivo con URLs a descargar.
     * @param carpeta Carpeta destino para guardar los archivos descargados.
     */
    public DescargaParalelaThreadPool(Path archivo, Path carpeta) {
        setArchivo(archivo);
        setCarpeta(carpeta);
    }

    /**
     * Constructor que adicionalmente permite especificar el número de hilos simultáneos para las descargas.
     *
     * @param archivo Archivo con URLs a descargar.
     * @param carpeta Carpeta destino para guardar los archivos descargados.
     * @param hilosSimultaneos Número de hilos simultáneos para las descargas.
     */
    public DescargaParalelaThreadPool(Path archivo, Path carpeta, int hilosSimultaneos) {
        this(archivo, carpeta);
        setHilosSimultaneos(hilosSimultaneos);
    }

    // Getters y setters con validaciones para asegurar que los argumentos son válidos.

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

    /**
     * Método principal que ejecuta el programa.
     *
     * @param args Argumentos de la línea de comando.
     */
    public static void main(String[] args) {

        DescargaParalelaThreadPool programa = null;

        // Procesamiento de argumentos y ejecución de las descargas según el número de argumentos.

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

    /**
     * Procesa las descargas utilizando un pool de hilos con un número fijo de hilos.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
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

    /**
     * Procesa las descargas sin un límite específico de hilos, iniciando un nuevo hilo por cada URL.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private void procesarDescargasSinLimite() throws IOException{
        try (BufferedReader br = Files.newBufferedReader(getArchivo())){
            String line = null;
            while ((line = br.readLine()) != null){
                new HiloDescarga(line, getCarpeta()).start();
            }
        }
    }
}
