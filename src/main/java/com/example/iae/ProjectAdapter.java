package com.example.iae;

import com.google.gson.*;

import java.io.File;
import java.lang.reflect.Type;
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
        return new Project(
                configurationTitle,
                filesToCompile,
                mainFileToRun,
                arguments,
                expectedOutput
        );
    }

    @Override
    public JsonElement serialize(Project project, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("configurationTitle", project.getConfiguration());
        jsonObject.addProperty("filesToCompile", project.getFilesToCompile());
        jsonObject.addProperty("mainFileToRun", project.getMainFileToRun());
        jsonObject.addProperty("arguments", project.getArguments());
        jsonObject.addProperty("expectedOutput", project.getExpectedOutput());
        return jsonObject;
    }
}