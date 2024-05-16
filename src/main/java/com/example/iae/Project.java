package com.example.iae;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String configurationTitle = "";
    private String filesToCompile = "";
    private String mainFileToRun = "";
    private String arguments = "";
    private String expectedOutput = "";
    private List<File> submissionZipFiles = new ArrayList<>();
    private List<Result> results = new ArrayList<>();

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

    public Project(String configuration,
                   String filesToCompile,
                   String mainFileToRun,
                   String arguments,
                   String expectedOutput,
                   List<File> submissionZipFiles) {
        this.configurationTitle = configuration;
        this.filesToCompile = filesToCompile;
        this.mainFileToRun = mainFileToRun;
        this.arguments = arguments;
        this.expectedOutput = expectedOutput;
        this.submissionZipFiles = submissionZipFiles;
    }

    public Project(String configuration,
                   String filesToCompile,
                   String mainFileToRun,
                   String arguments,
                   String expectedOutput) {
        this.configurationTitle = configuration;
        this.filesToCompile = filesToCompile;
        this.mainFileToRun = mainFileToRun;
        this.arguments = arguments;
        this.expectedOutput = expectedOutput;
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

    public List<File> getSubmissionZipFiles() {
        return submissionZipFiles;
    }

    public List<Result> getResults() {
        return results;
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

    public void setSubmissionZipFiles(List<File> submissionZipFiles) {
        this.submissionZipFiles = submissionZipFiles;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void debug() {
        System.out.println("Configuration title: " + getConfiguration());
        System.out.println("Files to compile: " + getFilesToCompile());
        System.out.println("Main file to run: " + getMainFileToRun());
        System.out.println("Arguments: " + getArguments());
        System.out.println("Expected output: " + getExpectedOutput());

        System.out.println("Submission files: ");
        for (File f : getSubmissionZipFiles()) System.out.println(f);
        System.out.println("Results: ");
        for (Result r : getResults()) System.out.println(r.getFile() + r.getResult());
    }
}
