package com.example.iae;

import java.io.File;
import java.util.List;

public class Project {
    private String configurationTitle;
    private String filesToCompile;
    private String mainFileToRun;
    private String arguments;
    private String expectedOutput;
    private List<File> submissionZipFiles;
    private List<Result> results;

    public Project(String configuration,
                   String filesToCompile,
                   String mainFileToRun,
                   String arguments,
                   String expectedOutput,
                   List<File> submissionZipFiles,
                   List<Result> results) {
        this.configurationTitle = configuration;
        this.filesToCompile = filesToCompile;
        this.mainFileToRun = mainFileToRun;
        this.arguments = arguments;
        this.expectedOutput = expectedOutput;
        this.submissionZipFiles = submissionZipFiles;
        this.results = results;
    }

    public String getConfiguration() {
        return configurationTitle;
    }

    public String getFilesToCompile() {
        return filesToCompile;
    }

    public String getMainFileToRun() {
        return mainFileToRun;
    }

    public String getArguments() {
        return arguments;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setConfiguration(String configuration) {
        this.configurationTitle = configuration;
    }

    public void setFilesToCompile(String filesToCompile) {
        this.filesToCompile = filesToCompile;
    }

    public void setMainFileToRun(String mainFileToRun) {
        this.mainFileToRun = mainFileToRun;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
