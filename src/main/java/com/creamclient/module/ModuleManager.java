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
    public static Module CHINA_HAT;
    public static Module PARTICLE_AURA;
    public static Module HIT_PARTICLES;
    public static Module TARGET_ESP;
    public static Module BLOCK_OVERLAY;
    public static Module BOX_ESP;
    public static Module CUSTOM_FOG;
    public static Module TIME_CHANGER;
    public static Module NO_WEATHER;
    public static Module VIEW_MODEL;
    public static Module FULLBRIGHT;
    public static Module NO_HURT_CAM;
    public static Module ZOOM;

    // HUD
    public static Module HUD;
    public static Module WATERMARK;
    public static Module TARGET_HUD;
    public static Module SESSION_INFO;
    public static Module VIGNETTE;
    public static Module CUSTOM_HOTBAR;
    public static Module COORDS;
    public static Module KEYSTROKES;
    public static Module ARRAYLIST;
    public static Module ARMOR_HUD;
    public static Module POTION_HUD;
    public static Module SPEEDOMETER;
    public static Module CROSSHAIR;

    // Movement / Player
    public static Module TOGGLE_SPRINT;
    public static Module AUTO_RESPAWN;

    // Interface
    public static Module CLIENT_THEME;
    public static Module CUSTOM_MENUS;
    public static Module CUSTOM_CURSOR;
    public static Module DISCORD_RPC;

    // Media
    public static Module BACKGROUND_MOD;
    public static Module MUSIC_MOD;

    // Settings
    public static Setting.SliderSetting CIRCLE_RADIUS;
    public static Setting.SliderSetting CIRCLE_SPEED;
    public static Setting.SliderSetting TRAIL_LENGTH;
    public static Setting.SliderSetting AURA_COUNT;
    public static Setting.SliderSetting AURA_RADIUS;
    public static Setting.SliderSetting HIT_COUNT;
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
    public static Setting.BoolSetting KEY_CPS;
    public static Setting.SliderSetting VIGNETTE_ALPHA;
    public static Setting.SliderSetting CROSSHAIR_SIZE;

    public static Setting.ModeSetting THEME_MODE;
    public static Setting.ModeSetting COLOR_MODE;
    public static Setting.ModeSetting FONT_MODE;
    public static Setting.ModeSetting LANG_MODE;
    public static Setting.SliderSetting PANEL_ALPHA;
    public static Setting.SliderSetting ANIM_SPEED;
    public static Setting.BoolSetting MENU_PARTICLES;
    public static Setting.BoolSetting MENU_LINES;
    public static Setting.BoolSetting MENU_LOGO;
    public static Setting.BoolSetting BANNER_PULSE;
    public static Setting.BoolSetting INV_STYLING;

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
        JUMP_CIRCLES = register(new Module("JumpCircles", "Double 3D rings on jump", Category.VISUALS, true));
        CIRCLE_RADIUS = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Radius", 1.5, 0.8, 3.0, 0.1));
        CIRCLE_SPEED = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Speed", 1.0, 0.4, 2.0, 0.1));

        TRAILS = register(new Module("Trails", "Glowing 3D ribbon behind player", Category.VISUALS, true));
        TRAIL_LENGTH = TRAILS.addSetting(new Setting.SliderSetting("Duration ms", 1000, 300, 2200, 100));

        CHINA_HAT = register(new Module("ChinaHat", "3D glowing cone above head in F5", Category.VISUALS, true));

        PARTICLE_AURA = register(new Module("ParticleAura", "Floating 3D fireflies around you", Category.VISUALS, true));
        AURA_COUNT = PARTICLE_AURA.addSetting(new Setting.SliderSetting("Count", 18, 6, 40, 2));
        AURA_RADIUS = PARTICLE_AURA.addSetting(new Setting.SliderSetting("Radius", 1.4, 0.6, 2.8, 0.2));

        HIT_PARTICLES = register(new Module("HitParticles", "Glowing 3D sparks on attack", Category.VISUALS, true));
        HIT_COUNT = HIT_PARTICLES.addSetting(new Setting.SliderSetting("Sparks", 16, 6, 36, 2));

        TARGET_ESP = register(new Module("TargetRing", "Animated 3D ring on looked target", Category.VISUALS, true));
        BLOCK_OVERLAY = register(new Module("BlockOverlay", "Glowing 3D block highlight", Category.VISUALS, true));

        BOX_ESP = register(new Module("BoxESP", "Clean 3D wireframe boxes around entities", Category.VISUALS, false));
        ESP_PLAYERS = BOX_ESP.addSetting(new Setting.BoolSetting("Players", true));
        ESP_MOBS = BOX_ESP.addSetting(new Setting.BoolSetting("Mobs", true));

        CUSTOM_FOG = register(new Module("CustomFog", "Tints world fog with custom RGB", Category.VISUALS, false));
        FOG_R = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Red", 0.35, 0.0, 1.0, 0.05));
        FOG_G = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Green", 0.85, 0.0, 1.0, 0.05));
        FOG_B = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Blue", 0.15, 0.0, 1.0, 0.05));

        TIME_CHANGER = register(new Module("TimeChanger", "Client-side sky time override", Category.VISUALS, false));
        TIME_MODE = TIME_CHANGER.addSetting(new Setting.ModeSetting("Time", "Night", "Day", "Sunset", "Night", "Midnight"));

        NO_WEATHER = register(new Module("NoWeather", "Disables rain and snow visually", Category.VISUALS, true));

        VIEW_MODEL = register(new Module("ViewModel", "Adjusts held item position and scale", Category.VISUALS, false));
        HAND_X = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset X", 0.0, -0.5, 0.5, 0.05));
        HAND_Y = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Y", -0.05, -0.5, 0.5, 0.05));
        HAND_Z = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Z", 0.0, -0.5, 0.5, 0.05));
        HAND_SCALE = VIEW_MODEL.addSetting(new Setting.SliderSetting("Scale", 0.85, 0.4, 1.4, 0.05));

        FULLBRIGHT = register(new Module("Fullbright", "Max gamma brightness everywhere", Category.VISUALS, false) {
            @Override
            protected void onEnable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) { savedGamma = mc.options.gamma; mc.options.gamma = 16.0D; }
            }
            @Override
            protected void onDisable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) { mc.options.gamma = savedGamma; }
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

        TARGET_HUD = register(new Module("TargetHUD", "Shows HP & distance of target", Category.HUD, true));
        SESSION_INFO = register(new Module("SessionInfo", "Shows play time & stats", Category.HUD, true));

        VIGNETTE = register(new Module("ScreenGlow", "Soft accent glow around edges", Category.HUD, true));
        VIGNETTE_ALPHA = VIGNETTE.addSetting(new Setting.SliderSetting("Intensity", 0.25, 0.05, 0.6, 0.05));

        CUSTOM_HOTBAR = register(new Module("CustomHotbar", "Sleek animated hotbar replacement", Category.HUD, true));

        COORDS = register(new Module("Coordinates", "XYZ & Biome display", Category.HUD, true));
        COORDS_BIOME = COORDS.addSetting(new Setting.BoolSetting("Show Biome", true));

        KEYSTROKES = register(new Module("Keystrokes", "Animated WASD & CPS overlay", Category.HUD, true));
        KEY_CPS = KEYSTROKES.addSetting(new Setting.BoolSetting("Show CPS", true));

        ARRAYLIST = register(new Module("ModuleList", "Active modules list with accent bar", Category.HUD, true));
        ARMOR_HUD = register(new Module("ArmorHUD", "Displays equipped armor durability", Category.HUD, true));
        POTION_HUD = register(new Module("PotionHUD", "Displays active potion timers", Category.HUD, true));
        SPEEDOMETER = register(new Module("Speedometer", "Shows movement speed in b/s", Category.HUD, false));

        CROSSHAIR = register(new Module("Crosshair", "Minimalist accent crosshair", Category.HUD, false));
        CROSSHAIR_SIZE = CROSSHAIR.addSetting(new Setting.SliderSetting("Size", 5.0, 3.0, 10.0, 1.0));

        // === MOVEMENT ===
        TOGGLE_SPRINT = register(new Module("ToggleSprint", "Auto-sprint when moving forward", Category.MOVEMENT, true));
        AUTO_RESPAWN = register(new Module("AutoRespawn", "Automatically respawns on death", Category.MOVEMENT, false));

        // === INTERFACE ===
        CLIENT_THEME = register(new Module("Appearance", "Theme, Rainbow, Lang, Font & Anim", Category.INTERFACE, true));
        THEME_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Palette", "Lime", "Lime", "Citrus", "Mint", "CyberCyan", "Amethyst", "Crimson", "CreamGold"));
        COLOR_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("ColorMode", "Breathing", "Breathing", "Rainbow", "Static"));
        FONT_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Font", "CreamHD", "CreamHD", "Vanilla"));
        LANG_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Language", "RU", "RU", "EN", "UK"));
        PANEL_ALPHA = CLIENT_THEME.addSetting(new Setting.SliderSetting("Panel Alpha", 0.85, 0.35, 1.0, 0.05));
        ANIM_SPEED = CLIENT_THEME.addSetting(new Setting.SliderSetting("Anim Speed", 0.16, 0.05, 0.4, 0.05));
        MENU_LOGO = CLIENT_THEME.addSetting(new Setting.BoolSetting("Menu Logo", true));
        MENU_PARTICLES = CLIENT_THEME.addSetting(new Setting.BoolSetting("Particles", true));
        MENU_LINES = CLIENT_THEME.addSetting(new Setting.BoolSetting("Constellations", true));
        BANNER_PULSE = CLIENT_THEME.addSetting(new Setting.BoolSetting("Banner Pulse", true));

        CUSTOM_MENUS = register(new Module("CustomMenus", "Custom Main, MP, Options & Inv", Category.INTERFACE, true));
        INV_STYLING = CUSTOM_MENUS.addSetting(new Setting.BoolSetting("Style Inventory", true));

        CUSTOM_CURSOR = register(new Module("BlackCursor", "Custom black gaming cursor in GUI", Category.INTERFACE, true));

        DISCORD_RPC = register(new Module("DiscordRPC", "Shows CreamClient activity in Discord", Category.INTERFACE, true) {
            @Override protected void onEnable() { DiscordRPC.start(); }
            @Override protected void onDisable() { DiscordRPC.stop(); }
        });
        RPC_SHOW_SERVER = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Server", true));
        RPC_SHOW_NICK = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Nick", true));

        // === MEDIA ===
        BACKGROUND_MOD = register(new Module("Background", "Wallpaper, animated grid or custom PNG", Category.MEDIA, true));
        BG_MODE = BACKGROUND_MOD.addSetting(new Setting.ModeSetting("BgType", "DefaultImage", "DefaultImage", "Animated", "CustomImage"));
        BG_DIM = BACKGROUND_MOD.addSetting(new Setting.SliderSetting("Dimming", 0.40, 0.0, 0.9, 0.05));

        MUSIC_MOD = register(new Module("MenuMusic", "Custom .wav music player in menu", Category.MEDIA, false) {
            @Override protected void onEnable() { MediaManager.playMusic(); }
            @Override protected void onDisable() { MediaManager.stopMusic(); }
        });
        MUSIC_MODE = MUSIC_MOD.addSetting(new Setting.ModeSetting("Source", "CustomWAV", "CustomWAV", "Off"));
        MUSIC_VOL = MUSIC_MOD.addSetting(new Setting.SliderSetting("Volume", 0.60, 0.0, 1.0, 0.05));
    }

    private static Module register(Module m) { MODULES.add(m); return m; }
    public static List<Module> getModules() { return Collections.unmodifiableList(MODULES); }
    public static List<Module> getByCategory(Category category) {
        List<Module> list = new ArrayList<>();
        for (Module m : MODULES) if (m.getCategory() == category) list.add(m);
        return list;
    }
}
