package com.creamclient.config;

import com.creamclient.discord.DiscordRPC;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.module.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class CreamConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private CreamConfig() {}

    private static File getConfigFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "creamclient.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("discordAppId", DiscordRPC.appId);
            for (Module m : ModuleManager.getModules()) {
                JsonObject modObj = new JsonObject();
                modObj.addProperty("enabled", m.isEnabled());
                JsonObject settingsObj = new JsonObject();
                for (Setting s : m.getSettings()) {
                    if (s instanceof Setting.BoolSetting) settingsObj.addProperty(s.getName(), ((Setting.BoolSetting) s).isEnabled());
                    else if (s instanceof Setting.SliderSetting) settingsObj.addProperty(s.getName(), ((Setting.SliderSetting) s).getValue());
                    else if (s instanceof Setting.ModeSetting) settingsObj.addProperty(s.getName(), ((Setting.ModeSetting) s).getMode());
                }
                modObj.add("settings", settingsObj);
                root.add(m.getName(), modObj);
            }
            try (FileWriter writer = new FileWriter(getConfigFile())) { GSON.toJson(root, writer); }
        } catch (Exception ignored) {}
    }

    public static void load() {
        File f = getConfigFile();
        if (!f.exists()) return;
        try (FileReader reader = new FileReader(f)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;
            if (root.has("discordAppId")) DiscordRPC.appId = root.get("discordAppId").getAsString();
            for (Module m : ModuleManager.getModules()) {
                if (!root.has(m.getName())) continue;
                JsonObject modObj = root.getAsJsonObject(m.getName());
                if (modObj.has("enabled")) m.setEnabled(modObj.get("enabled").getAsBoolean());
                if (modObj.has("settings")) {
                    JsonObject sObj = modObj.getAsJsonObject("settings");
                    for (Setting s : m.getSettings()) {
                        if (!sObj.has(s.getName())) continue;
                        JsonElement el = sObj.get(s.getName());
                        if (s instanceof Setting.BoolSetting) ((Setting.BoolSetting) s).setEnabled(el.getAsBoolean());
                        else if (s instanceof Setting.SliderSetting) ((Setting.SliderSetting) s).setValue(el.getAsDouble());
                        else if (s instanceof Setting.ModeSetting) ((Setting.ModeSetting) s).setMode(el.getAsString());
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
