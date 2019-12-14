package br.ufop.tomaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        AnchorPane pane = new AnchorPane();
        Label label = new Label("NODE PROJECT CREATOR.");
        pane.getChildren().add(label);
        Scene scene = new Scene(pane, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
