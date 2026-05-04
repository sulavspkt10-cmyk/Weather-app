package com.weatherapp;

import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.weatherapp.middleware.AuthMiddleware;
import com.weatherapp.services.AuthService;
import com.weatherapp.services.UserService;
import com.weatherapp.services.WeatherService;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class App {

    public static void main(String[] args) {

        // Set port
        port(4567);

        // Serve static files (HTML, CSS)
        staticFiles.location("/public");

        // Initialize services
        AuthService authService = new AuthService();
        UserService userService = new UserService();
        WeatherService weatherService = new WeatherService();
        Gson gson = new Gson();

        // ==========================================
        // PUBLIC ROUTES
        // ==========================================

        // Home page
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // Login page
        get("/login", (req, res) -> {
            res.redirect("/login.html");
            return null;
        });

        // Login POST - handle login form
        post("/login", (req, res) -> {
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            String sessionToken = authService.loginAdmin(email, password);

            if (sessionToken != null) {
                // Set session cookie
                res.cookie("sessionToken", sessionToken);
                res.redirect("/dashboard");
            } else {
                res.redirect("/login?error=invalid");
            }
            return null;
        });

        // Get weather (public)
        get("/weather", (req, res) -> {
            String city = req.queryParams("city");
            if (city == null || city.isEmpty()) {
                return "{\"error\": \"City is required\"}";
            }
            res.type("application/json");
            var weather = weatherService.getWeather(city);
            return weather != null ? weather.toString() :
                    "{\"error\": \"City not found\"}";
        });

        // ==========================================
        // PROTECTED ROUTES (Admin only)
        // ==========================================

        // Dashboard page
        get("/dashboard", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.redirect("/dashboard.html");
            return null;
        });

        // Users page
        get("/users", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.redirect("/users.html");
            return null;
        });

        // Get all users as JSON
        get("/api/users", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            res.type("application/json");
            List<Document> users = userService.getAllUsers();
            return gson.toJson(users);
        });

        // Save a new user
        post("/api/save-user", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            String name = req.queryParams("name");
            String city = req.queryParams("city");
            String address = req.queryParams("address");

            boolean saved = userService.saveUser(name, city, address);
            res.type("application/json");
            return saved ? "{\"success\": true}" :
                    "{\"success\": false}";
        });

        // Delete a user
        delete("/api/user/:id", (req, res) -> {
            AuthMiddleware.checkAuth(req, res);
            String id = req.params("id");
            boolean deleted = userService.deleteUser(id);
            res.type("application/json");
            return deleted ? "{\"success\": true}" :
                    "{\"success\": false}";
        });

        // Logout
        post("/logout", (req, res) -> {
            String sessionToken = req.cookie("sessionToken");
            authService.logoutAdmin(sessionToken);
            res.removeCookie("sessionToken");
            res.redirect("/login");
            return null;
        });

        // Register first admin (run once then disable!)
        post("/setup", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            boolean registered = authService.registerAdmin(
                    name, email, password);
            res.type("application/json");
            return registered ? "{\"success\": true}" :
                    "{\"success\": false}";
        });

        System.out.println("✅ Server started at http://localhost:4567");
    }
}