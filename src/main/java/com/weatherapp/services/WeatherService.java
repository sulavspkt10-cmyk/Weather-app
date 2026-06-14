package com.weatherapp.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherService {

    private static String API_KEY;

    public WeatherService() {
        try {
            Properties props = new Properties();
            InputStream input = WeatherService.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");
            props.load(input);
            API_KEY = props.getProperty("WEATHER_API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject getWeather(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + encodedCity + "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status != 200) {
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            JsonObject jsonResponse = JsonParser
                    .parseString(response.toString())
                    .getAsJsonObject();

            JsonObject result = new JsonObject();
            result.addProperty("city", city);
            result.addProperty("temperature",
                    jsonResponse.getAsJsonObject("main")
                            .get("temp").getAsDouble());
            result.addProperty("humidity",
                    jsonResponse.getAsJsonObject("main")
                            .get("humidity").getAsInt());
            result.addProperty("description",
                    jsonResponse.getAsJsonArray("weather")
                            .get(0).getAsJsonObject()
                            .get("description").getAsString());
            result.addProperty("feelsLike",
                    jsonResponse.getAsJsonObject("main")
                            .get("feels_like").getAsDouble());

            return result;

        } catch (Exception e) {
            System.err.println("Could not fetch weather for city: " + city);
            return null;
        }
    }
}
