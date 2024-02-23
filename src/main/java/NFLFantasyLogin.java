package fantasy_ddbb.utils;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class NFLFantasyLogin {

    public static void main(String[] args) {
        try {
            CookieHandler.setDefault(new CookieManager());

            String loginUrl = "https://fantasy.nfl.com/account/sign-in";

            String username = "";
            String password = "";

            login(loginUrl, username, password);

            String dataUrl = "https://fantasy.nfl.com/league/54538/transactions?transactionType=drop";
            String responseData = fetchData(dataUrl);
            Files.write(Path.of("src/main/resources/scripts/html"), responseData.getBytes());
            fetchAndSaveHTML(dataUrl, "src/main/resources/scripts");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void login(String loginUrl, String username, String password) throws Exception {

        URL url = new URL(loginUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Crear los datos del formulario de inicio de sesión
        String postData = "username=" + username + "&password=" + password;
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Obtener las cookies de la respuesta y almacenarlas
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                String[] parts = cookie.split(";");
                String cookieValue = parts[0];
                // Aquí puedes almacenar las cookies según sea necesario
            }
        }

        // Leer la respuesta del servidor
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Respuesta del inicio de sesión: " + response.toString());
        }

        // Cerrar la conexión
        connection.disconnect();
    }

    private static String fetchData(String dataUrl) throws Exception {
        // Establecer la conexión
        URL url = new URL(dataUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Obtener las cookies almacenadas y establecerlas en la solicitud
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Cookie");
        if (cookiesHeader != null) {
            connection.setRequestProperty("Cookie", String.join(";", cookiesHeader));
        }

        // Leer la respuesta del servidor
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            // Cerrar la conexión
            connection.disconnect();
        }
    }
    private static void fetchAndSaveHTML(String dataUrl, String destinationPath) throws Exception {
        // Establecer la conexión
        URL url = new URL(dataUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Obtener las cookies almacenadas y establecerlas en la solicitud
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Cookie");
        if (cookiesHeader != null) {
            connection.setRequestProperty("Cookie", String.join(";", cookiesHeader));
        }

        // Crear el directorio de destino si no existe
        File destinationDir = new File(destinationPath);
        destinationDir.mkdirs();

        // Crear el archivo de destino para el contenido HTML
        File htmlFile = new File(destinationDir, "index.html");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile))) {

            // Guardar el contenido HTML en el archivo
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
}

