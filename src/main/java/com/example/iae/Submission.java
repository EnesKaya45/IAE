package com.example.iae;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Submission implements Runnable{
    /**
    This  class is used to process submissions.
     */
    private String name;
    private Project project;
    private Configuration configuration;
    private Path workingDirectory;
    private File zip;
    private String result = "Fail";

    public Submission(String name, Project project, Configuration configuration, File zip) {
        this.name = name;
        this.project = project;
        this.configuration = configuration;
        this.zip = zip;
    }

    @Override
    public void run() {
        try {
            workingDirectory = Files.createTempDirectory(name);
            try (ZipFile zipFile = new ZipFile(zip)) {
                zipFile.extractAll(workingDirectory.toAbsolutePath().toString());
            }
            String config = configuration.getCommand()
                    .replace("FILES_TO_COMPILE", project.getFilesToCompile())
                    .replace("FILE_TO_RUN", project.getMainFileToRun())
                    .replace("ARGUMENTS", project.getArguments());
            System.out.println("Çalıştırılan komut: " + config);
            ArrayList<String> commands = new ArrayList<>();
            commands.add("cmd");
            commands.add("/C");
            Collections.addAll(commands, config.split(" "));
            // commands.add("exit");

            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(workingDirectory.resolve(removeExtension(name)).toFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = "";
            StringBuilder output = new StringBuilder();
            while ((s = stdInput.readLine()) != null) output.append(s);
            System.out.println(output);
            if (output.toString().equals(project.getExpectedOutput())) result = "Success";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResult() {
        return result;
    }

    public File getZip() {
        return zip;
    }

    public String removeExtension(String s) {
        if (s.indexOf(".") > 0) {
            return s.substring(0, s.lastIndexOf("."));
        } else {
            return s;
        }
    }
}
