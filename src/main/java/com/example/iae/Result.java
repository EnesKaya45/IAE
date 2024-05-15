package com.example.iae;

import java.io.File;
import java.nio.file.Path;

public class Result {
    File file;
    String result;

    public Result(File file, String result) {
        this.file = file;
        this.result = result;
    }

    public File getPath() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
