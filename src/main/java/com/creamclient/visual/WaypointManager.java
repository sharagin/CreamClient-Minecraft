package com.creamclient.visual;

import com.creamclient.media.SoundFX;
import com.creamclient.ui.NotificationManager;
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

public final class WaypointManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    private WaypointManager() {}

    public static class Waypoint {
        public String name;
        public double x, y, z;
        public String dimension;
        public int color;
        public boolean enabled;

        public Waypoint(String name, double x, double y, double z, String dimension, int color, boolean enabled) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.color = color;
            this.enabled = enabled;
        }
    }

    public static List<Waypoint> getWaypoints() {
        return WAYPOINTS;
    }

    private static File getFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config/creamclient");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "waypoints.json");
    }

    public static String getCurrentDimension() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return "minecraft:overworld";
        return mc.level.dimension().location().toString();
    }

    public static void addWaypoint(String name, double x, double y, double z, String dim, int color) {
        WAYPOINTS.add(new Waypoint(name, Math.round(x * 10.0) / 10.0, Math.round(y * 10.0) / 10.0, Math.round(z * 10.0) / 10.0, dim, color, true));
        save();
    }

    public static void addQuickWaypoint() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        String name = "Point #" + (WAYPOINTS.size() + 1);
        int[] palette = {0xFFA3E635, 0xFF38BDF8, 0xFFF472B6, 0xFFFACC15, 0xFFC084FC, 0xFF34D399};
        int col = palette[WAYPOINTS.size() % palette.length];
        addWaypoint(name, mc.player.getX(), mc.player.getY() + 1.0, mc.player.getZ(), getCurrentDimension(), col);
        SoundFX.waypoint();
        NotificationManager.push("Waypoint", name + " (" + (int) mc.player.getX() + ", " + (int) mc.player.getY() + ", " + (int) mc.player.getZ() + ")", col);
    }

    public static void removeWaypoint(int index) {
        if (index >= 0 && index < WAYPOINTS.size()) {
            WAYPOINTS.remove(index);
            save();
        }
    }

    public static void toggleWaypoint(int index) {
        if (index >= 0 && index < WAYPOINTS.size()) {
            WAYPOINTS.get(index).enabled = !WAYPOINTS.get(index).enabled;
            save();
        }
    }

    public static void clearAll() {
        WAYPOINTS.clear();
        save();
    }

    public static void save() {
        try {
            JsonArray arr = new JsonArray();
            for (Waypoint w : WAYPOINTS) {
                JsonObject o = new JsonObject();
                o.addProperty("name", w.name);
                o.addProperty("x", w.x);
                o.addProperty("y", w.y);
                o.addProperty("z", w.z);
                o.addProperty("dimension", w.dimension);
                o.addProperty("color", w.color);
                o.addProperty("enabled", w.enabled);
                arr.add(o);
            }
            try (FileWriter fw = new FileWriter(getFile())) {
                GSON.toJson(arr, fw);
            }
        } catch (Exception ignored) {}
    }

    public static void load() {
        WAYPOINTS.clear();
        File f = getFile();
        if (!f.exists()) return;
        try (FileReader fr = new FileReader(f)) {
            JsonArray arr = GSON.fromJson(fr, JsonArray.class);
            if (arr == null) return;
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject o = el.getAsJsonObject();
                WAYPOINTS.add(new Waypoint(
                        o.has("name") ? o.get("name").getAsString() : "Point",
                        o.has("x") ? o.get("x").getAsDouble() : 0,
                        o.has("y") ? o.get("y").getAsDouble() : 64,
                        o.has("z") ? o.get("z").getAsDouble() : 0,
                        o.has("dimension") ? o.get("dimension").getAsString() : "minecraft:overworld",
                        o.has("color") ? o.get("color").getAsInt() : 0xFFA3E635,
                        !o.has("enabled") || o.get("enabled").getAsBoolean()
                ));
            }
        } catch (Exception ignored) {}
    }
}
