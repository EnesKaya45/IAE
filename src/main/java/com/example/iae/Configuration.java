package com.example.iae;

public class Configuration {
    private String command;

    public Configuration(String command) {
        this.command = command;
    }

    public Configuration() {
        this.command = "";
    }

    public void printInfo() {
        System.out.println("Command: " + command);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
