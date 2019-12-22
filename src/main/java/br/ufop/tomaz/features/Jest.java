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

public class Jest implements Feature {
    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Jest Installer > Installing...");

        String[] packages = getPackagesToInstall(projectDir);
        List<ProcessBuilder> queue = getProcessBuilderQueue(projectDir, packageManager.name(), packages);
        queue.forEach(ProcessExecutor::execute);

        insertScripts(projectDir);
        createConfigFile(projectDir);

        System.out.println("Jest Installer > Jest installed successfully.");
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

    private String[] getPackagesToInstall(File projectDir) {
        boolean isBabelInstalled = isThisPackageInstalled(projectDir, "@babel/core");

        return (isBabelInstalled)
                ? "jest babel-jest".split(" ")
                : "jest".split(" ");
    }

    private void insertScripts(File projectDir) {
        JSONObject packageJson = getPackageJson(projectDir);
        assert packageJson != null;
        JSONObject scripts = (JSONObject) packageJson.get("scripts");
        scripts.put("test", "clear && jest");
        packageJson.replace("scripts", scripts);

        String packageJsonPath = projectDir.getPath().concat("/package.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(packageJsonPath))) {
            writer.write(packageJson.toJSONString().replace("\\", ""));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Jest Installer > Occurred some error when inserting scripts on package.json");
            e.printStackTrace();
        }
    }

    @Override
    public void createConfigFile(File projectDir) {
        boolean isBabelInstalled = isThisPackageInstalled(projectDir, "@babel/core");

        if (isBabelInstalled) {
            String babelConfigFilePath = projectDir.getPath().concat("/.babelrc");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(babelConfigFilePath))) {
                JSONObject babelConfigJson = new JSONObject();
                JSONArray presets = new JSONArray();
                JSONArray preset1 = new JSONArray();

                String presetOneItem1 = "@babel/preset-env";
                JSONObject presetOneItem2 = new JSONObject();
                JSONObject targets = new JSONObject();
                targets.put("node", "current");
                presetOneItem2.put("targets", targets);

                preset1.add(presetOneItem1);
                preset1.add(presetOneItem2);

                presets.add(preset1);

                babelConfigJson.put("presets", presets);

                writer.write(babelConfigJson.toJSONString().replace("\\", ""));
                writer.flush();

                System.out.println("Jest Installer > Integrated with Babel.");
            } catch (IOException e) {
                System.err.println("Jest Installer > Occurred an error when trying to modified babel config file.");
                e.printStackTrace();
            }
        }
    }
}
