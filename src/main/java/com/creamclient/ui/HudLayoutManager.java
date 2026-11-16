package com.creamclient.ui;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HudLayoutManager {
    public static class WidgetRect {
        public final String id;
        public final String labelEn;
        public final String labelRu;
        public float x, y, scale;
        public int baseW, baseH;
        public boolean visible;
        public boolean compact;

        public WidgetRect(String id, String labelEn, String labelRu, float x, float y, float scale, int baseW, int baseH, boolean visible, boolean compact) {
            this.id = id;
            this.labelEn = labelEn;
            this.labelRu = labelRu;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.baseW = baseW;
            this.baseH = baseH;
            this.visible = visible;
            this.compact = compact;
        }
    }

    private static final Map<String, WidgetRect> WIDGETS = new LinkedHashMap<>();

    static {
        resetDefaults();
    }

    private HudLayoutManager() {}

    public static void resetDefaults() {
        WIDGETS.clear();
        WIDGETS.put("watermark", new WidgetRect("watermark", "Watermark", "Водяной знак", 8, 8, 1.0f, 175, 20, true, false));
        WIDGETS.put("info", new WidgetRect("info", "Coordinates & Info", "Координаты и Инфо", 8, 32, 1.0f, 175, 34, true, false));
        WIDGETS.put("music", new WidgetRect("music", "Music Player", "Музыкальный плеер", 8, 72, 1.0f, 178, 44, true, false));
        WIDGETS.put("nearby", new WidgetRect("nearby", "Nearby Players", "Игроки рядом", 8, 122, 1.0f, 178, 64, true, false));
        WIDGETS.put("waypoints", new WidgetRect("waypoints", "Waypoints HUD", "Метки HUD", 8, 194, 1.0f, 178, 56, true, false));
        WIDGETS.put("keystrokes", new WidgetRect("keystrokes", "Keystrokes & CPS", "Клавиши и CPS", 200, 8, 1.0f, 96, 62, true, false));
        WIDGETS.put("session", new WidgetRect("session", "Session Stats", "Статистика сессии", 200, 76, 1.0f, 140, 36, true, false));
        WIDGETS.put("armor", new WidgetRect("armor", "Armor & Potions", "Броня и Зелья", 350, 8, 1.0f, 120, 42, true, false));
        WIDGETS.put("weather_orb", new WidgetRect("weather_orb", "Weather Mini-Orb", "Мини-погода", 480, 8, 1.0f, 34, 34, true, false));
        WIDGETS.put("chat", new WidgetRect("chat", "Custom Chat", "Чат клиента", 8, 260, 1.0f, 240, 90, true, false));
    }

    public static WidgetRect get(String id) { return WIDGETS.get(id); }
    public static Map<String, WidgetRect> all() { return WIDGETS; }

    public static JsonObject toJson() {
        JsonObject root = new JsonObject();
        for (WidgetRect w : WIDGETS.values()) {
            JsonObject o = new JsonObject();
            o.addProperty("x", w.x);
            o.addProperty("y", w.y);
            o.addProperty("scale", w.scale);
            o.addProperty("visible", w.visible);
            o.addProperty("compact", w.compact);
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
                if (o.has("scale")) w.scale = Math.max(0.5f, Math.min(2.2f, o.get("scale").getAsFloat()));
                if (o.has("visible")) w.visible = o.get("visible").getAsBoolean();
                if (o.has("compact")) w.compact = o.get("compact").getAsBoolean();
            }
        }
    }
}
