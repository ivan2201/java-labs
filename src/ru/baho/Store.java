package ru.baho;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Store {
    Statement statement = null;

    public Store(Statement statement)
    {
        this.statement = statement;
    }
    public static int POSITION_TITLE = 1;
    public static int POSITION_PRODID = 2;
    public static int POSITION_ID = 4;
    public static int POSITION_COST = 3;

    public boolean createTables()
    {
        try {
            statement.executeUpdate("CREATE TABLE products (\n" +
                    "   title varchar(80) unique,\n" +
                    "   prodid bigint unique,\n" +
                    "   cost bigint,\n" +
                    "   id bigserial primary key\n" +
                    ");");
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean deleteTables()
    {
        try {
            statement.executeUpdate("DROP TABLE products;");
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean addProduct(String title, long cost) {
        try {
            statement.executeUpdate("INSERT INTO products VALUES ('"+ title + "', " +
                    Long.toString(((long)title.hashCode()) + 5000000000L) + ", " +
                            Long.toString(cost) + ");");
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean deleteProduct(String title) {
        try {
            statement.executeUpdate("DELETE FROM products WHERE title = '" + title + "';");
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public ResultSet getAllProducts() {
        try {
            return statement.executeQuery("SELECT * FROM products;");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public ResultSet getProduct(String title) {
        try {
            return statement.executeQuery("SELECT title, cost FROM products\n" +
                    "WHERE title = '" + title + "';");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public boolean setPrice(String title, long price) {
        try {
            statement.executeUpdate("UPDATE products\n" +
                    "SET cost = " + Long.toString(price) +
                    "WHERE title = '" + title + "';");
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public ResultSet getProducts(long min, long max) {
        try {
            return statement.executeQuery("SELECT * FROM products\n" +
                    "WHERE cost > " + Long.toString(min) +
                    " AND cost < " + Long.toString(max) + "\n" +
                    "ORDER BY title;");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public void fillTable(int N) {
        try {
            for (int i = 1; i <= N; i++) {
                String str = Integer.toString(i);
                statement.executeUpdate("INSERT INTO products\n" +
                        "VALUES ('товар" + str + "', " +
                        Long.toString(((long)("товар" + str).hashCode()) + 5000000000L) + ", " +
                        str + "0);");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
