package com.tangykiwi.kiwiclient.util;

import static com.tangykiwi.kiwiclient.KiwiClient.LOGGER;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.Setting;

public class ConfigManager {
    private static Path dir;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        dir = Paths.get(mc.gameDirectory.getPath(), "kiwiclient/");
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
    }

    public static void loadModules(String config) {
        JsonObject jo = readJsonFile("modules_" + config + ".json");

        if (jo == null) return;

        for (Map.Entry<String, JsonElement> e : jo.entrySet()) {
            Module module = KiwiClient.moduleManager.getModule(e.getKey());

            if (module == null) {
                LOGGER.error("Module " + e.getKey() + " not found, skipping");
                continue;
            }

            if (e.getValue().isJsonObject()) {
                JsonObject mo = e.getValue().getAsJsonObject();
                if (mo.has("toggled")) {
                    try {
                        boolean enabled = mo.get("toggled").getAsBoolean();
                        module.setEnabled(enabled);
                    } catch (Exception ex) {
                        LOGGER.error("Error enabling module " + e.getKey() + ", disabling!");
                        module.setEnabled(false);
                    }
                }

                if (mo.has("settings") && mo.get("settings").isJsonObject()) {
                    Map<String, Setting<?>> settingMap = getSettingMap(module.getSettings());

                    for (Map.Entry<String, JsonElement> se : mo.get("settings").getAsJsonObject().entrySet()) {
                        try {
                            Setting<?> s = settingMap.get(se.getKey());
                            if (s != null) s.read(se.getValue());
                            else LOGGER.error("Error loading setting \"" + se.getKey() + "\" for module " + e.getKey());
                        } catch (Exception ex) {
                            LOGGER.error("Error loading setting \"" + se.getKey() + "\" for module " + e.getKey() + ": " + se.getValue());
                        }
                    }
                }
            }
        }
    }

    public static void saveModules(String config) {
        JsonObject jo = new JsonObject();

        for (Module module : KiwiClient.moduleManager.moduleList) {
            JsonObject mo = new JsonObject();
            mo.addProperty("toggled", module.isEnabled());

            JsonObject so = new JsonObject();
            Map<String, Setting<?>> settingMap = getSettingMap(module.getSettings());
            for (Map.Entry<String, Setting<?>> s : settingMap.entrySet()) {
                so.add(s.getKey(), s.getValue().write());
            }

            if (so.size() != 0) mo.add("settings", so);
            if (mo.size() != 0) jo.add(module.getName(), mo);
        }

        setJsonFile("modules_" + config + ".json", jo);
    }

    private static Map<String, Setting<?>> getSettingMap(Collection<Setting<?>> settings) {
        Map<String, Setting<?>> settingMap = new HashMap<>();
        for (Setting<?> s : settings) {
            String name = s.getName();
            int i = 1;
            while (settingMap.containsKey(name))
                name = s.getName() + "$" + i++;

            settingMap.put(name, s);
        }

        return settingMap;
    }

    public static void loadClickGUI(String config) {
        JsonObject jo = readJsonFile("clickgui_" + config + ".json");

        if (jo == null) return;

        for (Map.Entry<String, JsonElement> e : jo.entrySet()) {
            if (!e.getValue().isJsonObject()) continue;

            CategoryWindow w = ClickGUIScreen.INSTANCE.getWindow(e.getKey());
            JsonObject jw = e.getValue().getAsJsonObject();

            try {
                w.x = jw.get("x").getAsInt();
                w.y = jw.get("y").getAsInt();
                w.expanded = jw.get("expanded").getAsBoolean();
            } catch (Exception ex) {
                LOGGER.error("Error loading ClickGUI window \"" + e.getKey() + "\": " + e.getValue());
            }
        }
    }

    public static void saveClickGUI(String config) {
        JsonObject jo = new JsonObject();

        for (CategoryWindow w : ClickGUIScreen.INSTANCE.windows) {
            JsonObject jw = new JsonObject();
            jw.addProperty("x", w.x);
            jw.addProperty("y", w.y);
            jw.addProperty("expanded", w.expanded);
            jo.add(w.title, jw);
        }

        setJsonFile("clickgui_" + config + ".json", jo);
    }

    public static void loadHUD(String config) {
        JsonObject jo = readJsonFile("hud_" + config + ".json");

        if (jo == null) return;

        for (Map.Entry<String, JsonElement> e : jo.entrySet()) {
            if (!e.getValue().isJsonObject()) continue;

            HUDComponent hudComponent = HUDEditorScreen.INSTANCE.getComponent(e.getKey());
            JsonObject jw = e.getValue().getAsJsonObject();

            try {
                hudComponent.setX(jw.get("x").getAsFloat());
                hudComponent.setY(jw.get("y").getAsFloat());
            } catch (Exception ex) {
                LOGGER.error("Error loading HUD component \"" + e.getKey() + "\": " + e.getValue());
            }
        }
    }

    public static void saveHUD(String config) {
        JsonObject jo = new JsonObject();

        for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
            JsonObject jw = new JsonObject();
            jw.addProperty("x", component.getX());
            jw.addProperty("y", component.getY());
            jo.add(component.getName(), jw);
        }

        setJsonFile("hud_" + config + ".json", jo);
    }

    /**
     *  FILE IO UTILITIES
     */

    public static Path getDir() {
        return dir;
    }

    public static boolean fileExists(String path) {
        try {
            return getDir().resolve(path).toFile().exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static String readFile(String path) {
        try {
            return Files.readString(getDir().resolve(path));
        } catch (NoSuchFileException ignored) {

        } catch (Exception e) {
            LOGGER.error("Error Reading File: " + path + "\n" + e);
        }

        return "";
    }

    public static List<String> readFileLines(String path) {
        try {
            return Files.readAllLines(getDir().resolve(path));
        } catch (NoSuchFileException ignored) {

        } catch (Exception e) {
            LOGGER.error("Error Reading File: " + path + "\n" + e);
        }

        return new ArrayList<>();
    }

    public static void createFile(String path) {
        try {
            if (!fileExists(path)) {
                getDir().resolve(path).getParent().toFile().mkdirs();
                Files.createFile(getDir().resolve(path));
            }
        } catch (Exception e) {
            LOGGER.error("Error Creating File: " + path + "\n" + e);
        }
    }

    public static void createEmptyFile(String path) {
        try {
            createFile(path);

            FileWriter writer = new FileWriter(getDir().resolve(path).toFile());
            writer.write("");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error Clearing/Creating File: " + path + "\n" + e);
        }
    }

    public static void appendFile(String path, String content) {
        try {
            String fileContent = new String(Files.readAllBytes(getDir().resolve(path)));
            FileWriter writer = new FileWriter(getDir().resolve(path).toFile(), true);
            writer.write(
                    (fileContent.endsWith("\n") || !fileContent.contains("\n") ? "" : "\n")
                            + content
                            + (content.endsWith("\n") ? "" : "\n"));
            writer.close();
        } catch (Exception e) {
            System.out.println("Error Appending File: " + path + "\n" + e);
        }
    }

    public static void deleteFile(String path) {
        try {
            Files.deleteIfExists(getDir().resolve(path));
        } catch (Exception e) {
            System.out.println("Error Deleting File: " + path + "\n" + e);
        }
    }

    public static void setJsonFile(String path, JsonObject element) {
        createEmptyFile(path);
        appendFile(path, GSON.toJson(element));
    }

    public static JsonObject readJsonFile(String path) {
        String content = readFile(path);

        if (content.isEmpty())
            return null;

        try {
            return JsonParser.parseString(content).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            System.out.println("Error trying to read json file \"" + path + "\", Deleting file!");

            deleteFile(path);
            return null;
        }
    }
}
