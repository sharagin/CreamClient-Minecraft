package com.creamclient.theme;

import com.creamclient.module.ModuleManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class CustomColorManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<SavedColor> COLORS = new ArrayList<>();

    static {
        COLORS.add(new SavedColor("Neon Lime", 163, 255, 18));
        COLORS.add(new SavedColor("Cyber Mint", 46, 255, 178));
        COLORS.add(new SavedColor("Sakura Pink", 255, 92, 168));
        COLORS.add(new SavedColor("Electric Sky", 56, 189, 248));
        COLORS.add(new SavedColor("Sunset Gold", 255, 168, 45));
    }

    private CustomColorManager() {}

    public static class SavedColor {
        public String name;
        public int r, g, b;

        public SavedColor(String name, int r, int g, int b) {
            this.name = name;
            this.r = Math.max(0, Math.min(255, r));
            this.g = Math.max(0, Math.min(255, g));
            this.b = Math.max(0, Math.min(255, b));
        }

        public int toArgb() {
            return (0xFF << 24) | (r << 16) | (g << 8) | b;
        }

        public String toHex() {
            return String.format("#%02X%02X%02X", r, g, b);
        }
    }

    public static List<SavedColor> getColors() { return COLORS; }

    private static File getFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config/creamclient");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "colors.json");
    }

    public static void addColor(String name, int r, int g, int b) {
        String clean = (name == null || name.trim().isEmpty()) ? ("Color #" + (COLORS.size() + 1)) : name.trim();
        COLORS.add(new SavedColor(clean, r, g, b));
        save();
    }

    public static void removeColor(int index) {
        if (index >= 0 && index < COLORS.size()) {
            COLORS.remove(index);
            save();
        }
    }

    public static void applyColor(int r, int g, int b) {
        if (ModuleManager.CUSTOM_R != null) ModuleManager.CUSTOM_R.setValue(r);
        if (ModuleManager.CUSTOM_G != null) ModuleManager.CUSTOM_G.setValue(g);
        if (ModuleManager.CUSTOM_B != null) ModuleManager.CUSTOM_B.setValue(b);
        if (ModuleManager.COLOR_MODE != null) ModuleManager.COLOR_MODE.setMode("CustomRGB");
    }

    public static void applySaved(int index) {
        if (index < 0 || index >= COLORS.size()) return;
        SavedColor c = COLORS.get(index);
        applyColor(c.r, c.g, c.b);
    }

    public static boolean applyHex(String hex) {
        if (hex == null) return false;
        String h = hex.trim().replace("#", "");
        if (h.length() != 6) return false;
        try {
            int val = Integer.parseInt(h, 16);
            applyColor((val >> 16) & 0xFF, (val >> 8) & 0xFF, val & 0xFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String currentHex() {
        int r = ModuleManager.CUSTOM_R != null ? (int) ModuleManager.CUSTOM_R.getValue() : 163;
        int g = ModuleManager.CUSTOM_G != null ? (int) ModuleManager.CUSTOM_G.getValue() : 255;
        int b = ModuleManager.CUSTOM_B != null ? (int) ModuleManager.CUSTOM_B.getValue() : 18;
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static void save() {
        try {
            JsonArray arr = new JsonArray();
            for (SavedColor c : COLORS) {
                JsonObject o = new JsonObject();
                o.addProperty("name", c.name);
                o.addProperty("r", c.r);
                o.addProperty("g", c.g);
                o.addProperty("b", c.b);
                arr.add(o);
            }
            try (FileWriter fw = new FileWriter(getFile())) {
                GSON.toJson(arr, fw);
            }
        } catch (Exception ignored) {}
    }

    public static void load() {
        File f = getFile();
        if (!f.exists()) return;
        try (FileReader fr = new FileReader(f)) {
            JsonArray arr = GSON.fromJson(fr, JsonArray.class);
            if (arr == null || arr.size() == 0) return;
            COLORS.clear();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject o = el.getAsJsonObject();
                COLORS.add(new SavedColor(
                        o.has("name") ? o.get("name").getAsString() : "Custom",
                        o.has("r") ? o.get("r").getAsInt() : 163,
                        o.has("g") ? o.get("g").getAsInt() : 255,
                        o.has("b") ? o.get("b").getAsInt() : 18
                ));
            }
        } catch (Exception ignored) {}
    }
}
