package org.infantaelena.ies.psp.UD3.ejercicios_voluntarios.ejercicio03;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DescargaParalela {
    public static void main(String[] args) {

        if (args.length == 2){

            try {
                Path archivo = Paths.get(args[0]);
                Path carpeta = Paths.get(args[1]);
                Files.createDirectories(carpeta);
                if (!Files.isRegularFile(archivo) || !Files.isDirectory(carpeta)){
                    throw new IllegalArgumentException();
                }


                try (BufferedReader br = Files.newBufferedReader(archivo)){
                    String line = null;
                    while ((line = br.readLine()) != null){
                        new HiloDescarga(line, carpeta).start();
                    }
                }

            } catch (InvalidPathException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
