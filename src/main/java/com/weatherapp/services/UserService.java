package com.weatherapp.services;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.weatherapp.config.MongoDBConnection;

public class UserService {

    private static MongoCollection<Document> usersCollection;

    public UserService() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        usersCollection = db.getCollection("users");
    }

    // Save a new user
    public boolean saveUser(String name, String city, String address) {
        try {
            Document user = new Document()
                    .append("name", name)
                    .append("city", city)
                    .append("address", address);

            usersCollection.insertOne(user);
            System.out.println("✅ User saved: " + name);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all users
    public List<Document> getAllUsers() {
        List<Document> users = new ArrayList<>();
        try {
            for (Document doc : usersCollection.find()) {
                // Convert ObjectId to String for JSON
                doc.put("_id", doc.getObjectId("_id").toString());
                users.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Delete a user by ID
    public boolean deleteUser(String id) {
        try {
            usersCollection.deleteOne(
                    new Document("_id", new ObjectId(id))
            );
            System.out.println("✅ User deleted: " + id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}