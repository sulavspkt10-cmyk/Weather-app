package com.weatherapp.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    // Get weather for a city
    public JsonObject getWeather(String city) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read response
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON response
            JsonObject jsonResponse = JsonParser
                    .parseString(response.toString())
                    .getAsJsonObject();

            // Extract what we need
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

            System.out.println("✅ Weather fetched for: " + city);
            return result;

        } catch (Exception e) {
            System.out.println("❌ Could not get weather for: " + city);
            e.printStackTrace();
            return null;
        }
    }
}