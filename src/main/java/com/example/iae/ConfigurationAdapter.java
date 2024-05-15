package com.example.iae;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ConfigurationAdapter implements JsonSerializer<Configuration>, JsonDeserializer<Configuration> {

    @Override
    public Configuration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String command = jsonObject.get("command").getAsString();
        return new Configuration(command);
    }

    @Override
    public JsonElement serialize(Configuration configuration, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("command", configuration.getCommand());
        return jsonObject;
    }
}
