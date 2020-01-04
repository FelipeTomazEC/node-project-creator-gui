package br.ufop.tomaz.features;

import br.ufop.tomaz.util.PackageManagers;
import br.ufop.tomaz.util.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Sequelize implements Feature {
    private SGBD sgbd;

    public Sequelize(SGBD sgbd){
        this.sgbd = sgbd;
    }

    @Override
    public void install(File projectDir, PackageManagers packageManager, String args) {
        System.out.println("Sequelize Installer > Installing...");

        String command = packageManager.name().toLowerCase()
                .concat(" add sequelize");

        String sgbdPackages = Arrays.stream(sgbd.getRequiredPackagesNames())
                .reduce((acc, current) -> acc.concat(" ").concat(current))
                .orElse("");

        command = command.concat(" ").concat(sgbdPackages);

        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", command);
        processBuilder.directory(projectDir);
        ProcessExecutor.execute(processBuilder);

        createDirectories(projectDir);
        createConfigFile(projectDir);

        System.out.println("Sequelize Installer > Sequelize installed successfully.");
    }

    @Override
    public void createConfigFile(File projectDir) {
        String sequelizercFilePath = projectDir.getPath().concat("/.sequelizerc");
        String configFilePath = projectDir.getPath().concat("/src/database/config/config.js");
        File sequelizercFile = new File(sequelizercFilePath);
        File configFile = new File(configFilePath);

        InputStream seqIn = Sequelize.class.getResourceAsStream("/example-files/sequelize/sequelize-rc-example.js");
        InputStream confIn = Sequelize.class.getResourceAsStream("/example-files/sequelize/config-example.js");
        try {
            Files.copy(seqIn, sequelizercFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(confIn, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Sequelize Installer > Occurred an error when creating the config file.");
            e.printStackTrace();
        }
    }

    private void createDirectories(File projectDir){
        String databaseDirPath = projectDir.getPath().concat("/src/database");
        File databaseDir = new File(databaseDirPath);

        if(databaseDir.mkdir()){
            File config = new File(databaseDirPath.concat("/config"));
            File models = new File(databaseDirPath.concat("/models"));
            File seeders = new File(databaseDirPath.concat("/seeders"));
            File migrations = new File(databaseDirPath.concat("/migrations"));

            boolean isDirectoriesStructureCreated = config.mkdir()
                    && models.mkdir()
                    && seeders.mkdir()
                    && migrations.mkdir();

            if(!isDirectoriesStructureCreated){
                System.err.println("Sequelize Installer > " +
                        "Occurred an error when creating the directories structure.");
            }
        }
    }

    public void setSgbd(SGBD sgbd) {
        this.sgbd = sgbd;
    }

    @Override
    public String getName() {
        return "Sequelize";
    }
}

