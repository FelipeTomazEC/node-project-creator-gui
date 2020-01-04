package br.ufop.tomaz.controller;

import br.ufop.tomaz.App;
import br.ufop.tomaz.features.*;
import br.ufop.tomaz.util.PackageManagers;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

public class FXMLHomeController implements Initializable {
    @FXML private TextField tfProjectName;
    @FXML private RadioButton rbNpm;
    @FXML private RadioButton rbYarn;
    @FXML private CheckBox chkEslintAndPrettier;
    @FXML private CheckBox chkBabel;
    @FXML private ComboBox<CodeStyle> cmbCodeStyle;
    @FXML private ComboBox<SGBD> cmbSgbd;
    @FXML private CheckBox chkNodemon;
    @FXML private CheckBox chkJest;
    @FXML private CheckBox chkExpress;
    @FXML private CheckBox chkMongoose;
    @FXML private CheckBox chkSequelize;
    @FXML private CheckBox chkLodash;
    @FXML private Button btnCreateProject;
    @FXML private ToggleGroup radioGroup;
    private Map<Features, Entry<Feature, String>> featuresToInstall;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding hasNoCodeStyle = chkEslintAndPrettier.selectedProperty()
                .and(cmbCodeStyle.getSelectionModel().selectedItemProperty().isNull());

        BooleanBinding hasNoSgbd = chkSequelize.selectedProperty()
                .and(cmbSgbd.getSelectionModel().selectedItemProperty().isNull());

        BooleanBinding isCreateButtonDisable = tfProjectName.textProperty().isEmpty()
                .or(radioGroup.selectedToggleProperty().isNull())
                .or(hasNoCodeStyle)
                .or(hasNoSgbd);

        btnCreateProject.disableProperty().bind(isCreateButtonDisable);

        cmbCodeStyle.disableProperty().bind(chkEslintAndPrettier.selectedProperty().not());
        cmbSgbd.disableProperty().bind(chkSequelize.selectedProperty().not());

        cmbCodeStyle.getItems().addAll(CodeStyle.values());
        cmbSgbd.getItems().addAll(SGBD.values());

        startMonitoringFeatures();
    }

    @FXML
    private void createProject() {
        String projectName = tfProjectName.getText();
        String selectedPackageManagerName = ((RadioButton) radioGroup.getSelectedToggle()).getText();
        PackageManagers packageManager = PackageManagers.valueOf(selectedPackageManagerName.toUpperCase());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(projectName);
        File directoryToSave = fileChooser.showSaveDialog(App.getWindow());

        if (directoryToSave != null) {
            final int numberOfFeatures = featuresToInstall.size();
          Entry<Feature, String>[] featuresList = featuresToInstall.values()
                        .toArray(new Entry[numberOfFeatures]);
            try {
                this.openProgressInstallerDialog(projectName, directoryToSave, packageManager, featuresList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMonitoringFeatures() {
        this.featuresToInstall = new HashMap<>();
        featuresToInstall.put(Features.DOTENV, Map.entry(new Dotenv(), ""));

        chkBabel.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.BABEL, Map.entry(new Babel(), ""));
            } else {
                featuresToInstall.remove(Features.BABEL);
            }
        });

        chkNodemon.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.NODEMON, Map.entry(new Nodemon(), ""));
            } else {
                featuresToInstall.remove(Features.NODEMON);
            }
        });

        chkJest.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.JEST, Map.entry(new Jest(), ""));
            } else {
                featuresToInstall.remove(Features.JEST);
            }
        });

        chkMongoose.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.MONGOOSE, Map.entry(new Mongoose(), ""));
            } else {
                featuresToInstall.remove(Features.MONGOOSE);
            }
        });

        chkExpress.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.EXPRESS, Map.entry(new Express(), ""));
            } else {
                featuresToInstall.remove(Features.EXPRESS);
            }
        });

        chkEslintAndPrettier.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                CodeStyle style = cmbCodeStyle.getSelectionModel().getSelectedItem();
                featuresToInstall.put(Features.ESLINT_AND_PRETTIER,
                        Map.entry(new ESLintAndPrettier(style), "")
                );
            } else {
                featuresToInstall.remove(Features.ESLINT_AND_PRETTIER);
            }
        });

        cmbCodeStyle.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) -> {
                    Entry<Feature, String> featureEntry = featuresToInstall.get(Features.ESLINT_AND_PRETTIER);
                    ESLintAndPrettier eslintPrettierInstaller =
                            (ESLintAndPrettier) featureEntry.getKey();
                    eslintPrettierInstaller.setCodeStyle(nv);
                });

        chkSequelize.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                SGBD sgbd = cmbSgbd.getSelectionModel().getSelectedItem();
                featuresToInstall.put(Features.SEQUELIZE,
                        Map.entry(new Sequelize(sgbd), "")
                );
            } else {
                featuresToInstall.remove(Features.SEQUELIZE);
            }
        });

        cmbSgbd.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) -> {
                    Entry<Feature, String> featureEntry = featuresToInstall.get(Features.SEQUELIZE);
                    Sequelize sequelize = (Sequelize) featureEntry.getKey();
                    sequelize.setSgbd(nv);
                });

        chkLodash.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                featuresToInstall.put(Features.LODASH, Map.entry(new Lodash(), ""));
            } else {
                featuresToInstall.remove(Features.LODASH);
            }
        });
    }

    private void openProgressInstallerDialog(String projectName,
                                             File projectDir,
                                             PackageManagers packageManager,
                                             Map.Entry<Feature, String>[] featuresList
    ) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Screen.PROGRESS_INDICATOR_DIALOG.getFxmlFilePath()));
        FXMLProgressIndicatorDialog dialogController =
                new FXMLProgressIndicatorDialog(projectDir, projectName, packageManager, featuresList);

        loader.setController(dialogController);
        Parent parent = loader.load();
        Scene dialogScene = new Scene(parent, 400, 250);

        Image icon = new Image(getClass().getResourceAsStream("/br/ufop/tomaz/icons/logo-small.png"));
        Stage dialogStage = new Stage(StageStyle.UNDECORATED);
        dialogStage.getIcons().add(icon);
        dialogStage.setTitle("Creating Project");
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(App.getWindow());
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

//    private Task<Void> getInstallFeaturesTask(AppCreator creator) {
//
//        return new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                final int numberOfFeatures = featuresToInstall.size();
//                Entry<Feature, String>[] featuresList = featuresToInstall.values()
//                        .toArray(new Entry[numberOfFeatures]);
//
//                for (int i = 0; i < numberOfFeatures; i++) {
//                    Feature feature = featuresList[i].getKey();
//                    String args = featuresList[i].getValue();
//
//                    updateMessage("Installing ".concat(feature.getName()).concat("..."));
//                    creator.installFeature(feature, args);
//                    updateProgress(i, numberOfFeatures);
//                    updateMessage(feature.getName().concat(" installed successfully."));
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                super.done();
//                updateMessage("All features were installed successfully.");
//                updateProgress(1,1);
//            }
//        };
//    }
}
