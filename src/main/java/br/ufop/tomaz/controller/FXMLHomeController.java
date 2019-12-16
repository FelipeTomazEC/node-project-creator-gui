package br.ufop.tomaz.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLHomeController implements Initializable {
    @FXML private TextField tfProjectName;
    @FXML private RadioButton rbNpm;
    @FXML private RadioButton rbYarn;
    @FXML private CheckBox chkEslint;
    @FXML private CheckBox chkPrettier;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding isCreateButtonDisable = tfProjectName.textProperty().isEmpty()
                .or(radioGroup.selectedToggleProperty().isNull());
        btnCreateProject.disableProperty().bind(isCreateButtonDisable);

        BooleanBinding isCodeStyleComboboxDisable = chkBabel.selectedProperty().not()
                .and(chkEslint.selectedProperty().not())
                .and(chkPrettier.selectedProperty().not());
        cmbCodeStyle.disableProperty().bind(isCodeStyleComboboxDisable);

        cmbCodeStyle.getItems().addAll("AirBnB", "Google", "Standard");
        cmbCodeStyle.getSelectionModel().selectFirst();
    }
}
