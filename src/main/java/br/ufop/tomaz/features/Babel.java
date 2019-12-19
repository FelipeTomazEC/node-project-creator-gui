package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Babel implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Babel Installer > Installing ...");

        createDistDirectory(projectDir);
        createConfigFile(projectDir);
        insertScripts(projectDir);

        String packageManagerName = packageManager.name();
        String[] packages = {"@babel/core", "@babel/cli", "@babel/preset-env", "@babel/node"};
        List<ProcessBuilder> queue = getProcessBuilderQueue(projectDir, packageManagerName, packages);
        queue.forEach(process -> ProcessExecutor.execute(process));

        System.out.println("Babel Installer > Babel installed with success!");
    }

    private void createDistDirectory(File projectDir) {
        String distDirPath = projectDir.getPath().concat("/dist");
        File distDir = new File(distDirPath);

        if (distDir.mkdir()) {
            System.out.println("Babel Installer > Dist directory created.");
        } else {
            System.out.println("Babel Installer > An error occurred when creating Dist directory.");
        }
    }

    private String getInstallerCommand(String packageManagerName, String packageName) {
        return packageManagerName.toLowerCase()
                .concat(" ")
                .concat("add -D ")
                .concat(packageName);
    }

    private List<ProcessBuilder> getProcessBuilderQueue(File projectDir,
                                                        String packageManagerName,
                                                        String... packageList) {
        return Arrays.stream(packageList)
                .map(pack -> getInstallerCommand(packageManagerName, pack))
                .map(command -> new ProcessBuilder("powershell.exe", command))
                .map(processBuilder -> processBuilder.directory(projectDir))
                .collect(Collectors.toList());
    }

    @Override
    public void createConfigFile(File projectDir) {
        String filepath = projectDir.getPath().concat("/.babelrc");
        JSONObject config = new JSONObject();
        JSONArray presets = new JSONArray();
        presets.add("@babel/preset-env");

        config.put("presets", presets);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write(config.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertScripts(File projectDir) {
        System.out.println("Babel Installer > Inserting Scripts...");
        String packageJsonPath = projectDir.getPath().concat("/").concat("package.json");
        JSONParser parser = new JSONParser();

        try(Reader reader = new FileReader(packageJsonPath)){
            JSONObject packageJson = (JSONObject) parser.parse(reader);
            JSONObject previousScripts = (JSONObject) packageJson.get("scripts");
            JSONObject scripts = new JSONObject();
            String start = ((String) previousScripts.get("start")).contains("nodemon")
                    ? "nodemon --exec babel-node src/index.js"
                    : "babel-node src/index.js";
            String build = "babel src --out-dir dist";
            String serve = "node dist/index.js";

            scripts.put("start", start);
            scripts.put("build", build);
            scripts.put("serve", serve);
            packageJson.replace("scripts", scripts);

            BufferedWriter bf = new BufferedWriter(new FileWriter(packageJsonPath));
            bf.write(packageJson.toJSONString().replace("\\", ""));
            bf.flush();
            bf.close();
        } catch (ParseException | IOException e) {
            System.out.println("Babel Installer > Error when trying to insert scripts.");
            e.printStackTrace();
        }
    }
}
