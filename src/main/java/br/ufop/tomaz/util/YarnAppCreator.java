package br.ufop.tomaz.util;

import br.ufop.tomaz.features.Feature;
import br.ufop.tomaz.features.Features;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class YarnAppCreator implements AppCreator {
    private File projectDirectory;
    private String appName;

    public YarnAppCreator(File projectDirectory, String appName) {
        this.projectDirectory = projectDirectory;
        this.appName = appName;
    }

    @Override
    public void createApp() {
        if (this.projectDirectory.mkdirs()) {
            System.out.println("Yarn App Creator > Creating ".concat(this.appName).concat(" Project..."));

            String yarnCreateCommand = "yarn init -y ".concat(this.appName);
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", yarnCreateCommand);
            processBuilder.directory(this.projectDirectory);
            ProcessExecutor.execute(processBuilder);

            this.createDirectoryStructure(this.projectDirectory);

            System.out.println("Yarn App Creator > Project ".concat(this.appName).concat(" created."));

        } else {
            System.out.println("Occurred some error!");
        }
    }

    @Override
    public void installFeatures(Map<Features, Map.Entry<Feature, String>> features) {
        System.out.println("Yarn App Creator > Installing features...");
        features.values().forEach(item -> {
            Feature feature = item.getKey();
            String args = item.getValue();

            feature.install(this.projectDirectory, PackageManagers.YARN, args);
        });
        System.out.println("Yarn App Creator > Yarn App Creator > Features installed successfully.");
    }

    private String commandBuilder(String... commands) {
        return Arrays.stream(commands)
                .reduce((acc, current) -> acc.concat(" && ").concat(current))
                .orElse("");
    }
}
