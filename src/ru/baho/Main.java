package ru.baho;
import java.sql.*;

public class Main {
    static Connection db = null;
    static Statement statement = null;
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/java_lab_4_db?serverTimezone=Europe/Moscow&useSSL=false";
    static final String USER = "baho";
    static final String PASS = "KillerOfMyMind29";
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            db = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection succesfull!");
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        if (db != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        try {
            statement = db.createStatement();
            (new Console()).run();
        } catch (Exception ex) {
    }
        closeDB();
    }
    private static void closeDB()
    {
        try {
            db.close();
        } catch (Exception ex)
        {
            System.out.println("Failed disconnection to database");
        }
    }
}
