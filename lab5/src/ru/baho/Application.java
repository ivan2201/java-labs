package ru.baho;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class Application extends javafx.application.Application {

    Store store;
    ObservableList productsViews;
    class ProductViews extends HBox
    {
        Alert badFormatCost = new Alert(Alert.AlertType.ERROR);
        Label m_title = new Label();
        Label m_prodId = new Label();
        Label m_id = new Label();
        Label m_cost = new Label();
        public ProductViews(String title, String id, String prodId, String cost)
        {
            super();
            badFormatCost.setTitle("Error uncorrect value");
            badFormatCost.setHeaderText("this value has uncorrect format");
            badFormatCost.setContentText(null);
            EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    TextInputDialog dialog = new TextInputDialog("change_price");
                    dialog.setTitle(title);
                    dialog.setHeaderText(null);
                    dialog.setContentText("Please enter new price:");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        try {
                            store.setPrice(m_title.getText(), Long.parseLong(result.get()));
                            m_cost.setText(result.get());
                        } catch (Exception ex)
                        {
                            badFormatCost.showAndWait();
                        }
                    }

                    result.ifPresent(name -> {
                        try {
                            store.setPrice(m_title.getText(), Long.parseLong(result.get()));
                            m_cost.setText(name);
                        } catch (Exception ex)
                        {
                            badFormatCost.showAndWait();
                        }
                    });
                }
            };
            m_cost.setText(cost);
            m_cost.addEventFilter(MouseEvent.MOUSE_CLICKED, event);
            m_prodId.setText(prodId);
            m_prodId.addEventFilter(MouseEvent.MOUSE_CLICKED, event);
            m_title.setText(title);
            m_title.addEventFilter(MouseEvent.MOUSE_CLICKED, event);
            m_id.setText(id);
            m_id.addEventFilter(MouseEvent.MOUSE_CLICKED, event);
            ObservableList list = getChildren();
            list.addAll(title, prodId, id, cost);
            addEventFilter(MouseEvent.MOUSE_CLICKED, event);
        }
    }
    static Connection connection = null;
    static Statement db= null;
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/java_lab_4_db?serverTimezone=Europe/Moscow&useSSL=false";
    static final String USER = "baho";
    static final String PASS = "KillerOfMyMind29";

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error create Connection");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
        if (connection != null) {

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed connection to DB");
            alert.setContentText(null);
            alert.showAndWait();
        }
        try {
            db = connection.createStatement();
        } catch (Exception ex) {
            closeDB();
        }
        if (db == null) return;
        store = new Store(db);
        store.createTables();
        store.fillTable(10);
        stage.setTitle("DBApplication");
        Text text = new Text("Store");
        productsViews = getProductViews();
        ListView<ProductViews> productList = new ListView<ProductViews>(productsViews);
        Group root = new Group();
        StackPane pane = new StackPane();
        ObservableList list = root.getChildren();
        list.add(productList);
        Scene scene = new Scene(root, 800,600);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                store.deleteTables();
            }
        });
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

    public ObservableList<ProductViews> getProductViews()
    {
        ArrayList<ProductViews> result = new ArrayList<ProductViews>();
        ResultSet set = store.getAllProducts();
        try {
            while (set.next()) {
                result.add(new ProductViews(set.getString(Store.POSITION_TITLE),
                        set.getString(Store.POSITION_ID),set.getString(Store.POSITION_PRODID),
                        set.getString(Store.POSITION_COST)));
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error while getting");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
        return FXCollections.observableArrayList(result);
    }
}
