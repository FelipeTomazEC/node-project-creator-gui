package br.ufop.tomaz.util;

import java.io.File;

public class AppCreatorFactory {

    public static AppCreator getAppCreator(PackageManagers packageManager, File projectDirectory, String projectName){
        switch (packageManager){
            case YARN: return new YarnAppCreator(projectDirectory, projectName);
            default: return new NpmAppCreator(projectDirectory, projectName);
        }
    }
}
