package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Nodemon implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Nodemon Installer > Installing...");
        String command = packageManager.name().toLowerCase()
                .concat(" add -D nodemon");
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", command);
        processBuilder.directory(projectDir);

        ProcessExecutor.execute(processBuilder);

        insertScripts(projectDir);

        System.out.println("Nodemon Installer > Nodemon installed successfully.");
    }

    public void insertScripts(File projectDir) {
        System.out.println("Nodemon Installer > Inserting scripts on package.json...");

        String packageJsonPath = projectDir.getPath().concat("/package.json");
        try (Reader reader = new FileReader(packageJsonPath)) {
            JSONParser parser = new JSONParser();
            JSONObject packageJson = (JSONObject) parser.parse(reader);
            JSONObject scripts = (JSONObject) packageJson.get("scripts");
            String previousStartScript = (String) scripts.get("start");
            String start = previousStartScript.contains("babel")
                    ? "nodemon --exec ".concat(previousStartScript)
                    : "nodemon src/index.js";

            scripts.replace("start", start);
            packageJson.replace("scripts", scripts);

            BufferedWriter bf = new BufferedWriter(new FileWriter(packageJsonPath));
            bf.write(packageJson.toJSONString().replace("\\", ""));
            bf.flush();
            bf.close();

        } catch (IOException | ParseException e) {
            System.err.println("Nodemon Installer > Occurred some error when trying to insert scripts.");
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Nodemon";
    }
}
