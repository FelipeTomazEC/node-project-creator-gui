package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Dotenv implements Feature {

    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Dotenv Installer > Installing...");
        String command = packageManager.name().toLowerCase()
                .concat(" add dotenv");
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", command);
        processBuilder.directory(projectDir);

        ProcessExecutor.execute(processBuilder);
        createConfigFile(projectDir);

        System.out.println("Dotenv Installer > Dotenv installed successfully.");
    }

    @Override
    public void createConfigFile(File projectDir) {
        String dotenvFilePath = projectDir.getPath().concat("/.env");
        File dotenvFile = new File(dotenvFilePath);
        InputStream in = Dotenv.class.getResourceAsStream("/example-files/dotenv-example");

        try {
            Files.copy(in, dotenvFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Dotenv Installer > Error when creating a .env file.");
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Dotenv";
    }
}
