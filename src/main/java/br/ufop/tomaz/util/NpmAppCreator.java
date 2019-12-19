package br.ufop.tomaz.util;

import br.ufop.tomaz.features.Feature;
import br.ufop.tomaz.features.Features;

import java.io.File;
import java.util.Map;

public class NpmAppCreator implements AppCreator {
    private File projectDirectory;
    private String appName;

    public NpmAppCreator(File projectDirectory, String appName) {
        this.projectDirectory = projectDirectory;
        this.appName = appName;
    }
    @Override
    public void createApp() {
        if (this.projectDirectory.mkdirs()) {
            System.out.println("Npm App Creator > Creating ".concat(this.appName).concat(" Project..."));

            String npmCreateCommand = "npm init --yes";
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", npmCreateCommand);
            processBuilder.directory(this.projectDirectory);
            ProcessExecutor.execute(processBuilder);

            this.createDirectoryStructure(this.projectDirectory);

            System.out.println("Npm App Creator > Project ".concat(this.appName).concat(" created."));

        } else {
            System.out.println("Occurred some error!");
        }
    }

    @Override
    public void installFeatures(Map<Features, Map.Entry<Feature, String>> features) {
        features.values().forEach((item) -> {
            Feature feature = item.getKey();
            String args = item.getValue();

            feature.install(this.projectDirectory, PackageManagers.NPM, args);
        });
    }
}
