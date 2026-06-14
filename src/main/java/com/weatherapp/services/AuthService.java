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

    public boolean registerAdmin(String name, String email, String password) {
        try {
            Document existing = adminsCollection
                    .find(new Document("email", email))
                    .first();

            if (existing != null) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            Document admin = new Document()
                    .append("name", name)
                    .append("email", email)
                    .append("password", hashedPassword)
                    .append("role", "admin");

            adminsCollection.insertOne(admin);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loginAdmin(String email, String password) {
        try {
            Document admin = adminsCollection
                    .find(new Document("email", email))
                    .first();

            // Use the same path and same timing whether email exists or not
            // to prevent admin-email enumeration via timing or error messages.
            String storedHash = (admin != null)
                    ? admin.getString("password")
                    : "$2a$10$invalidhashpaddingtomatchbcrypttime000000000000000000000";

            if (!BCrypt.checkpw(password, storedHash) || admin == null) {
                return null;
            }

            String sessionToken = UUID.randomUUID().toString();

            Document session = new Document()
                    .append("adminId", admin.getObjectId("_id").toString())
                    .append("sessionToken", sessionToken)
                    .append("email", email);

            sessionsCollection.insertOne(session);
            return sessionToken;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean logoutAdmin(String sessionToken) {
        try {
            sessionsCollection.deleteOne(
                    new Document("sessionToken", sessionToken)
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isLoggedIn(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) return false;
        Document session = sessionsCollection
                .find(new Document("sessionToken", sessionToken))
                .first();
        return session != null;
    }

    public long getAdminCount() {
        return adminsCollection.countDocuments();
    }
}
