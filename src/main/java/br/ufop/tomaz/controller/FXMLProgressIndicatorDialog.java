package br.ufop.tomaz.controller;

import br.ufop.tomaz.features.Feature;
import br.ufop.tomaz.util.AppCreator;
import br.ufop.tomaz.util.AppCreatorFactory;
import br.ufop.tomaz.util.PackageManagers;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FXMLProgressIndicatorDialog implements Initializable {

    @FXML private Button btnFinish;
    @FXML private Label lblStatusMessage;
    @FXML private ProgressBar installerProgress;
    @FXML private Label lblPercentageProgress;

    private File projectDir;
    private String projectName;
    private PackageManagers packageManager;
    private Map.Entry<Feature, String>[] featuresList;

    public FXMLProgressIndicatorDialog(File projectDir,
                                       String projectName,
                                       PackageManagers packageManager,
                                       Map.Entry<Feature, String>[] featuresList
    ) {
        this.projectDir = projectDir;
        this.projectName = projectName;
        this.packageManager = packageManager;
        this.featuresList = featuresList;
    }

    public void close(Event event) {
        Stage stage = (Stage) btnFinish.getParent().getScene().getWindow();
        stage.close();
    }

    private void createProject() {
        AppCreator appCreator = AppCreatorFactory.getAppCreator(packageManager, projectDir, projectName);
        appCreator.createApp();

        Task<Void> installPackages = getInstallFeaturesTask(appCreator);
        this.lblStatusMessage.textProperty().bind(installPackages.messageProperty());
        this.installerProgress.progressProperty().bind(installPackages.progressProperty());
        new Thread(installPackages).start();
    }

    private Task<Void> getInstallFeaturesTask(AppCreator creator) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final int numberOfFeatures = featuresList.length;
                for (int i = 0; i < numberOfFeatures; i++) {
                    if (this.isCancelled()) {
                        break;
                    }
                    Feature feature = featuresList[i].getKey();
                    String args = featuresList[i].getValue();
                    updateMessage("Installing ".concat(feature.getName()).concat("..."));
                    creator.installFeature(feature, args);
                    updateProgress(i, numberOfFeatures);
                    updateMessage(feature.getName().concat(" installed successfully."));
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
                updateMessage("Done.");
                updateProgress(1, 1);
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnFinish.disableProperty().bind(installerProgress.progressProperty().isNotEqualTo(1));
        installerProgress.progressProperty().addListener((ob, ov, nv) -> {
            if(nv.doubleValue() > 0){
                String progress = (int)(nv.doubleValue() * 100) + "%";
                lblPercentageProgress.setText(progress);
            }
        });
        btnFinish.setOnAction((event) -> close(event));
        createProject();
    }
}
