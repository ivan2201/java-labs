package ru.baho;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.text.html.ListView;
import java.sql.Statement;
import java.util.Optional;
import java.util.Stack;

public class Application extends javafx.application.Application {

    Store store;
    Statement db;
    class ProductViews extends HBox
    {
        Label m_title = new Label();
        Label m_prodId = new Label();
        Label m_id = new Label();
        Label m_cost = new Label();

        public ProductViews(String title, String id, String prodId, String cost)
        {
            super();
            EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    TextInputDialog dialog = new TextInputDialog("change_price");
                    dialog.setTitle(title);
                    dialog.setHeaderText(null);
                    dialog.setContentText("Please enter new price:");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        m_cost.setText(result.get());

                    }

                    result.ifPresent(name -> {
                        m_cost.setText(name);

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

    @Override
    public void start(Stage stage) throws Exception {
        db = Main.statement;
        if (db == null) return;
        store = new Store(db);
        stage.setTitle("DBApplication");
        Text text = new Text("Store");
        ListView producctList = new ListView();
        Group root = new Group();
        StackPane pane = new StackPane();
        ObservableList list = root.getChildren();
        Scene scene = new Scene(root, 800,600);
        stage.setScene(scene);
        stage.show();
    }
}
