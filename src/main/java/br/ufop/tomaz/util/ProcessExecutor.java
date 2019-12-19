package br.ufop.tomaz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessExecutor {
    public static void execute(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String errorLine, line;

            while (process.isAlive()) {
                while ((errorLine = errorStream.readLine()) != null) {
                    System.out.println(errorLine);
                }
                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int code = process.exitValue();
            System.out.println("Process exit with code " + code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
