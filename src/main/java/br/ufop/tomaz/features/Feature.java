package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;

import java.io.File;

public interface Feature {
    void install(File projectDir, PackageManagers packageManager, String args);
    default void createConfigFile(File projectDir){
        System.out.println("Method not implemented yet!");
    };
}
