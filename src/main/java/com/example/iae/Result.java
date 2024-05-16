package com.example.iae;

import java.io.File;

public class Result {
    File file;
    String result;

    public Result(File file, String result) {
        this.file = file;
        this.result = result;
    }

    public File getFile() {
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
