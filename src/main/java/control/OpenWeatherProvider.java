package control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

public class OpenWeatherProvider implements WeatherProvider {
    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={apiKey}&units=metric";
    private final String apiKey;

    public OpenWeatherProvider(String apiKeyLocation) {
        this.apiKey = readApiKey(apiKeyLocation);
    }

    @Override
    public Weather get(Location location, Instant instant) {
        String url = buildURL(location.latitude(), location.longitude(), apiKey);

        try {
            String json = obtainJSON(url);

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray listArray = jsonObject.getAsJsonArray("list");

            JsonObject matchingObject = null;
            long targetEpoch = instant.getEpochSecond();

            for (JsonElement element : listArray) {
                JsonObject item = element.getAsJsonObject();
                long itemEpoch = item.get("dt").getAsLong();
                if (itemEpoch == targetEpoch) {
                    matchingObject = item;
                    break;
                }
            }

            if (matchingObject != null) {
                JsonObject mainObject = matchingObject.getAsJsonObject("main");
                JsonObject cloudsObject = matchingObject.getAsJsonObject("clouds");
                JsonObject windObject = matchingObject.getAsJsonObject("wind");

                Float temp = mainObject.get("temp").getAsFloat();
                Float rain = matchingObject.get("pop").getAsFloat();
                Integer humidity = mainObject.get("humidity").getAsInt();
                Integer clouds = cloudsObject.get("all").getAsInt();
                Float windSpeed = windObject.get("speed").getAsFloat();

                return new Weather(temp, rain, humidity, clouds, windSpeed, location, instant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildURL(Double latitude, Double longitude, String apiKey) {
        return BASE_URL
                .replace("{lat}", String.valueOf(latitude))
                .replace("{lon}", String.valueOf(longitude))
                .replace("{apiKey}", apiKey);
    }

    private String obtainJSON(String url) throws IOException {
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            return document.text();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String readApiKey(String apiKeyLocation) {
        try (BufferedReader apiKeyReader = new BufferedReader(new FileReader(apiKeyLocation))) {
            return apiKeyReader.readLine();
        } catch (IOException e) {
            System.err.println("Error reading API key from file: " + apiKeyLocation);
            e.printStackTrace();
            return null;
        }
    }
}
