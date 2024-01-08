package bookingprovidermc.control;

import bookingprovidermc.model.Booking;
import bookingprovidermc.model.Hotel;
import bookingprovidermc.model.Rate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class XoteloHotelProvider implements HotelProvider {
    private static final String BASE_URL =
            "https://data.xotelo.com/api/rates?hotel_key={hotel_key}&chk_in={chk_in}&chk_out={chk_out}&currency=EUR";
    private static final String ss = "booking-provider";

    @Override
    public Booking get(Hotel hotel, String checkIn, String checkOut) {
        String url = buildURL(hotel.key(), checkIn, checkOut);
        try {
            String jsonResponse = obtainJSON(url);

            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonElement jsonElement = JsonParser.parseString(jsonResponse);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    if (jsonObject.getAsJsonObject("result").has("rates")) {
                        JsonArray ratesArray = jsonObject.getAsJsonObject("result").getAsJsonArray("rates");

                        if (!ratesArray.isJsonNull() && ratesArray.size() > 0) {
                            String chkIn = jsonObject.getAsJsonObject("result").get("chk_in").getAsString();
                            String chkOut = jsonObject.getAsJsonObject("result").get("chk_out").getAsString();
                            long timestamp = jsonObject.get("timestamp").getAsLong();
                            Instant ts = Instant.ofEpochMilli(timestamp);
                            ArrayList<Rate> rates = new ArrayList<>();

                            for (int i = 0; i < ratesArray.size(); i++) {
                                JsonObject rateObject = ratesArray.get(i).getAsJsonObject();
                                String code = rateObject.get("code").getAsString();
                                String name = rateObject.get("name").getAsString();
                                Float rate = rateObject.get("rate").getAsFloat();
                                Float tax = rateObject.get("tax").getAsFloat();
                                rates.add(new Rate(code, name, rate, tax));
                            }
                            return new Booking(hotel, chkIn, chkOut, rates, ts, ss);
                        }
                    }
                } else {
                    System.out.println("The response is not a valid JSON object");
                }
            } else {
                System.out.println("The response from the web service is null or empty.");
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildURL(String key, String checkIn, String checkOut) {
        return BASE_URL
                .replace("{hotel_key}", key)
                .replace("{chk_in}", checkIn)
                .replace("{chk_out}", checkOut);
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
}
