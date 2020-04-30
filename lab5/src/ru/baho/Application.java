package ru.baho;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class Application extends javafx.application.Application {

    Connection db;
    Statement statement;
    Store store;
    ObservableList<ProductViews> productsViews;
    ListView<ProductViews> productList;
    int type = 0;

    class ProductViews extends FlowPane {
        Alert badFormatCost;
        Label m_title;
        Label m_prodId;
        Label m_id;
        Label m_cost;

        public ProductViews(String title, String id, String prodId, String cost) {
            super(10,10);
            badFormatCost = new Alert(Alert.AlertType.ERROR);
            m_title = new Label();
            m_prodId = new Label();
            m_id = new Label();
            m_cost = new Label();
            badFormatCost.setTitle("Error incorrect value");
            badFormatCost.setHeaderText("this value has incorrect format");
            badFormatCost.setContentText(null);
            EventHandler<MouseEvent> event = mouseEvent -> {
                if (type == 0) {
                    TextInputDialog dialog = new TextInputDialog(m_cost.getText());
                    dialog.setTitle(title);
                    dialog.setHeaderText(null);
                    dialog.setContentText("Please enter new price:");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        try {
                            store.setPrice(m_title.getText(), Long.parseLong(result.get()));
                            m_cost.setText(result.get());
                        } catch (Exception ex) {
                            badFormatCost.showAndWait();
                        }
                    } else {
                        result.ifPresent(name -> {
                            try {
                                store.setPrice(m_title.getText(), Long.parseLong(result.get()));
                                m_cost.setText(name);
                            } catch (Exception ex) {
                                badFormatCost.showAndWait();
                            }
                        });
                    }
                } else if (type == 1)
                {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle(title);
                    confirmation.setHeaderText("delete " + title);
                    confirmation.setContentText("Do you want to delete " + title + "?");
                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent()) {
                        try {
                            if (result.get()==ButtonType.OK) {
                                store.deleteProduct(m_title.getText());
                                productList.getItems().removeAll(ProductViews.this);
                            }
                        } catch (Exception ex) {
                            badFormatCost.showAndWait();
                        }
                    } else {
                        result.ifPresent(name -> {
                            try {
                                if (result.get()==ButtonType.APPLY) {
                                    store.deleteProduct(m_title.getText());
                                    productList.getItems().removeAll(ProductViews.this);
                                }
                            } catch (Exception ex) {
                                badFormatCost.showAndWait();
                            }
                        });
                    }
                }
            };
            m_cost.setText(cost);
            m_cost.prefWidthProperty().bind(Bindings.divide(
                    Bindings.subtract(widthProperty(),60), 4
            ));
            m_prodId.setText(prodId);
            m_prodId.prefWidthProperty().bind(Bindings.divide(
                    Bindings.subtract(widthProperty(),60), 4
            ));
            m_title.setText(title);
            m_title.prefWidthProperty().bind(Bindings.divide(
                    Bindings.subtract(widthProperty(),60), 4
            ));
            m_id.setText(id);
            m_id.prefWidthProperty().bind(Bindings.divide(
                    Bindings.subtract(widthProperty(),60), 4
            ));
            ObservableList list = getChildren();
            list.addAll(m_title, m_prodId, m_id, m_cost);
            addEventFilter(MouseEvent.MOUSE_CLICKED, event);
        }
    }

    static private final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/java_lab_4_db?serverTimezone=Europe/Moscow&useSSL=false";
    static private final String USER = "baho";
    static private final String PASS = "KillerOfMyMind29";

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            db = DriverManager.getConnection(DB_URL, USER, PASS);
            try {
                statement = db.createStatement();
                if (statement == null) throw new SQLException("cann't create statement");
                store = new Store(statement);
                store.createTables();
                store.fillTable(10);
                stage.setTitle("DBApplication");
                Text text = new Text("Store");
                Text text1 = new Text("action:");
                Button butAdd = new Button("add");
                butAdd.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    Dialog<Pair<String, String>> dialog = new Dialog<>();
                    dialog.setTitle("add Product");
                    dialog.setHeaderText("add Product");

// Set the icon (must be included in the project).
// Set the button types.
                    ButtonType editButtonType = new ButtonType("add", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

// Create labels and fields.
                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    TextField titleField = new TextField();
                    titleField.setPromptText("title");
                    TextField priceField = new TextField();
                    priceField.setPromptText("Price");
                    grid.add(new Label("Title:"), 0, 0);
                    grid.add(titleField, 1, 0);
                    grid.add(new Label("Price:"), 0, 1);
                    grid.add(priceField, 1, 1);

                    Node addButtonNode = dialog.getDialogPane().lookupButton(editButtonType);
                    addButtonNode.setDisable(true);

                    titleField.textProperty().addListener((observable, oldValue, newValue) -> {
                        addButtonNode.setDisable(newValue.trim().isEmpty());
                    });

                    dialog.getDialogPane().setContent(grid);

// Request focus on the title field by default.
                    Platform.runLater(titleField::requestFocus);

// Convert the result to a title-price-pair when the add button is clicked.
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == editButtonType) {
                            return new Pair<>(titleField.getText(), priceField.getText());
                        }
                        return null;
                    });

                    Optional<Pair<String, String>> result = dialog.showAndWait();

                    result.ifPresent(titlePrice -> {
                        try {
                            store.addProduct(titlePrice.getKey(), Long.parseLong(titlePrice.getValue()));
                            productList.setItems(getProductViews());
                        } catch (NumberFormatException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("error: incorrect value");
                            alert.setContentText(ex.toString());
                            alert.showAndWait();
                        }
                    });
                });
                ToggleGroup toggleGroup = new ToggleGroup();
                ToggleButton butDelete = new ToggleButton("delete");
                butDelete.setToggleGroup(toggleGroup);
                ToggleButton butEditPrice = new ToggleButton("edit price");
                butEditPrice.setToggleGroup(toggleGroup);
                toggleGroup.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
                    if (new_toggle == null)
                        type = 2;
                    else if (new_toggle == butDelete)
                        type = 1;
                    else if (new_toggle == butEditPrice)
                        type = 0;
                });
                productsViews = getProductViews();
                productList = new ListView<ProductViews>(productsViews);
                Group root = new Group();
                TextField minCost = new TextField("0");
                TextField maxCost = new TextField("1000");
                Button butFilter = new Button("filter");
                butFilter.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    try {
                        long min = Long.parseLong(minCost.getText());
                        long max = Long.parseLong(maxCost.getText());
                        if (min >= max) throw new NumberFormatException("left field must be smaller then right");
                        productList.setItems(getFilteredProductViews(min,max));
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("error: incorrect value");
                        alert.setContentText(ex.toString());
                        alert.showAndWait();
                    }
                });
                FlowPane topPane = new FlowPane(10,10,text,minCost,maxCost,butFilter);
                FlowPane downPane = new FlowPane(10,10,text1, butAdd,butDelete, butEditPrice);
                FlowPane mainPain = new FlowPane(Orientation.HORIZONTAL);
                mainPain.getChildren().addAll(topPane, productList,downPane);
                Scene scene = new Scene(mainPain,300,400);
                mainPain.prefWidthProperty().bind(scene.widthProperty());
                mainPain.prefHeightProperty().bind(scene.heightProperty());
                mainPain.setMinWidth(400);
                topPane.prefWidthProperty().bind(mainPain.widthProperty());
                downPane.prefWidthProperty().bind(mainPain.widthProperty());
                productList.prefWidthProperty().bind(mainPain.widthProperty());
                productList.prefHeightProperty().bind(Bindings.subtract(Bindings.subtract(mainPain.heightProperty(),
                        topPane.heightProperty()),downPane.heightProperty()));
                stage.setScene(scene);
                stage.show();
                stage.setOnCloseRequest(event -> {
                    if (store != null) store.deleteTables();
                    try {
                        if (statement != null) statement.close();
                        if (db != null) db.close();
                    } catch (SQLException ex)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("error while app closing");
                        alert.setContentText(ex.toString());
                        alert.showAndWait();
                    }
                });
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("error: faiied to create statement");
                alert.setContentText(ex.toString());
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error create Connection");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error unknown error");
            alert.setContentText(ex.toString());
            alert.showAndWait();
            throw ex;
        }
    }

    public ObservableList<ProductViews> getProductViews() {
        ArrayList<ProductViews> result = new ArrayList<ProductViews>();
        try (ResultSet set = store.getAllProducts()) {
            while (set.next()) {
                result.add(new ProductViews(set.getString(Store.POSITION_TITLE),
                        set.getString(Store.POSITION_ID), set.getString(Store.POSITION_PRODID),
                        set.getString(Store.POSITION_COST)));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error while getting");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
        return FXCollections.observableArrayList(result);
    }

    public ObservableList<ProductViews> getFilteredProductViews(long min, long max) {
        ArrayList<ProductViews> result = new ArrayList<ProductViews>();
        try (ResultSet set = store.getProducts(min, max)) {
            while (set.next()) {
                result.add(new ProductViews(set.getString(Store.POSITION_TITLE),
                        set.getString(Store.POSITION_ID), set.getString(Store.POSITION_PRODID),
                        set.getString(Store.POSITION_COST)));
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("error while getting");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
        return FXCollections.observableArrayList(result);
    }
}
