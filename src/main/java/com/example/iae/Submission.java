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
    This is class is used to process submissions.
     */
    private String name;
    private Project project;
    private Configuration configuration;
    private Path workingDirectory;
    private File zip;
    private String result = "Fail";

    public Submission(String name, Project project, Configuration configuration, Path workingDirectory, Path zipPath) {
        this.name = name;
        this.project = project;
        this.configuration = configuration;
        this.workingDirectory = workingDirectory;
        this.zip = zip;
    }

    @Override
    public void run() {
        try {
            workingDirectory = Files.createTempDirectory(name);
            ZipFile zipFile = new ZipFile(zip.toString());
            zipFile.extractAll(workingDirectory.toAbsolutePath().toString());
            String config = configuration.getCommand()
                    .replace("FILES_TO_COMPILE", project.getFilesToCompile())
                    .replace("FILE_TO_RUN", project.getMainFileToRun())
                    .replace("ARGUMENTS", project.getArguments());
            ArrayList<String> commands = new ArrayList<>();
            commands.add("cmd");
            commands.add("/C");
            Collections.addAll(commands, config.split(" "));
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(workingDirectory.toFile()); // TODO düzgün çalışıyor mu?
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = "";
            StringBuilder output = new StringBuilder();
            while ((s = stdInput.readLine()) != null) output.append(s);
            if (output.toString().equals(project.getExpectedOutput())) result = "Success";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public File getZip() {
        return zip;
    }
}
