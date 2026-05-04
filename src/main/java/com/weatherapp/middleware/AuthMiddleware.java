package com.weatherapp.middleware;

import com.weatherapp.services.AuthService;
import spark.Request;
import spark.Response;
import static spark.Spark.halt;

public class AuthMiddleware {

    private static AuthService authService = new AuthService();

    // Call this before every protected route
    public static void checkAuth(Request req, Response res) {

        // Get session token from cookie
        String sessionToken = req.cookie("sessionToken");

        // Check if logged in
        if (!authService.isLoggedIn(sessionToken)) {
            // Not logged in → redirect to login page
            res.redirect("/login");
            halt(401);
        }
    }
}