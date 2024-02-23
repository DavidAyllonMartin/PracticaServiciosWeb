import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Esta clase se encarga de realizar el login en la página de Fantasy NFL y
 * recuperar datos específicos de la página después del login.
 */
public class NFLFantasyLogin {

    /**
     * Punto de entrada principal del programa.
     *
     * @param args Los argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        try {
            // Establecer el manejador de cookies para almacenar y reutilizar cookies entre solicitudes
            CookieHandler.setDefault(new CookieManager());

            String loginUrl = "https://fantasy.nfl.com/account/sign-in";
            String username = ""; // Aquí debe ir el nombre de usuario
            String password = ""; // Aquí debe ir la contraseña

            login(loginUrl, username, password);

            String dataUrl = "https://fantasy.nfl.com/league/54538/transactions?transactionType=drop";
            fetchAndSaveHTML(dataUrl, "src/main/resources/scripts");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza el login en la página especificada usando el nombre de usuario y contraseña proporcionados.
     *
     * @param loginUrl URL de la página de login.
     * @param username Nombre de usuario para el login.
     * @param password Contraseña para el login.
     * @throws IOException Si ocurre un error durante la conexión o el proceso de login.
     */
    private static void login(String loginUrl, String username, String password) throws IOException {
        URL url = new URL(loginUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String postData = "username=" + username + "&password=" + password;
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Procesar y almacenar cookies si es necesario (actualmente solo se imprime la respuesta)
        readAndPrintResponse(connection);

        connection.disconnect();
    }

    /**
     * Lee la respuesta del servidor y la imprime.
     * Este método puede ser modificado para procesar la respuesta de manera diferente si es necesario.
     *
     * @param connection La conexión HTTP desde la cual se leerá la respuesta.
     * @throws IOException Si ocurre un error al leer la respuesta.
     */
    private static void readAndPrintResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Respuesta del inicio de sesión: " + response);
        }
    }

    /**
     * Descarga el contenido HTML de una URL y lo guarda en un archivo en el directorio especificado.
     *
     * @param dataUrl          URL de la página de la cual se descargará el contenido.
     * @param destinationPath  Ruta del directorio donde se guardará el archivo HTML.
     * @throws IOException Si ocurre un error durante la descarga o al guardar el archivo.
     */
    private static void fetchAndSaveHTML(String dataUrl, String destinationPath) throws IOException {
        URL url = new URL(dataUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Intentar reutilizar las cookies guardadas para mantener la sesión
        // Nota: Este paso podría necesitar ajustes para asegurar que las cookies se manejen correctamente.
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Cookie");
        if (cookiesHeader != null) {
            connection.setRequestProperty("Cookie", String.join(";", cookiesHeader));
        }

        File destinationDir = new File(destinationPath);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs(); // Asegurar que el directorio de destino existe
        }

        File htmlFile = new File(destinationDir, "index.html");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
}
