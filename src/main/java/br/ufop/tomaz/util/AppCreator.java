package br.ufop.tomaz.util;

import br.ufop.tomaz.features.Feature;
import br.ufop.tomaz.features.Features;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public interface AppCreator {
    void createApp();

    default void createDirectoryStructure(File root) {
        String src = root.getPath().concat("/src");
        File srcDir = new File(src);
        if (srcDir.mkdir()) {
            try (InputStream os = this.getClass().getResourceAsStream("/example-files/index.js")) {
                String indexPath = src.concat("/index.js");
                File index = new File(indexPath);
                Files.copy(os, index.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("App Creator > Occurred some error when creating directory structure.");
                e.printStackTrace();
            }

            File packageJsonFile = new File(root.getPath().concat("/package.json"));
            try (Reader reader = new FileReader(packageJsonFile)) {
                JSONParser parser = new JSONParser();
                JSONObject packageJson = (JSONObject) parser.parse(reader);

                JSONObject scripts = new JSONObject();
                scripts.put("start", "node src/index.js");
                packageJson.put("scripts", scripts);

                BufferedWriter bf = new BufferedWriter(new FileWriter(packageJsonFile));
                bf.write(packageJson.toJSONString().replace("\\", ""));
                bf.flush();
                bf.close();
            } catch (IOException | ParseException e) {
                System.out.println("App Creator > Error when trying to create start script.");
                e.printStackTrace();
            }

            System.out.println("App Creator > Structure created successfully.");
        }
    }

    void installFeatures(Map<Features, Map.Entry<Feature, String>> features);
}
