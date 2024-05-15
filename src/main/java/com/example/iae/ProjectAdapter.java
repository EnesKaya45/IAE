package com.example.iae;

import com.google.gson.*;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter implements JsonSerializer<Project>, JsonDeserializer<Project> {
    @Override
    public Project deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String configurationTitle = jsonObject.get("configurationTitle").getAsString();
        String filesToCompile = jsonObject.get("filesToCompile").getAsString();
        String mainFileToRun = jsonObject.get("mainFileToRun").getAsString();
        String arguments = jsonObject.get("arguments").getAsString();
        String expectedOutput = jsonObject.get("expectedOutput").getAsString();

        JsonArray submissionZipFilesArray = jsonObject.getAsJsonArray("submissionZipFiles");
        List<File> files = new ArrayList<>();
        for (JsonElement fileElement : submissionZipFilesArray) {
            File file = new File(fileElement.getAsString());
            files.add(file);
        }

        JsonArray resultsArray = jsonObject.getAsJsonArray("results");
        List<Result> results = new ArrayList<>();
        for (JsonElement resultElement : resultsArray) {
            JsonObject resultObject = resultElement.getAsJsonObject();
            File file = new File(resultObject.get("file").getAsString());
            String status = resultObject.get("status").getAsString();
            results.add(new Result(file, status));
        }

        return new Project(
                configurationTitle,
                filesToCompile,
                mainFileToRun,
                arguments,
                expectedOutput,
                files,
                results
        );
    }

    @Override
    public JsonElement serialize(Project project, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("configurationTitle", project.getConfiguration());
        jsonObject.addProperty("filesToCompile", project.getFilesToCompile());
        jsonObject.addProperty("mainFileToRun", project.getMainFileToRun());
        jsonObject.addProperty("arguments", project.getArguments());
        jsonObject.addProperty("expectedOutput", project.getExpectedOutput());

        // Serialize the lists of File and Result objects
        JsonArray submissionZipFilesArray = new JsonArray();
        for (File file : project.getSubmissionZipFiles()) {
            submissionZipFilesArray.add(file.getPath());
        }
        jsonObject.add("submissionZipFiles", submissionZipFilesArray);

        JsonArray resultsArray = new JsonArray();
        for (Result result : project.getResults()) {
            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("file", result.getPath().toString());
            resultObject.addProperty("status", result.getResult());
            resultsArray.add(resultObject);
        }
        jsonObject.add("results", resultsArray);

        return jsonObject;
    }
}