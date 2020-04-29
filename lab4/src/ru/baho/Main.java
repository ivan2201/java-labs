package ru.baho;
import java.sql.*;

public class Main {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/java_lab_4_db?serverTimezone=Europe/Moscow&useSSL=false";
    static final String USER = "baho";
    static final String PASS = "KillerOfMyMind29";
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            try (Connection db = DriverManager.getConnection(DB_URL, USER, PASS)) {
                System.out.println("Connection succesfull!");
                try (Statement statement = db.createStatement()) {
                    (new Console(statement)).run();
                } catch (Exception ex) {
                    System.out.println("Connection failed...");
                    System.out.println(ex);
                }
            } catch (SQLException ex) {
                throw ex;
            }
        }catch (ClassNotFoundException ex)
        {
            System.out.println("Failed to load Database Driver");
        } catch (SQLException ex) {
            System.out.println("Failed to make connection to database");
            System.out.println(ex);
        } catch (Exception ex)
        {
            System.out.println("Unknown Error");
        }
    }
}
