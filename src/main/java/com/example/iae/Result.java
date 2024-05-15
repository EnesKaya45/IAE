package com.example.iae;

import java.nio.file.Path;

public class Result {
    Path path;
    String result;

    public Result(Path path, String result) {
        this.path = path;
        this.result = result;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
