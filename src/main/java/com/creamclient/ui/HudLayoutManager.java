package com.creamclient.ui;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HudLayoutManager {
    public static class WidgetRect {
        public final String id;
        public final String label;
        public float x, y, scale;
        public int baseW, baseH;
        public boolean visible;

        public WidgetRect(String id, String label, float x, float y, float scale, int baseW, int baseH, boolean visible) {
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.baseW = baseW;
            this.baseH = baseH;
            this.visible = visible;
        }
    }

    private static final Map<String, WidgetRect> WIDGETS = new LinkedHashMap<>();

    static {
        resetDefaults();
    }

    private HudLayoutManager() {}

    public static void resetDefaults() {
        WIDGETS.clear();
        WIDGETS.put("watermark", new WidgetRect("watermark", "Watermark", 8, 8, 1.0f, 170, 20, true));
        WIDGETS.put("info", new WidgetRect("info", "Coordinates & Info", 8, 32, 1.0f, 170, 34, true));
        WIDGETS.put("music", new WidgetRect("music", "Music Player", 8, 72, 1.0f, 175, 42, true));
        WIDGETS.put("nearby", new WidgetRect("nearby", "Nearby Players", 8, 120, 1.0f, 175, 64, true));
        WIDGETS.put("waypoints", new WidgetRect("waypoints", "Waypoints HUD", 8, 192, 1.0f, 175, 56, true));
    }

    public static WidgetRect get(String id) {
        return WIDGETS.get(id);
    }

    public static Map<String, WidgetRect> all() {
        return WIDGETS;
    }

    public static JsonObject toJson() {
        JsonObject root = new JsonObject();
        for (WidgetRect w : WIDGETS.values()) {
            JsonObject o = new JsonObject();
            o.addProperty("x", w.x);
            o.addProperty("y", w.y);
            o.addProperty("scale", w.scale);
            o.addProperty("visible", w.visible);
            root.add(w.id, o);
        }
        return root;
    }

    public static void fromJson(JsonObject root) {
        if (root == null) return;
        for (WidgetRect w : WIDGETS.values()) {
            if (root.has(w.id) && root.get(w.id).isJsonObject()) {
                JsonObject o = root.getAsJsonObject(w.id);
                if (o.has("x")) w.x = o.get("x").getAsFloat();
                if (o.has("y")) w.y = o.get("y").getAsFloat();
                if (o.has("scale")) w.scale = Math.max(0.6f, Math.min(2.0f, o.get("scale").getAsFloat()));
                if (o.has("visible")) w.visible = o.get("visible").getAsBoolean();
            }
        }
    }
}
