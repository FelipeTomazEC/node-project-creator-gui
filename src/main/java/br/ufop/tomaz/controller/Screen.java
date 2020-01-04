package br.ufop.tomaz.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public enum Screen {
    HOME("FXMLHome.fxml"),
    PROGRESS_INDICATOR_DIALOG("FXMLProgressIndicatorDialog.fxml");

    private String fxmlFileName;

    Screen(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
    }

    public String getFxmlFilePath(){
        String separator = "/";
        return separator.concat("br")
                .concat(separator)
                .concat("ufop")
                .concat(separator)
                .concat("tomaz")
                .concat(separator)
                .concat("fxml")
                .concat(separator)
                .concat(this.fxmlFileName);
    }
}
