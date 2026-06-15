package com.weatherapp;

import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.middleware.AuthMiddleware;
import com.weatherapp.services.AuthService;
import com.weatherapp.services.UserService;
import com.weatherapp.services.WeatherService;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class App {

    private static final int SESSION_MAX_AGE = 3600; // seconds

    public static void main(String[] args) {

        port(4567);
        staticFiles.location("/public");

        AuthService authService = new AuthService();
        UserService userService = new UserService();
        WeatherService weatherService = new WeatherService();
        Gson gson = new Gson();

        // Security headers on every response
        before((req, res) -> {
            res.header("X-Frame-Options", "DENY");
            res.header("X-Content-Type-Options", "nosniff");
            res.header("X-XSS-Protection", "1; mode=block");
            res.header("Referrer-Policy", "strict-origin-when-cross-origin");
            res.header("Content-Security-Policy",
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
        });

        // ==========================================
        // PUBLIC ROUTES
        // ==========================================

        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        get("/login", (req, res) -> {
            res.redirect("/login.html");
            return null;
        });

        post("/login", (req, res) -> {
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            if (email == null || password == null
                    || email.trim().isEmpty() || password.isEmpty()) {
                res.redirect("/login?error=invalid");
                return null;
            }

            String sessionToken = authService.loginAdmin(
                    email.trim().toLowerCase(), password);

            if (sessionToken != null) {
                // HttpOnly + SameSite=Strict prevents JS access and CSRF
                res.header("Set-Cookie",
                        "sessionToken=" + sessionToken
                        + "; Path=/; HttpOnly; SameSite=Strict; Max-Age="
                        + SESSION_MAX_AGE);
                res.redirect("/dashboard");
            } else {
                res.redirect("/login?error=invalid");
            }
            return null;
        });

        get("/weather", (req, res) -> {
            String city = req.queryParams("city");
            if (city == null || city.trim().isEmpty()) {
                res.status(400);
                return "{\"error\": \"City is required\"}";
            }
            if (city.length() > 100) {
                res.status(400);
                return "{\"error\": \"City name too long\"}";
            }
            res.type("application/json");
            JsonObject weather = weatherService.getWeather(city.trim());
            return weather != null ? weather.toString()
                    : "{\"error\": \"City not found\"}";
        });

        // ==========================================
        // PROTECTED ROUTES (Admin only)
        // ==========================================

        get("/dashboard", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.redirect("/dashboard.html");
            return null;
        });

        get("/users", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.redirect("/users.html");
            return null;
        });

        get("/api/users", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.type("application/json");
            List<Document> users = userService.getAllUsers();
            return gson.toJson(users);
        });

        // Accepts a JSON body: { "name": "...", "city": "...", "address": "..." }
        post("/api/save-user", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.type("application/json");

            try {
                JsonObject body = JsonParser.parseString(req.body())
                        .getAsJsonObject();
                String name    = body.has("name")    ? body.get("name").getAsString().trim()    : "";
                String city    = body.has("city")    ? body.get("city").getAsString().trim()    : "";
                String address = body.has("address") ? body.get("address").getAsString().trim() : "";

                if (name.isEmpty() || name.length() > 100) {
                    res.status(400);
                    return "{\"error\": \"Invalid name\"}";
                }
                if (city.isEmpty() || city.length() > 100) {
                    res.status(400);
                    return "{\"error\": \"Invalid city\"}";
                }
                if (address.isEmpty() || address.length() > 200) {
                    res.status(400);
                    return "{\"error\": \"Invalid address\"}";
                }

                boolean saved = userService.saveUser(name, city, address);
                return saved ? "{\"success\": true}" : "{\"success\": false}";

            } catch (Exception e) {
                res.status(400);
                return "{\"error\": \"Invalid request body\"}";
            }
        });

        delete("/api/user/:id", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            String id = req.params("id");
            if (id == null || !id.matches("[a-fA-F0-9]{24}")) {
                res.status(400);
                return "{\"error\": \"Invalid user ID\"}";
            }
            boolean deleted = userService.deleteUser(id);
            res.type("application/json");
            return deleted ? "{\"success\": true}" : "{\"success\": false}";
        });

        post("/logout", (req, res) -> {
            String sessionToken = req.cookie("sessionToken");
            authService.logoutAdmin(sessionToken);
            // Expire the cookie immediately
            res.header("Set-Cookie",
                    "sessionToken=; Path=/; HttpOnly; SameSite=Strict; Max-Age=0;"
                    + " Expires=Thu, 01 Jan 1970 00:00:00 GMT");
            res.redirect("/login");
            return null;
        });

        // One-time setup — disabled after the first admin is registered.
        post("/setup", (req, res) -> {
            res.type("application/json");
            if (authService.getAdminCount() > 0) {
                res.status(403);
                return "{\"error\": \"Setup already completed\"}";
            }
            String name     = req.queryParams("name");
            String email    = req.queryParams("email");
            String password = req.queryParams("password");

            if (name == null || email == null || password == null
                    || name.trim().isEmpty() || email.trim().isEmpty()
                    || password.length() < 8) {
                res.status(400);
                return "{\"error\": \"Invalid setup parameters\"}";
            }

            boolean registered = authService.registerAdmin(
                    name.trim(), email.trim().toLowerCase(), password);
            return registered ? "{\"success\": true}" : "{\"success\": false}";
        });

        System.out.println("Server started at http://localhost:4567");
    }
}
