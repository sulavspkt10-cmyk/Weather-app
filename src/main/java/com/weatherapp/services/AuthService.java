package com.weatherapp.services;

import java.util.UUID;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.weatherapp.config.MongoDBConnection;

public class AuthService {

    private static MongoCollection<Document> adminsCollection;
    private static MongoCollection<Document> sessionsCollection;

    public AuthService() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        adminsCollection = db.getCollection("admins");
        sessionsCollection = db.getCollection("sessions");
    }

    // Register a new admin
    public boolean registerAdmin(String name, String email, String password) {
        try {
            // Check if email already exists
            Document existing = adminsCollection
                    .find(new Document("email", email))
                    .first();

            if (existing != null) {
                System.out.println("Admin already exists!");
                return false;
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Save admin to MongoDB
            Document admin = new Document()
                    .append("name", name)
                    .append("email", email)
                    .append("password", hashedPassword)
                    .append("role", "admin");

            adminsCollection.insertOne(admin);
            System.out.println("✅ Admin registered successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login admin
    public String loginAdmin(String email, String password) {
        try {
            // Find admin by email
            Document admin = adminsCollection
                    .find(new Document("email", email))
                    .first();

            if (admin == null) {
                System.out.println("Admin not found!");
                return null;
            }

            // Check password
            String hashedPassword = admin.getString("password");
            if (!BCrypt.checkpw(password, hashedPassword)) {
                System.out.println("Wrong password!");
                return null;
            }

            // Create session token
            String sessionToken = UUID.randomUUID().toString();

            // Save session to MongoDB
            Document session = new Document()
                    .append("adminId", admin.getObjectId("_id").toString())
                    .append("sessionToken", sessionToken)
                    .append("email", email);

            sessionsCollection.insertOne(session);
            System.out.println("✅ Admin logged in successfully!");
            return sessionToken;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Logout admin
    public boolean logoutAdmin(String sessionToken) {
        try {
            sessionsCollection.deleteOne(
                    new Document("sessionToken", sessionToken)
            );
            System.out.println("✅ Admin logged out!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if session is valid
    public boolean isLoggedIn(String sessionToken) {
        if (sessionToken == null) return false;
        Document session = sessionsCollection
                .find(new Document("sessionToken", sessionToken))
                .first();
        return session != null;
    }
}