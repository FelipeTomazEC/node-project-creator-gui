package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;

import java.io.File;

public class Lodash implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Lodash Installer > Installing...");

        String command = packageManager.name().toLowerCase()
                .concat(" add lodash");

        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", command);
        processBuilder.directory(projectDir);
        ProcessExecutor.execute(processBuilder);

        System.out.println("Lodash Installer > Lodash installed successfully.");
    }

    @Override
    public String getName() {
        return "Lodash";
    }
}
