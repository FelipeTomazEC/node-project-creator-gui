package br.ufop.tomaz;

import br.ufop.tomaz.controller.Screen;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public class App extends Application {

    private static Stage STAGE;
    private static HostServices HOST_SERVICES;

    @Override
    public void start(Stage primaryStage) throws Exception {
        STAGE = primaryStage;
        HOST_SERVICES = this.getHostServices();

        STAGE.setTitle("Node Project Creator");
        Image icon = new Image(getClass().getResourceAsStream("/br/ufop/tomaz/icons/logo-small.png"));
        STAGE.getIcons().add(icon);

        STAGE.setResizable(false);
        Parent root = FXMLLoader.load(App.class.getResource(Screen.HOME.getFxmlFilePath()));
        Scene scene = new Scene(root);
        STAGE.setScene(scene);
        STAGE.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Window getWindow() {
        return STAGE.getOwner();
    }

    public static void OpenGitHubPage() {
        String githubLink = "https://github.com/FelipeTomazEC/node-project-creator-gui";
        HOST_SERVICES.showDocument(githubLink);
    }
}
