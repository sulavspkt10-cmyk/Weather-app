package com.weatherapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                // Load config.properties
                Properties props = new Properties();
                InputStream input = MongoDBConnection.class
                        .getClassLoader()
                        .getResourceAsStream("config.properties");
                props.load(input);

                String mongoUri = props.getProperty("MONGO_URI");
                String dbName = props.getProperty("DB_NAME");

                // Connect to MongoDB
                mongoClient = MongoClients.create(mongoUri);
                database = mongoClient.getDatabase(dbName);

                System.out.println("✅ MongoDB Connected Successfully!");

            } catch (Exception e) {
                System.out.println("❌ MongoDB Connection Failed!");
                e.printStackTrace();
            }
        }
        return database;
    }
}