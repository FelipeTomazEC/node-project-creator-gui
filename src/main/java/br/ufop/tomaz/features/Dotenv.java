package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;
import org.json.simple.JSONObject;

import java.io.*;
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
        updateScripts(projectDir);

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

    public void updateScripts(File projectDir) {
        JSONObject packageJson = getPackageJson(projectDir);
        JSONObject scripts = (JSONObject) packageJson.get("scripts");
        boolean isBabelInstalled = isThisPackageInstalled(projectDir, "@babel/node");

        String start = ((String) scripts.get("start"))
                .replace("src/index.js", "-r dotenv/config src/index.js");
        scripts.replace("start", start);

        if (isBabelInstalled) {
            String serve = "node -r dotenv/config dist/index.js";
            scripts.replace("serve", serve);
        }

        packageJson.replace("scripts", scripts);

        String packageJsonPath = projectDir.getPath().concat("/package.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(packageJsonPath))) {
            writer.write(packageJson.toJSONString().replace("\\", ""));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "Dotenv";
    }
}
