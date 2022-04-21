package com.tangykiwi.kiwiclient.util;

import com.google.gson.*;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.clickgui.window.ClickGuiWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.window.Window;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.modules.settings.Setting;
import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigManager {
    private static Path dir;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        dir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "kiwiclient/");
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
    }

    public static Path getDir() {
        return dir;
    }

    public static String readFile(String path) {
        try {
            return Files.readString(getDir().resolve(path));
        } catch (NoSuchFileException ignored) {

        } catch (Exception e) {
            System.out.println("Error Reading File: " + path);
        }

        return "";
    }

    public static void createFile(String path) {
        try {
            if (!fileExists(path)) {
                getDir().resolve(path).getParent().toFile().mkdirs();
                Files.createFile(getDir().resolve(path));
            }
        } catch (Exception e) {
            System.out.println("Error Creating File: " + path);
        }
    }

    public static void createEmptyFile(String path) {
        try {
            createFile(path);

            FileWriter writer = new FileWriter(getDir().resolve(path).toFile());
            writer.write("");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error Clearing/Creating File: " + path);
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
            System.out.println("Error Appending File: " + path);
        }
    }

    public static boolean fileExists(String path) {
        try {
            return getDir().resolve(path).toFile().exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static void deleteFile(String path) {
        try {
            Files.deleteIfExists(getDir().resolve(path));
        } catch (Exception e) {
            System.out.println("Error Deleting File: " + path);
        }
    }

    public static void loadModules(String config) {
        JsonObject jo = readJsonFile("modules_" + config +".json");

        if (jo == null)
            return;

        for (Map.Entry<String, JsonElement> e : jo.entrySet()) {
            Module mod = KiwiClient.moduleManager.getModuleByName(e.getKey());

            if (mod == null) {
                System.out.println(e.getKey() + " null mod, skipping");
                continue;
            }

            if (e.getValue().isJsonObject()) {
                JsonObject mo = e.getValue().getAsJsonObject();
                if (mo.has("toggled")) {
                    try {
                        mod.setToggled(mo.get("toggled").getAsBoolean());
                    } catch (Exception ex) {
                        System.out.println("Error enabling " + e.getKey() + ", Disabling!");

                        try {
                            mod.setToggled(false);
                        } catch (Exception ex2) {
                            // ????
                        }
                    }
                }

                if (mo.has("settings") && mo.get("settings").isJsonObject()) {
                    Map<String, Setting<?>> settingMap = getSettingMap(mod.getSettings());

                    for (Map.Entry<String, JsonElement> se : mo.get("settings").getAsJsonObject().entrySet()) {
                        try {
                            Setting<?> s = settingMap.get(se.getKey());
                            if (s != null) {
                                s.read(se.getValue());
                            } else {
                                System.out.println("Error reading setting \"" + se.getKey() + "\" in module " + mod.getName() + ", removed?");
                            }
                        } catch (Exception ex) {
                            System.out.println("Error reading setting \"" + se.getKey() + "\" in module " + mod.getName() + ": " + se.getValue());
                        }
                    }
                }
            }
        }
    }

    public static void saveModules(String config) {
        JsonObject json = new JsonObject();

        for (Module mod : KiwiClient.moduleManager.moduleList) {
            JsonObject modjson = new JsonObject();

            modjson.add("toggled", new JsonPrimitive(mod.isEnabled()));

            JsonObject setjson = new JsonObject();
            Map<String, Setting<?>> settingMap = getSettingMap(mod.getSettings());
            for (Map.Entry<String, Setting<?>> s : settingMap.entrySet()) {
                setjson.add(s.getKey(), s.getValue().write());
            }

            if (setjson.size() != 0)
                modjson.add("settings", setjson);

            if (modjson.size() != 0)
                json.add(mod.getName(), modjson);
        }

        setJsonFile("modules_" + config + ".json", json);
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

    public static void loadClickGui(String config) {
        JsonObject jo = readJsonFile("clickgui_" + config + ".json");

        if (jo == null)
            return;

        for (Map.Entry<String, JsonElement> e : jo.entrySet()) {
            if (!e.getValue().isJsonObject())
                continue;

            for (Window w : ClickGui.clickGui.getWindows()) {
                if (w.title.equals(e.getKey())) {
                    JsonObject jw = e.getValue().getAsJsonObject();

                    try {
                        w.x1 = jw.get("x").getAsInt();
                        w.y1 = jw.get("y").getAsInt();

                        if (w instanceof ClickGuiWindow && jw.has("hidden")) {
                            ((ClickGuiWindow) w).hiding = jw.get("hidden").getAsBoolean();
                        }
                    } catch (Exception ex) {
                        System.out.println("Error trying to load clickgui window: " + e.getKey() + " with data: " + e.getValue());
                    }
                }
            }
        }
    }

    public static void saveClickGui(String config) {
        JsonObject jo = new JsonObject();

        for (Window w : ClickGui.clickGui.getWindows()) {
            JsonObject jw = new JsonObject();
            jw.addProperty("x", w.x1);
            jw.addProperty("y", w.y1);

            if (w instanceof ClickGuiWindow) {
                jw.addProperty("hidden", ((ClickGuiWindow) w).hiding);
            }

            jo.add(w.title, jw);
        }

        setJsonFile("clickgui_" + config + ".json", jo);
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
