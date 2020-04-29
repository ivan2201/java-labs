package ru.baho;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Console implements Runnable {
    Statement db;
    public Console(Statement db)
    {
        this.db = db;
    }
    @Override
    public void run() {
        String str = "";
        if (db == null) return;
        Store store = new Store(db);
        store.createTables();
        store.fillTable(10);
        System.out.println("вас приветствуем система управления складом))). вводите команды");
        Scanner sc = new Scanner(System.in);
        while (str.compareTo("/q") != 0)
        {
            str = sc.next();
            if (str.compareTo("/add") == 0)
            {
                if (store.addProduct(sc.next(), sc.nextLong()))
                {
                    System.out.println("success");
                } else
                {
                    System.out.println("unsuccess");
                }
            } else if (str.compareTo("/delete") == 0)
            {
                if (store.deleteProduct(sc.next()))
                {
                    System.out.println("success");
                } else
                {
                    System.out.println("unsuccess");
                }
            } else if (str.compareTo("/show_all") == 0)
            {
                try (ResultSet set = store.getAllProducts()) {
                    if (set != null) {
                        printRows(set, 4, new int[]{Store.POSITION_PRODID, Store.POSITION_ID, Store.POSITION_TITLE, Store.POSITION_COST});
                    }
                } catch (SQLException ex)
                {
                    System.out.println("Error " + ex.toString());
                }
            } else if (str.compareTo("/price") == 0)
            {
                try (ResultSet set = store.getProduct(sc.next()))
                {
                    if (set != null)
                    {
                        printRows(set,2, new int[] { 1, 2});
                    }
                } catch (SQLException ex)
                {
                System.out.println("Error " + ex.toString());
                }
            } else if (str.compareTo("/change_price") == 0)
            {
                if (store.setPrice(sc.next(), sc.nextLong()))
                {
                    System.out.println("success");
                } else
                {
                    System.out.println("unsuccess");
                }
            } else if (str.compareTo("/filter_by_price") == 0)
            {
                try (ResultSet set = store.getProducts(sc.nextInt(), sc.nextInt()))
                {
                if (set != null)
                {
                    printRows(set,4, new int[] { Store.POSITION_PRODID, Store.POSITION_ID,Store.POSITION_TITLE, Store.POSITION_COST});
                }
                } catch (SQLException ex)
                {
                    System.out.println("Error " + ex.toString());
                }
            } else {
            }
        }
        store.deleteTables();
    }

    public void printRows(ResultSet set, int numCols, int[] rows)
    {
        try {
            while (set.next()) {
                for (int i = 0; i < numCols; i++)
                {
                    System.out.print(set.getString(rows[i]) + ' ');
                }
                System.out.print('\n');
            }
        } catch(SQLException ex)
        {
            System.out.println("printing error "+ ex);
        }
    }
}
