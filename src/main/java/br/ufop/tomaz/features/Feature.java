package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

public interface Feature {
    //TODO -- Make args a parameter that receives the name of the terminal application based on SO.
    void install(File projectDir, PackageManagers packageManager, String args);

    default void createConfigFile(File projectDir) {
        System.out.println("Method not implemented yet!");
    }

    default JSONObject getPackageJson(File projectDir) {
        String packageJsonPath = projectDir.getPath().concat("/package.json");
        String[] classNameArray = this.getClass().getName().split("\\.");
        String installerName = classNameArray[classNameArray.length - 1].concat(" Installer");

        try (Reader reader = new FileReader(packageJsonPath)) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            String errorMessage = installerName
                    .concat(" > Occurred some error when trying to read package.json.");

            System.err.println(errorMessage);
            e.printStackTrace();
            return null;
        }
    }

    default boolean isThisPackageInstalled(File projectDir, String packageName) {
        JSONObject packageJson = getPackageJson(projectDir);
        JSONObject devDependencies = (JSONObject) Objects.requireNonNull(packageJson).get("devDependencies");
        JSONObject dependencies = (JSONObject) Objects.requireNonNull(packageJson).get("dependencies");
        boolean isInstalledAsDevDependency = false;
        boolean isInstalledAsDependency = false;

        if (dependencies != null) {
            isInstalledAsDependency = dependencies.containsKey(packageName);
        }

        if (devDependencies != null) {
            isInstalledAsDevDependency = devDependencies.containsKey(packageName);
        }

        return isInstalledAsDependency || isInstalledAsDevDependency;
    }

    default String getName(){
        return this.getClass().getName();
    }
}
