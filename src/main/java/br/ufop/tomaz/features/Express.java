package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Express implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Express Installer > Installing...");

        String command = packageManager.name().toLowerCase()
                .concat(" add express");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(projectDir);
        processBuilder.command("powershell.exe", command);
        ProcessExecutor.execute(processBuilder);

        createAppEntryPoint(projectDir);
        System.out.println("Express Installer > Express installed successfully.");
    }

    public void createAppEntryPoint(File projectDir) {
        boolean isBabelInstalled = isThisPackageInstalled(projectDir, "@babel/core");
        String resourceName = isBabelInstalled
                ? "/example-files/express/index-babel.js"
                : "/example-files/express/index.js";
        String indexFilePath = projectDir.getPath().concat("/src/index.js");
        File index = new File(indexFilePath);

        try (InputStream in = Express.class.getResourceAsStream(resourceName)) {
            Files.copy(in, index.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Express Installer > Occurred an error when creating a index.js.");
            e.printStackTrace();
        }
    }
}
