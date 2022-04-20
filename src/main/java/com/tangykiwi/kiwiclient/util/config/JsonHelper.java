package com.tangykiwi.kiwiclient.util.config;

import com.google.gson.*;

public class JsonHelper {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void setJsonFile(String path, JsonObject element) {
        ConfigManager.createEmptyFile(path);
        ConfigManager.appendFile(path, GSON.toJson(element));
    }

    public static JsonObject readJsonFile(String path) {
        String content = ConfigManager.readFile(path);

        if (content.isEmpty())
            return null;

        try {
            return JsonParser.parseString(content).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            System.out.println("Error trying to read json file \"" + path + "\", Deleting file!");

            ConfigManager.deleteFile(path);
            return null;
        }
    }
}
