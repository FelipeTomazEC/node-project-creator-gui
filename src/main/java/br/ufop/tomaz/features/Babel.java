package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Babel implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Babel Installer > Installing ...");

        String packageManagerName = packageManager.name();
        String[] packages = getPackagesToInstall(projectDir);
        List<ProcessBuilder> queue = getProcessBuilderQueue(projectDir, packageManagerName, packages);
        queue.forEach(process -> ProcessExecutor.execute(process));

        createDistDirectory(projectDir);
        createConfigFile(projectDir);
        insertScripts(projectDir);
        boolean isExpressInstalled = isThisPackageInstalled(projectDir,"express");
        if(isExpressInstalled){
            new Express().createAppEntryPoint(projectDir);
        }
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
        JSONObject babelConfigJson = new JSONObject();
        JSONArray presets = new JSONArray();
        boolean isJestInstalled = isThisPackageInstalled(projectDir, "jest");

        if (isJestInstalled) {
            JSONArray preset1 = new JSONArray();
            JSONObject targets = new JSONObject();
            targets.put("node", "current");
            preset1.add("@babel/preset-env");
            preset1.add(targets);
            presets.add(preset1);
        } else {
            presets.add("@babel/preset-env");
        }

        babelConfigJson.put("presets", presets);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write(babelConfigJson.toJSONString().replace("\\", ""));
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertScripts(File projectDir) {
        System.out.println("Babel Installer > Inserting Scripts...");
        JSONObject packageJson = getPackageJson(projectDir);
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

        String packageJsonPath = projectDir.getPath().concat("/package.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(packageJsonPath))) {
            writer.write(packageJson.toJSONString().replace("\\", ""));
            writer.flush();
        } catch (IOException e) {
            System.out.println("Babel Installer > Error when trying to insert scripts.");
            e.printStackTrace();
        }
    }

    private String[] getPackagesToInstall(File projectDir) {
        boolean isJestInstalled = isThisPackageInstalled(projectDir, "jest");

        return (isJestInstalled)
                ? "@babel/core @babel/cli @babel/preset-env @babel/node babel-jest".split(" ")
                : "@babel/core @babel/cli @babel/preset-env @babel/node".split(" ");
    }

}
