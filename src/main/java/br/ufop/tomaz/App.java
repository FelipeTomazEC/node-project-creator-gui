package br.ufop.tomaz;

import br.ufop.tomaz.controller.Screen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public class App extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Node Project Creator");
        Image icon = new Image(getClass().getResourceAsStream("/br/ufop/tomaz/icons/logo-small.png"));
        stage.getIcons().add(icon);

        stage.setResizable(false);
        Parent root = FXMLLoader.load(App.class.getResource(Screen.HOME.getFxmlFilePath()));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Window getWindow(){
        return stage.getOwner();
    }
}
