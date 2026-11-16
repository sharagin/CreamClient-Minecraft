package com.creamclient.config;

import com.creamclient.discord.DiscordRPC;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.module.Setting;
import com.creamclient.ui.CreamChatManager;
import com.creamclient.ui.HudLayoutManager;
import com.creamclient.visual.WaypointManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CreamConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private CreamConfig() {}

    public static File getConfigDir() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config/creamclient");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getProfilesDir() {
        File dir = new File(getConfigDir(), "configs");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private static File getMainConfigFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "creamclient.json");
    }

    public static void save() {
        saveToFile(getMainConfigFile());
        WaypointManager.save();
    }

    public static void load() {
        loadFromFile(getMainConfigFile());
        WaypointManager.load();
    }

    public static void saveProfile(String name) {
        if (name == null || name.trim().isEmpty()) return;
        String safe = name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_");
        saveToFile(new File(getProfilesDir(), safe + ".json"));
    }

    public static boolean loadProfile(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String safe = name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_");
        File f = new File(getProfilesDir(), safe + ".json");
        if (!f.exists()) return false;
        loadFromFile(f);
        save();
        return true;
    }

    public static boolean deleteProfile(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String safe = name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_");
        File f = new File(getProfilesDir(), safe + ".json");
        return f.exists() && f.delete();
    }

    public static List<String> listProfiles() {
        List<String> list = new ArrayList<>();
        File[] files = getProfilesDir().listFiles((d, n) -> n.toLowerCase().endsWith(".json"));
        if (files != null) {
            Arrays.sort(files);
            for (File f : files) {
                list.add(f.getName().substring(0, f.getName().length() - 5));
            }
        }
        return list;
    }

    public static void openConfigsFolder() {
        Util.getPlatform().openFile(getProfilesDir());
    }

    public static void saveToFile(File file) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("discordAppId", DiscordRPC.appId);
            root.addProperty("chatX", CreamChatManager.chatX);
            root.addProperty("chatY", CreamChatManager.chatY);
            root.add("hudLayout", HudLayoutManager.toJson());
            for (Module m : ModuleManager.getModules()) {
                JsonObject modObj = new JsonObject();
                modObj.addProperty("enabled", m.isEnabled());
                modObj.addProperty("key", m.getKey());
                JsonObject settingsObj = new JsonObject();
                for (Setting s : m.getSettings()) {
                    if (s instanceof Setting.BoolSetting) settingsObj.addProperty(s.getName(), ((Setting.BoolSetting) s).isEnabled());
                    else if (s instanceof Setting.SliderSetting) settingsObj.addProperty(s.getName(), ((Setting.SliderSetting) s).getValue());
                    else if (s instanceof Setting.ModeSetting) settingsObj.addProperty(s.getName(), ((Setting.ModeSetting) s).getMode());
                }
                modObj.add("settings", settingsObj);
                root.add(m.getName(), modObj);
            }
            try (FileWriter fw = new FileWriter(file)) {
                GSON.toJson(root, fw);
            }
        } catch (Exception ignored) {}
    }

    public static void loadFromFile(File file) {
        if (!file.exists()) return;
        try (FileReader fr = new FileReader(file)) {
            JsonObject root = GSON.fromJson(fr, JsonObject.class);
            if (root == null) return;
            if (root.has("discordAppId")) DiscordRPC.appId = root.get("discordAppId").getAsString();
            if (root.has("chatX")) CreamChatManager.chatX = root.get("chatX").getAsInt();
            if (root.has("chatY")) CreamChatManager.chatY = root.get("chatY").getAsInt();
            if (root.has("hudLayout") && root.get("hudLayout").isJsonObject()) {
                HudLayoutManager.fromJson(root.getAsJsonObject("hudLayout"));
            }
            for (Module m : ModuleManager.getModules()) {
                JsonElement el = root.get(m.getName());
                if (el == null || !el.isJsonObject()) continue;
                JsonObject modObj = el.getAsJsonObject();
                if (modObj.has("enabled")) m.setEnabled(modObj.get("enabled").getAsBoolean());
                if (modObj.has("key")) m.setKey(modObj.get("key").getAsInt());
                if (modObj.has("settings") && modObj.get("settings").isJsonObject()) {
                    JsonObject settingsObj = modObj.getAsJsonObject("settings");
                    for (Setting s : m.getSettings()) {
                        if (!settingsObj.has(s.getName())) continue;
                        if (s instanceof Setting.BoolSetting) ((Setting.BoolSetting) s).setEnabled(settingsObj.get(s.getName()).getAsBoolean());
                        else if (s instanceof Setting.SliderSetting) ((Setting.SliderSetting) s).setValue(settingsObj.get(s.getName()).getAsDouble());
                        else if (s instanceof Setting.ModeSetting) ((Setting.ModeSetting) s).setMode(settingsObj.get(s.getName()).getAsString());
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
