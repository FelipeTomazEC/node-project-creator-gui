package br.ufop.tomaz.controller;

import br.ufop.tomaz.App;
import br.ufop.tomaz.features.*;
import br.ufop.tomaz.util.AppCreator;
import br.ufop.tomaz.util.AppCreatorFactory;
import br.ufop.tomaz.util.PackageManagers;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.*;

public class FXMLHomeController implements Initializable {
    @FXML private TextField tfProjectName;
    @FXML private RadioButton rbNpm;
    @FXML private RadioButton rbYarn;
    @FXML private CheckBox chkEslintAndPrettier;
    @FXML private CheckBox chkBabel;
    @FXML private ComboBox<String> cmbCodeStyle;
    @FXML private CheckBox chkNodemon;
    @FXML private CheckBox chkJest;
    @FXML private CheckBox chkExpress;
    @FXML private CheckBox chkMongoose;
    @FXML private CheckBox chkSequelize;
    @FXML private CheckBox chkLodash;
    @FXML private Button btnCreateProject;
    @FXML private ToggleGroup radioGroup;
    private Map<Features, Map.Entry<Feature, String>> featuresToInstall;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding isCreateButtonDisable = tfProjectName.textProperty().isEmpty()
                .or(radioGroup.selectedToggleProperty().isNull());
        btnCreateProject.disableProperty().bind(isCreateButtonDisable);

        cmbCodeStyle.disableProperty().bind(chkEslintAndPrettier.selectedProperty().not());

        cmbCodeStyle.getItems().addAll("AirBnB", "Google", "Standard");
        cmbCodeStyle.getSelectionModel().selectFirst();

        startMonitoringFeatures();
    }

    @FXML
    private void createProject(){
        String projectName = tfProjectName.getText();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(projectName);
        File directoryToSave = fileChooser.showSaveDialog(App.getWindow());

        if(directoryToSave != null){
            String selectedPackageManagerName = ((RadioButton) radioGroup.getSelectedToggle()).getText();
            PackageManagers packageManager = PackageManagers.valueOf(selectedPackageManagerName.toUpperCase());
            AppCreator appCreator = AppCreatorFactory.getAppCreator(packageManager, directoryToSave, projectName);

            appCreator.createApp();
            appCreator.installFeatures(this.featuresToInstall);

        }
    }

    private void startMonitoringFeatures(){
        this.featuresToInstall = new HashMap<>();
        chkBabel.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                featuresToInstall.put(Features.BABEL, Map.entry(new Babel(), ""));
            } else{
                featuresToInstall.remove(Features.BABEL);
            }
        });

        chkNodemon.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                featuresToInstall.put(Features.NODEMON, Map.entry(new Nodemon(), ""));
            } else {
                featuresToInstall.remove(Features.NODEMON);
            }
        });

        chkJest.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                featuresToInstall.put(Features.JEST, Map.entry(new Jest(), ""));
            } else {
                featuresToInstall.remove(Features.JEST);
            }
        });

        chkMongoose.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                featuresToInstall.put(Features.MONGOOSE, Map.entry(new Mongoose(), ""));
            } else {
                featuresToInstall.remove(Features.MONGOOSE);
            }
        });

        chkExpress.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                featuresToInstall.put(Features.EXPRESS, Map.entry(new Express(), ""));
            } else {
                featuresToInstall.remove(Features.EXPRESS);
            }
        });

        chkEslintAndPrettier.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv){
                String codeStyle = cmbCodeStyle.getSelectionModel().getSelectedItem();
                featuresToInstall.put(Features.ESLINT_AND_PRETTIER,
                        Map.entry(new ESLintAndPrettier(), codeStyle)
                );
            } else{
                featuresToInstall.remove(Features.ESLINT_AND_PRETTIER);
            }
        });

       cmbCodeStyle.getSelectionModel()
               .selectedItemProperty()
               .addListener((ob, ov, nv) -> {
                   Map.Entry<Feature, String> featureEntry = featuresToInstall.get(Features.ESLINT_AND_PRETTIER);
                   Feature eslintPrettierInstaller = featureEntry.getKey();
                   featuresToInstall.put(Features.ESLINT_AND_PRETTIER,
                           Map.entry(eslintPrettierInstaller, nv));
               });
    }
}
