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
        boolean isEslintInstalled = isThisPackageInstalled(projectDir, "eslint");

        String packages = (isBabelInstalled) ? "jest babel-jest" : "jest";
        packages = (isEslintInstalled) ? packages.concat(" eslint-plugin-jest") : packages;
        return packages.split(" ");
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

        } catch (IOException e) {
            System.err.println("Jest Installer > Occurred some error when inserting scripts on package.json");
            e.printStackTrace();
        }
    }

    @Override
    public void createConfigFile(File projectDir) {
        boolean isEslintInstalled = isThisPackageInstalled(projectDir, "eslint");
        if (isEslintInstalled) {
            String eslintFilePath = projectDir.getPath().concat("/.eslintrc.json");
            JSONObject config = getEslintConfigJson(projectDir);
            try (Writer writer = new BufferedWriter(new FileWriter(eslintFilePath))) {
                JSONArray extendsArray = (JSONArray) config.get("extends");
                JSONArray plugins = (JSONArray) config.get("plugins");
                JSONObject rules = (JSONObject) config.get("rules");

                extendsArray.add("plugin:jest/recommended");
                extendsArray.add( "plugin:jest/style");
                plugins.add("jest");
                rules.put("jest/no-disabled-tests", "warn");
                rules.put("jest/no-focused-tests", "error");
                rules.put("jest/no-identical-title", "error");
                rules.put("jest/prefer-to-have-length", "warn");
                rules.put("jest/valid-expect", "error");

                config.replace("extends", extendsArray);
                config.replace("plugins", plugins);
                config.replace("rules", rules);

                writer.write(config.toJSONString().replace("\\", ""));
                writer.flush();

            } catch (IOException e) {
                System.err.println("Jest Installer > " +
                        "Occurred and error when trying to create config files.");
            }
        }
    }

    @Override
    public String getName() {
        return "Jest";
    }

    private JSONObject getEslintConfigJson(File projectDir) {
        String eslintFilePath = projectDir.getPath().concat("/.eslintrc.json");
        try (Reader reader = new FileReader(eslintFilePath)) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Jest Installer > " +
                    "Occurred an error when trying to get the config file.");
            return null;
        }
    }
}
