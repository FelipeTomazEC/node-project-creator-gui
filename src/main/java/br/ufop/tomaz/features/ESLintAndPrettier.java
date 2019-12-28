package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ESLintAndPrettier implements Feature {

    private CodeStyle codeStyle;

    public ESLintAndPrettier(CodeStyle codeStyle){
        this.codeStyle = codeStyle;
    }

    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("ESLint and Prettier Installer > Installing...");

        List<String> packagesToInstall = getPackages();
        String terminalAppName = "powershell.exe";
        List<ProcessBuilder> processBuilderList = packagesToInstall.stream()
                .map(pkg -> getCommand(packageManager, pkg))
                .map(command -> getProcessBuilder(projectDir, terminalAppName, command))
                .collect(Collectors.toList());

        processBuilderList.forEach(ProcessExecutor::execute);

        createConfigFile(projectDir);

        System.out.println("ESLint and Prettier Installer > ESLint and Prettier installed successfully.");
    }

    @Override
    public void createConfigFile(File projectDir) {
        JSONObject eslintConfig = eslintConfigJson();
        JSONObject prettierConfig = prettierConfigJson();
        String eslintConfigFilePath = projectDir.getPath().concat("/.eslintrc.json");
        String prettierConfigFilePath = projectDir.getPath().concat("/.prettierrc");

        try (
             BufferedWriter writer1 = new BufferedWriter(new FileWriter(eslintConfigFilePath));
             BufferedWriter writer2 = new BufferedWriter(new FileWriter(prettierConfigFilePath))
        ) {
            writer1.write(eslintConfig.toJSONString().replace("\\", ""));
            writer2.write(prettierConfig.toJSONString().replace("\\", ""));
            writer1.flush();
            writer2.flush();
        } catch (IOException e) {
            System.err.println("Eslint and Prettier Installer > " +
                    "Occurred and error when trying to create config files.");
            e.printStackTrace();
        }
    }

    private List<String> getPackages() {
        String[] commonPackageNames = {
                "eslint",
                "prettier",
                "eslint-config-prettier",
                "eslint-plugin-prettier",
                "eslint-plugin-import"
        };

        List<String> packagesList = new ArrayList<>();
        packagesList.addAll(Arrays.asList(commonPackageNames));
        packagesList.addAll(Arrays.asList(this.codeStyle.getRequiredPackages()));

        return packagesList;
    }

    private String getCommand(PackageManagers packageManager, String packageName) {
        return packageManager.name()
                .toLowerCase()
                .concat(" add -D ")
                .concat(packageName);
    }

    private ProcessBuilder getProcessBuilder(File projectDir, String terminalAppName, String commands) {
        ProcessBuilder processBuilder = new ProcessBuilder(terminalAppName, commands);
        return processBuilder.directory(projectDir);
    }

    private JSONObject eslintConfigJson() {
        JSONObject config = new JSONObject();
        JSONArray extendsArray = new JSONArray();
        JSONArray plugins = new JSONArray();
        JSONObject rules = new JSONObject();
        JSONArray rule1 = new JSONArray();

        extendsArray.addAll(Arrays.asList(this.codeStyle.getExtendName(), "prettier"));
        plugins.add("prettier");
        rule1.add("error");
        rules.put("prettier/prettier", rule1);

        config.put("extends", extendsArray);
        config.put("plugins", plugins);
        config.put("rules", rules);

        return config;
    }

    private JSONObject prettierConfigJson() {
        JSONObject config = new JSONObject();
        config.put("printWidth", 100);
        config.put("singleQuote", true);

        return config;
    }

    public void setCodeStyle(CodeStyle codeStyle) {
        this.codeStyle = codeStyle;
    }
}
