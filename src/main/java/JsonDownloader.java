package org.ielena.pokedex.utils;

import javafx.scene.image.Image;
import org.ielena.pokedex.model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JsonDownloader {

    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon/";

    public static void downloadJson(int id, Path jsonPath) {
        try {
            String jsonContent = fetchJsonFromApi(id);
            saveJsonToFile(jsonContent, jsonPath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error downloading and saving the JSON file", e);
        }
    }

    public static Pokemon json2Pokemon(Path jsonFile) {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(jsonFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONTokener parser = new JSONTokener(jsonContent.toString());
        JSONObject pokemonJSON = new JSONObject(parser);

        // Extract Pokemon details
        int id = pokemonJSON.getInt("id");
        String name = pokemonJSON.getString("name");
        String[] typesArray = new String[2];

        JSONArray types = pokemonJSON.getJSONArray("types");
        for (int j = 0; j < types.length(); j++) {
            typesArray[j] = types.getJSONObject(j).getJSONObject("type").getString("name");
        }

        String type1 = typesArray[0];
        String type2 = typesArray[1];

        int height = pokemonJSON.getInt("height");
        int weight = pokemonJSON.getInt("weight");

        JSONArray stats = pokemonJSON.getJSONArray("stats");
        int hp = stats.getJSONObject(0).getInt("base_stat");
        int attack = stats.getJSONObject(1).getInt("base_stat");
        int defense = stats.getJSONObject(2).getInt("base_stat");
        int specialAttack = stats.getJSONObject(3).getInt("base_stat");
        int specialDefense = stats.getJSONObject(4).getInt("base_stat");
        int speed = stats.getJSONObject(5).getInt("base_stat");

        String imageUrl = pokemonJSON.getJSONObject("sprites")
                .getJSONObject("other")
                .getJSONObject("official-artwork")
                .getString("front_default");

        Path imgPath = Paths.get(String.format("src/main/resources/org/ielena/pokedex/img/images/%d.png", id));

        if (!Files.exists(imgPath)) {
            try {
                ImageDownloader.downloadAndResizeImageFromUrl(imageUrl, imgPath.toString(), 175, 175);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = new Image(imgPath.toUri().toString());

        return new Pokemon.Builder()
                .id(id)
                .name(name)
                .type1(type1)
                .type2(type2)
                .height(height)
                .weight(weight)
                .hp(hp)
                .attack(attack)
                .defense(defense)
                .specialAttack(specialAttack)
                .specialDefense(specialDefense)
                .speed(speed)
                .image(image)
                .build();
    }


    private static String fetchJsonFromApi(int id) throws IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + id))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Error making the HTTP request. Response code: " + response.statusCode());
            }

            return response.body();
        }
    }

    private static void saveJsonToFile(String jsonContent, Path jsonPath) {
        try {
            Files.createDirectories(jsonPath.getParent());
            Files.writeString(jsonPath, jsonContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error writing the JSON file", e);
        }
    }
}
