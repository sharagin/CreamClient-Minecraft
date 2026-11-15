package com.creamclient.module;

import com.creamclient.discord.DiscordRPC;
import com.creamclient.media.MediaManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    // Visuals
    public static Module JUMP_CIRCLES;
    public static Module TRAILS;
    public static Module BOX_ESP;
    public static Module CUSTOM_FOG;
    public static Module TIME_CHANGER;
    public static Module VIEW_MODEL;
    public static Module FULLBRIGHT;
    public static Module NO_HURT_CAM;
    public static Module ZOOM;

    // HUD
    public static Module HUD;
    public static Module WATERMARK;
    public static Module CUSTOM_HOTBAR;
    public static Module COORDS;
    public static Module KEYSTROKES;
    public static Module ARRAYLIST;
    public static Module ARMOR_HUD;
    public static Module CROSSHAIR;

    // Movement
    public static Module TOGGLE_SPRINT;

    // Interface
    public static Module CLIENT_THEME;
    public static Module CUSTOM_MENUS;
    public static Module DISCORD_RPC;

    // Media
    public static Module BACKGROUND_MOD;
    public static Module MUSIC_MOD;

    // Deep customization settings
    public static Setting.SliderSetting CIRCLE_RADIUS;
    public static Setting.SliderSetting CIRCLE_SPEED;
    public static Setting.SliderSetting TRAIL_LENGTH;
    public static Setting.BoolSetting ESP_PLAYERS;
    public static Setting.BoolSetting ESP_MOBS;
    public static Setting.SliderSetting FOG_R;
    public static Setting.SliderSetting FOG_G;
    public static Setting.SliderSetting FOG_B;
    public static Setting.ModeSetting TIME_MODE;
    public static Setting.SliderSetting HAND_X;
    public static Setting.SliderSetting HAND_Y;
    public static Setting.SliderSetting HAND_Z;
    public static Setting.SliderSetting HAND_SCALE;
    public static Setting.SliderSetting ZOOM_FOV;

    public static Setting.BoolSetting WM_LOGO;
    public static Setting.BoolSetting WM_PING;
    public static Setting.BoolSetting WM_FPS;
    public static Setting.BoolSetting COORDS_BIOME;
    public static Setting.SliderSetting CROSSHAIR_SIZE;

    public static Setting.ModeSetting THEME_MODE;
    public static Setting.ModeSetting FONT_MODE;
    public static Setting.SliderSetting PANEL_ALPHA;
    public static Setting.SliderSetting ANIM_SPEED;
    public static Setting.BoolSetting MENU_PARTICLES;
    public static Setting.BoolSetting MENU_LOGO;
    public static Setting.BoolSetting BANNER_PULSE;

    public static Setting.BoolSetting RPC_SHOW_SERVER;
    public static Setting.BoolSetting RPC_SHOW_NICK;

    public static Setting.ModeSetting BG_MODE;
    public static Setting.SliderSetting BG_DIM;
    public static Setting.ModeSetting MUSIC_MODE;
    public static Setting.SliderSetting MUSIC_VOL;

    private static double savedGamma = 1.0D;

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();

        // === VISUALS ===
        JUMP_CIRCLES = register(new Module("JumpCircles", "Expanding 3D rings when you jump", Category.VISUALS, true));
        CIRCLE_RADIUS = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Radius", 1.5, 0.8, 3.0, 0.1));
        CIRCLE_SPEED = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Speed", 1.0, 0.4, 2.0, 0.1));

        TRAILS = register(new Module("Trails", "Glowing 3D ribbon behind player", Category.VISUALS, true));
        TRAIL_LENGTH = TRAILS.addSetting(new Setting.SliderSetting("Duration ms", 900, 300, 2000, 100));

        BOX_ESP = register(new Module("BoxESP", "Clean 3D wireframe boxes around entities", Category.VISUALS, false));
        ESP_PLAYERS = BOX_ESP.addSetting(new Setting.BoolSetting("Players", true));
        ESP_MOBS = BOX_ESP.addSetting(new Setting.BoolSetting("Mobs", true));

        CUSTOM_FOG = register(new Module("CustomFog", "Tints world fog with custom RGB", Category.VISUALS, false));
        FOG_R = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Red", 0.35, 0.0, 1.0, 0.05));
        FOG_G = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Green", 0.85, 0.0, 1.0, 0.05));
        FOG_B = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Blue", 0.15, 0.0, 1.0, 0.05));

        TIME_CHANGER = register(new Module("TimeChanger", "Client-side sky time override", Category.VISUALS, false));
        TIME_MODE = TIME_CHANGER.addSetting(new Setting.ModeSetting("Time", "Night", "Day", "Sunset", "Night", "Midnight"));

        VIEW_MODEL = register(new Module("ViewModel", "Adjusts held item position and scale", Category.VISUALS, false));
        HAND_X = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset X", 0.0, -0.5, 0.5, 0.05));
        HAND_Y = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Y", -0.05, -0.5, 0.5, 0.05));
        HAND_Z = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Z", 0.0, -0.5, 0.5, 0.05));
        HAND_SCALE = VIEW_MODEL.addSetting(new Setting.SliderSetting("Scale", 0.85, 0.4, 1.4, 0.05));

        FULLBRIGHT = register(new Module("Fullbright", "Max gamma brightness everywhere", Category.VISUALS, false) {
            @Override
            protected void onEnable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) {
                    savedGamma = mc.options.gamma;
                    mc.options.gamma = 16.0D;
                }
            }

            @Override
            protected void onDisable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) {
                    mc.options.gamma = savedGamma;
                }
            }
        });

        NO_HURT_CAM = register(new Module("NoHurtCam", "Disables camera shake on damage", Category.VISUALS, true));

        ZOOM = register(new Module("Zoom", "Smooth FOV zoom on [C] key", Category.VISUALS, true));
        ZOOM_FOV = ZOOM.addSetting(new Setting.SliderSetting("Zoom FOV", 24.0, 10.0, 50.0, 2.0));

        // === HUD ===
        HUD = register(new Module("HUD", "Master toggle for all HUD overlays", Category.HUD, true));

        WATERMARK = register(new Module("Watermark", "Top-left brand pill", Category.HUD, true));
        WM_LOGO = WATERMARK.addSetting(new Setting.BoolSetting("Show Logo", true));
        WM_FPS = WATERMARK.addSetting(new Setting.BoolSetting("Show FPS", true));
        WM_PING = WATERMARK.addSetting(new Setting.BoolSetting("Show Ping", true));

        CUSTOM_HOTBAR = register(new Module("CustomHotbar", "Sleek animated hotbar replacement", Category.HUD, true));

        COORDS = register(new Module("Coordinates", "XYZ & Biome display", Category.HUD, true));
        COORDS_BIOME = COORDS.addSetting(new Setting.BoolSetting("Show Biome", true));

        KEYSTROKES = register(new Module("Keystrokes", "Animated WASD & Clicks overlay", Category.HUD, true));
        ARRAYLIST = register(new Module("ModuleList", "Active modules list with accent bar", Category.HUD, true));
        ARMOR_HUD = register(new Module("ArmorHUD", "Displays equipped armor durability", Category.HUD, true));

        CROSSHAIR = register(new Module("Crosshair", "Minimalist accent crosshair", Category.HUD, false));
        CROSSHAIR_SIZE = CROSSHAIR.addSetting(new Setting.SliderSetting("Size", 5.0, 3.0, 10.0, 1.0));

        // === MOVEMENT ===
        TOGGLE_SPRINT = register(new Module("ToggleSprint", "Auto-sprint when moving forward", Category.MOVEMENT, true));

        // === INTERFACE ===
        CLIENT_THEME = register(new Module("Appearance", "Full UI theme, font & animation tuning", Category.INTERFACE, true));
        THEME_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Palette", "Lime", "Lime", "Citrus", "Mint", "CyberCyan", "Amethyst", "Crimson", "CreamGold"));
        FONT_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Font", "CreamHD", "CreamHD", "Vanilla"));
        PANEL_ALPHA = CLIENT_THEME.addSetting(new Setting.SliderSetting("Panel Alpha", 0.85, 0.35, 1.0, 0.05));
        ANIM_SPEED = CLIENT_THEME.addSetting(new Setting.SliderSetting("Anim Speed", 0.18, 0.05, 0.4, 0.05));
        MENU_LOGO = CLIENT_THEME.addSetting(new Setting.BoolSetting("Menu Logo", true));
        MENU_PARTICLES = CLIENT_THEME.addSetting(new Setting.BoolSetting("Particles", true));
        BANNER_PULSE = CLIENT_THEME.addSetting(new Setting.BoolSetting("Banner Pulse", true));

        CUSTOM_MENUS = register(new Module("CustomMenus", "Replaces Main & Pause menus + dark theme", Category.INTERFACE, true));

        DISCORD_RPC = register(new Module("DiscordRPC", "Shows CreamClient activity in Discord", Category.INTERFACE, true) {
            @Override
            protected void onEnable() {
                DiscordRPC.start();
            }

            @Override
            protected void onDisable() {
                DiscordRPC.stop();
            }
        });
        RPC_SHOW_SERVER = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Server", true));
        RPC_SHOW_NICK = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Nick", true));

        // === MEDIA ===
        BACKGROUND_MOD = register(new Module("Background", "Built-in wallpaper, animated grid or custom PNG", Category.MEDIA, true));
        BG_MODE = BACKGROUND_MOD.addSetting(new Setting.ModeSetting("BgType", "DefaultImage", "DefaultImage", "Animated", "CustomImage"));
        BG_DIM = BACKGROUND_MOD.addSetting(new Setting.SliderSetting("Dimming", 0.40, 0.0, 0.9, 0.05));

        MUSIC_MOD = register(new Module("MenuMusic", "Custom .wav music player in menu", Category.MEDIA, false) {
            @Override
            protected void onEnable() {
                MediaManager.playMusic();
            }

            @Override
            protected void onDisable() {
                MediaManager.stopMusic();
            }
        });
        MUSIC_MODE = MUSIC_MOD.addSetting(new Setting.ModeSetting("Source", "CustomWAV", "CustomWAV", "Off"));
        MUSIC_VOL = MUSIC_MOD.addSetting(new Setting.SliderSetting("Volume", 0.60, 0.0, 1.0, 0.05));
    }

    private static Module register(Module m) {
        MODULES.add(m);
        return m;
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static List<Module> getByCategory(Category category) {
        List<Module> list = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.getCategory() == category) {
                list.add(m);
            }
        }
        return list;
    }
}
