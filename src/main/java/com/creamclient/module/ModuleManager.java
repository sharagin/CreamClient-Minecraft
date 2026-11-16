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
    public static Module CUSTOM_SKY;
    public static Module JUMP_CIRCLES;
    public static Module STEP_CIRCLES;
    public static Module TRAILS;
    public static Module BREADCRUMBS;
    public static Module CHINA_HAT;
    public static Module HALO;
    public static Module WINGS;
    public static Module ORBITAL_RINGS;
    public static Module CRYSTAL_ORBIT;
    public static Module WORLD_GRID;
    public static Module PULSE_RINGS;
    public static Module STAR_FIELD_3D;
    public static Module FOOTSTEP_DUST;
    public static Module ENTITY_HALO;
    public static Module PROJECTILE_TRAILS;
    public static Module DAMAGE_NUMBERS;
    public static Module HIT_BUBBLES;
    public static Module FREELOOK;
    public static Module BLOCK_BREATHE;
    public static Module RAIN_PUDDLE;
    public static Module PROJECTILE_2D_TRAIL;
    public static Module WEATHER_MINI_ORB;
    public static Module DEATH_COORDS;
    public static Module LOW_HP_PULSE;
    public static Module DYNAMIC_FOV;
    public static Setting.SliderSetting BASE_FOV;
    public static Module MEDIA_HOTKEYS;
    public static Module ARMOR_ALERT;
    public static Module REAL_CLOCK;
    public static Module UI_SOUNDS;
    public static Module SMOOTH_SWING;
    public static Setting.ModeSetting SWING_STYLE;
    public static Setting.SliderSetting SWING_SPEED;
    public static Setting.SliderSetting SWING_POWER;
    public static Module POTATO_OPT;
    public static Setting.SliderSetting OPT_CHUNKS;
    public static Setting.SliderSetting OPT_ENTITY_DIST;
    public static Setting.ModeSetting OPT_PARTICLES;
    public static Setting.ModeSetting OPT_GRAPHICS;
    public static Setting.ModeSetting OPT_AO;
    public static Setting.ModeSetting OPT_CLOUDS;
    public static Setting.SliderSetting OPT_MIPMAP;
    public static Setting.SliderSetting OPT_FPS;
    public static Module WAYPOINTS;
    public static Module PLAYER_SHADOW;
    public static Module PARTICLE_AURA;
    public static Module HIT_PARTICLES;
    public static Module TARGET_ESP;
    public static Module TARGET_CROWN;
    public static Module BLOCK_OVERLAY;
    public static Module BOX_ESP;
    public static Module TRACERS;
    public static Module CUSTOM_FOG;
    public static Module TIME_CHANGER;
    public static Module NO_WEATHER;
    public static Module NO_FIRE;
    public static Module VIEW_MODEL;
    public static Module FULLBRIGHT;
    public static Module NO_HURT_CAM;
    public static Module ZOOM;

    // HUD
    public static Module HUD;
    public static Module CUSTOM_CHAT;
    public static Module WATERMARK;
    public static Module NOTIFICATIONS;
    public static Module COMPASS_HUD;
    public static Module RADAR_HUD;
    public static Module NEARBY_PLAYERS;
    public static Module MUSIC_HUD;
    public static Module FPS_GRAPH;
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
    public static Module TOGGLE_SNEAK;
    public static Module AUTO_RESPAWN;
    public static Module CHAT_TIMESTAMPS;
    public static Module ANTI_AFK;

    // Interface
    public static Module CLIENT_THEME;
    public static Module CUSTOM_MENUS;
    public static Module CUSTOM_CURSOR;
    public static Module DISCORD_RPC;

    // Media
    public static Module BACKGROUND_MOD;
    public static Module MUSIC_MOD;

    // Sky Settings
    public static Setting.ModeSetting SKY_MODE;
    public static Setting.SliderSetting SKY_R;
    public static Setting.SliderSetting SKY_G;
    public static Setting.SliderSetting SKY_B;
    public static Setting.SliderSetting SKY_ALPHA;
    public static Setting.SliderSetting SKY_SPIN;
    public static Setting.BoolSetting SKY_AURORA;
    public static Setting.BoolSetting SKY_STARS;
    public static Setting.BoolSetting SKY_SYNC_FOG;

    // Visual Settings
    public static Setting.SliderSetting CIRCLE_RADIUS;
    public static Setting.SliderSetting CIRCLE_SPEED;
    public static Setting.SliderSetting CIRCLE_RINGS;
    public static Setting.SliderSetting TRAIL_LENGTH;
    public static Setting.SliderSetting BREADCRUMB_MAX;
    public static Setting.SliderSetting HAT_HEIGHT;
    public static Setting.SliderSetting HAT_RADIUS;
    public static Setting.BoolSetting HAT_SPIN;
    public static Setting.SliderSetting AURA_COUNT;
    public static Setting.SliderSetting AURA_RADIUS;
    public static Setting.SliderSetting HIT_COUNT;
    public static Setting.SliderSetting BLOCK_LINE;
    public static Setting.BoolSetting ESP_PLAYERS;
    public static Setting.BoolSetting ESP_MOBS;
    public static Setting.BoolSetting FOG_SYNC;
    public static Setting.SliderSetting FOG_R;
    public static Setting.SliderSetting FOG_G;
    public static Setting.SliderSetting FOG_B;
    public static Setting.ModeSetting TIME_MODE;

    // Deep Hand / ViewModel Settings
    public static Setting.SliderSetting HAND_X;
    public static Setting.SliderSetting HAND_Y;
    public static Setting.SliderSetting HAND_Z;
    public static Setting.SliderSetting HAND_ROT_X;
    public static Setting.SliderSetting HAND_ROT_Y;
    public static Setting.SliderSetting HAND_ROT_Z;
    public static Setting.SliderSetting HAND_SCALE;
    public static Setting.BoolSetting HAND_SPIN;
    public static Setting.SliderSetting ZOOM_FOV;

    // HUD Settings
    public static Setting.BoolSetting WM_LOGO;
    public static Setting.BoolSetting WM_PING;
    public static Setting.BoolSetting WM_FPS;
    public static Setting.BoolSetting COORDS_BIOME;
    public static Setting.BoolSetting KEY_CPS;
    public static Setting.SliderSetting RADAR_RANGE;
    public static Setting.SliderSetting VIGNETTE_ALPHA;
    public static Setting.SliderSetting CROSSHAIR_SIZE;

    // Interface Settings
    public static Setting.ModeSetting THEME_MODE;
    public static Setting.ModeSetting COLOR_MODE;
    public static Setting.SliderSetting CUSTOM_R;
    public static Setting.SliderSetting CUSTOM_G;
    public static Setting.SliderSetting CUSTOM_B;
    public static Setting.SliderSetting CORNER_RADIUS;
    public static Setting.ModeSetting FONT_MODE;
    public static Setting.SliderSetting FONT_SCALE;
    public static Setting.ModeSetting LANG_MODE;
    public static Setting.SliderSetting PANEL_ALPHA;
    public static Setting.SliderSetting ANIM_SPEED;
    public static Setting.SliderSetting BORDER_ALPHA;
    public static Setting.SliderSetting HUD_PADDING;
    public static Setting.SliderSetting GLOW_INTENSITY;
    public static Setting.SliderSetting NEARBY_RADIUS;
    public static Setting.BoolSetting NEARBY_ALERT;
    public static Setting.BoolSetting NEARBY_SOUND;
    public static Setting.BoolSetting NEARBY_HEALTH;
    public static Setting.BoolSetting WP_BEAM;
    public static Setting.BoolSetting WP_LABELS;
    public static Setting.SliderSetting WP_ALPHA;
    public static Setting.SliderSetting GRID_RADIUS;
    public static Setting.SliderSetting GRID_ALPHA;
    public static Setting.BoolSetting MUSIC_EQ;
    public static Setting.BoolSetting MUSIC_PROGRESS;
    public static Setting.BoolSetting MENU_PARTICLES;
    public static Setting.BoolSetting MENU_LINES;
    public static Setting.BoolSetting MENU_LOGO;
    public static Setting.BoolSetting SPLASH_ANIM;
    public static Setting.BoolSetting INV_STYLING;

    public static Setting.BoolSetting RPC_SHOW_SERVER;
    public static Setting.BoolSetting RPC_SHOW_NICK;
    public static Setting.BoolSetting RPC_BUTTONS;

    public static Setting.ModeSetting BG_MODE;
    public static Setting.SliderSetting BG_DIM;
    public static Setting.ModeSetting MUSIC_MODE;
    public static Setting.SliderSetting MUSIC_VOL;

    private static double savedGamma = 1.0D;

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();

        // === VISUALS ===
        CUSTOM_SKY = register(new Module("CustomSky", "20 customizable animated skies", Category.VISUALS, true));
        SKY_MODE = CUSTOM_SKY.addSetting(new Setting.ModeSetting("SkyType", "LimeAurora",
                "LimeAurora", "CyberGrid", "MidnightStars", "CitrusSunset", "BloodMoon",
                "VoidDark", "NeonPink", "OceanicDeep", "EmeraldNebula", "GoldHorizon",
                "RainbowPrism", "ArcticFrost", "ToxicStorm", "SakuraSky", "AmethystSpace",
                "SolarFlare", "MatrixRain", "PastelCream", "Eclipse", "CustomRGB"));
        SKY_R = CUSTOM_SKY.addSetting(new Setting.SliderSetting("Sky R", 60, 0, 255, 5));
        SKY_G = CUSTOM_SKY.addSetting(new Setting.SliderSetting("Sky G", 180, 0, 255, 5));
        SKY_B = CUSTOM_SKY.addSetting(new Setting.SliderSetting("Sky B", 30, 0, 255, 5));
        SKY_ALPHA = CUSTOM_SKY.addSetting(new Setting.SliderSetting("Sky Alpha", 0.65, 0.1, 1.0, 0.05));
        SKY_SPIN = CUSTOM_SKY.addSetting(new Setting.SliderSetting("Sky Spin", 1.0, 0.0, 4.0, 0.2));
        SKY_AURORA = CUSTOM_SKY.addSetting(new Setting.BoolSetting("Aurora Waves", true));
        SKY_STARS = CUSTOM_SKY.addSetting(new Setting.BoolSetting("Stars", true));
        SKY_SYNC_FOG = CUSTOM_SKY.addSetting(new Setting.BoolSetting("Sync Fog", true));

        JUMP_CIRCLES = register(new Module("JumpCircles", "Concentric 3D rings on jump", Category.VISUALS, true));
        CIRCLE_RADIUS = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Radius", 1.5, 0.8, 3.0, 0.1));
        CIRCLE_SPEED = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Speed", 1.0, 0.4, 2.0, 0.1));
        CIRCLE_RINGS = JUMP_CIRCLES.addSetting(new Setting.SliderSetting("Rings", 2.0, 1.0, 3.0, 1.0));

        STEP_CIRCLES = register(new Module("StepCircles", "Small glowing rings when walking", Category.VISUALS, false));

        TRAILS = register(new Module("Trails", "Glowing 3D ribbon behind player", Category.VISUALS, true));
        TRAIL_LENGTH = TRAILS.addSetting(new Setting.SliderSetting("Duration ms", 1000, 300, 2500, 100));

        BREADCRUMBS = register(new Module("Breadcrumbs", "Long glowing 3D path line", Category.VISUALS, false));
        BREADCRUMB_MAX = BREADCRUMBS.addSetting(new Setting.SliderSetting("Max Points", 160, 40, 400, 20));

        CHINA_HAT = register(new Module("ChinaHat", "3D glowing cone above head in F5", Category.VISUALS, true));
        HAT_HEIGHT = CHINA_HAT.addSetting(new Setting.SliderSetting("Height", 0.35, 0.2, 0.6, 0.05));
        HAT_RADIUS = CHINA_HAT.addSetting(new Setting.SliderSetting("Radius", 0.58, 0.4, 0.9, 0.02));
        HAT_SPIN = CHINA_HAT.addSetting(new Setting.BoolSetting("Spin Glow", true));

        HALO = register(new Module("Halo", "Rotating 3D angel halo in F5", Category.VISUALS, false));
        WINGS = register(new Module("Wings", "Animated 3D geometric wings in F5", Category.VISUALS, true));
        ORBITAL_RINGS = register(new Module("OrbitalRings", "3D gyroscopic orbits in F5", Category.VISUALS, false));
        CRYSTAL_ORBIT = register(new Module("CrystalOrbit", "Orbiting 3D crystals in F5", Category.VISUALS, true));
        PLAYER_SHADOW = register(new Module("PlayerAura", "Glowing 3D circle under feet", Category.VISUALS, true));

        PARTICLE_AURA = register(new Module("ParticleAura", "Floating 3D fireflies around you", Category.VISUALS, true));
        AURA_COUNT = PARTICLE_AURA.addSetting(new Setting.SliderSetting("Count", 18, 6, 40, 2));
        AURA_RADIUS = PARTICLE_AURA.addSetting(new Setting.SliderSetting("Radius", 1.4, 0.6, 2.8, 0.2));

        HIT_PARTICLES = register(new Module("HitParticles", "Glowing 3D sparks on attack", Category.VISUALS, true));
        HIT_COUNT = HIT_PARTICLES.addSetting(new Setting.SliderSetting("Sparks", 16, 6, 36, 2));

        TARGET_ESP = register(new Module("TargetRing", "Animated 3D ring on looked target", Category.VISUALS, true));
        TARGET_CROWN = register(new Module("TargetCrown", "3D crown above looked target", Category.VISUALS, false));

        BLOCK_OVERLAY = register(new Module("BlockOverlay", "Glowing 3D block highlight", Category.VISUALS, true));
        BLOCK_LINE = BLOCK_OVERLAY.addSetting(new Setting.SliderSetting("Line Width", 2.2, 1.0, 5.0, 0.2));

        BOX_ESP = register(new Module("BoxESP", "Clean 3D wireframe boxes around entities", Category.VISUALS, false));
        ESP_PLAYERS = BOX_ESP.addSetting(new Setting.BoolSetting("Players", true));
        ESP_MOBS = BOX_ESP.addSetting(new Setting.BoolSetting("Mobs", true));

        TRACERS = register(new Module("Tracers", "3D lines to nearby players", Category.VISUALS, false));

        CUSTOM_FOG = register(new Module("CustomFog", "Tints world fog with RGB or Theme", Category.VISUALS, false));
        FOG_SYNC = CUSTOM_FOG.addSetting(new Setting.BoolSetting("Sync Theme", true));
        FOG_R = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Red", 0.35, 0.0, 1.0, 0.05));
        FOG_G = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Green", 0.85, 0.0, 1.0, 0.05));
        FOG_B = CUSTOM_FOG.addSetting(new Setting.SliderSetting("Blue", 0.15, 0.0, 1.0, 0.05));

        TIME_CHANGER = register(new Module("TimeChanger", "Client-side sky time override", Category.VISUALS, false));
        TIME_MODE = TIME_CHANGER.addSetting(new Setting.ModeSetting("Time", "Night", "Day", "Sunset", "Night", "Midnight"));

        NO_WEATHER = register(new Module("NoWeather", "Disables rain and snow visually", Category.VISUALS, true));
        NO_FIRE = register(new Module("NoFireOverlay", "Hides screen fire overlay", Category.VISUALS, true));

        VIEW_MODEL = register(new Module("HandVisuals", "Hand position, 3D rotation, scale & spin", Category.VISUALS, true));
        HAND_X = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset X", 0.05, -0.5, 0.5, 0.05));
        HAND_Y = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Y", -0.05, -0.5, 0.5, 0.05));
        HAND_Z = VIEW_MODEL.addSetting(new Setting.SliderSetting("Offset Z", -0.10, -0.5, 0.5, 0.05));
        HAND_ROT_X = VIEW_MODEL.addSetting(new Setting.SliderSetting("Rot X", 0, -180, 180, 10));
        HAND_ROT_Y = VIEW_MODEL.addSetting(new Setting.SliderSetting("Rot Y", 0, -180, 180, 10));
        HAND_ROT_Z = VIEW_MODEL.addSetting(new Setting.SliderSetting("Rot Z", 0, -180, 180, 10));
        HAND_SCALE = VIEW_MODEL.addSetting(new Setting.SliderSetting("Scale", 0.85, 0.4, 1.4, 0.05));
        HAND_SPIN = VIEW_MODEL.addSetting(new Setting.BoolSetting("Spin Item", false));

        FULLBRIGHT = register(new Module("Fullbright", "Max gamma brightness everywhere", Category.VISUALS, false) {
            @Override protected void onEnable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) { savedGamma = mc.options.gamma; mc.options.gamma = 16.0D; }
            }
            @Override protected void onDisable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) mc.options.gamma = savedGamma;
            }
        });

        NO_HURT_CAM = register(new Module("NoHurtCam", "Disables camera shake on damage", Category.VISUALS, true));

        ZOOM = register(new Module("Zoom", "Smooth FOV zoom on [C] key", Category.VISUALS, true));
        ZOOM_FOV = ZOOM.addSetting(new Setting.SliderSetting("Zoom FOV", 24.0, 10.0, 50.0, 2.0));

        // === HUD ===
        HUD = register(new Module("HUD", "Master toggle for all HUD overlays", Category.HUD, true));
        CUSTOM_CHAT = register(new Module("CustomChat", "Animated draggable rounded chat", Category.HUD, true));

        WATERMARK = register(new Module("Watermark", "Top-left brand pill", Category.HUD, true));
        WM_LOGO = WATERMARK.addSetting(new Setting.BoolSetting("Show Logo", true));
        WM_FPS = WATERMARK.addSetting(new Setting.BoolSetting("Show FPS", true));
        WM_PING = WATERMARK.addSetting(new Setting.BoolSetting("Show Ping", true));

        NOTIFICATIONS = register(new Module("Notifications", "Smooth toggle popups", Category.HUD, true));
        COMPASS_HUD = register(new Module("CompassHUD", "Top directional compass bar", Category.HUD, true));

        RADAR_HUD = register(new Module("RadarHUD", "Mini-radar for nearby entities", Category.HUD, false));
        RADAR_RANGE = RADAR_HUD.addSetting(new Setting.SliderSetting("Range", 40.0, 16.0, 80.0, 4.0));

        FPS_GRAPH = register(new Module("FPSGraph", "Live FPS chart monitor", Category.HUD, false));
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

        WORLD_GRID = register(new Module("WorldGrid", "Neon cyber grid under player", Category.VISUALS, false));
        GRID_RADIUS = WORLD_GRID.addSetting(new Setting.SliderSetting("Grid Radius", 6.0, 3.0, 14.0, 1.0));
        GRID_ALPHA = WORLD_GRID.addSetting(new Setting.SliderSetting("Grid Alpha", 0.35, 0.1, 0.9, 0.05));

        PULSE_RINGS = register(new Module("PulseRings", "3D expanding shockwave rings around player", Category.VISUALS, true));
        STAR_FIELD_3D = register(new Module("StarField3D", "Floating 3D ambient stars around player", Category.VISUALS, true));
        FOOTSTEP_DUST = register(new Module("FootstepGlow", "Glowing 3D accent particles when moving", Category.VISUALS, true));
        ENTITY_HALO = register(new Module("EntityGlowRing", "Glowing 3D ring under nearby entities", Category.VISUALS, false));
        PROJECTILE_TRAILS = register(new Module("ProjectileTrails", "3D glowing trails behind arrows & pearls", Category.VISUALS, true));
        DAMAGE_NUMBERS = register(new Module("DamagePopups", "Floating 3D damage indicator on hit", Category.VISUALS, true));
        HIT_BUBBLES = register(new Module("HitShockwave", "Expanding 3D shockwave ring on attack", Category.VISUALS, true));
        FREELOOK = register(new Module("FreeLook", "360 camera look while holding Left Alt", Category.VISUALS, true));
        BLOCK_BREATHE = register(new Module("BlockBreathe", "Softly pulses placed block outline for 2s", Category.VISUALS, true));
        RAIN_PUDDLE = register(new Module("RainPuddle", "Reflective sky puddle under feet after 5s in rain", Category.VISUALS, true));
        PROJECTILE_2D_TRAIL = register(new Module("Projectile2DTrail", "Short 200ms 2D brush trail on thrown items & arrows", Category.VISUALS, true));
        WEATHER_MINI_ORB = register(new Module("WeatherMiniOrb", "Tiny 4-pixel sky & weather orb in corner", Category.HUD, true));
        DEATH_COORDS = register(new Module("DeathCoords", "Auto-saves a red waypoint where you died", Category.MOVEMENT, true));
        LOW_HP_PULSE = register(new Module("LowHealthPulse", "Soft pulsing red edge glow at low HP", Category.HUD, true));
        DYNAMIC_FOV = register(new Module("SmoothFOV", "Custom smooth FOV changer", Category.VISUALS, false));
        BASE_FOV = DYNAMIC_FOV.addSetting(new Setting.SliderSetting("FOV", 90.0, 50.0, 120.0, 1.0));
        MEDIA_HOTKEYS = register(new Module("MediaHotkeys", "F7 Prev / F8 Play-Pause / F9 Next track", Category.MEDIA, true));
        ARMOR_ALERT = register(new Module("ArmorAlert", "Warns when equipped armor durability is below 15%", Category.HUD, true));
        REAL_CLOCK = register(new Module("RealClock", "Displays real-world time in HUD", Category.HUD, true));
        UI_SOUNDS = register(new Module("UISounds", "Crisp audio feedback on clicks, toggles & equips", Category.INTERFACE, true));
        SMOOTH_SWING = register(new Module("SmoothSwing", "Fluid hand & sword swing animation on hit", Category.VISUALS, true));
        SWING_STYLE = SMOOTH_SWING.addSetting(new Setting.ModeSetting("SwingStyle", "Smooth", "Smooth", "1.7 Old", "Spin", "Push", "Wave"));
        SWING_SPEED = SMOOTH_SWING.addSetting(new Setting.SliderSetting("SwingSpeed", 0.75, 0.20, 2.50, 0.05));
        SWING_POWER = SMOOTH_SWING.addSetting(new Setting.SliderSetting("SwingPower", 1.00, 0.25, 2.00, 0.05));
        POTATO_OPT = register(new Module("PotatoOptimizer", "Weak PC FPS boost & configurable potato graphics", Category.INTERFACE, false));
        OPT_CHUNKS = POTATO_OPT.addSetting(new Setting.SliderSetting("Chunks", 6.0, 2.0, 16.0, 1.0));
        OPT_ENTITY_DIST = POTATO_OPT.addSetting(new Setting.SliderSetting("EntityDist%", 75.0, 50.0, 100.0, 5.0));
        OPT_PARTICLES = POTATO_OPT.addSetting(new Setting.ModeSetting("ParticlesOpt", "Decreased", "Minimal", "Decreased", "All"));
        OPT_GRAPHICS = POTATO_OPT.addSetting(new Setting.ModeSetting("GraphicsOpt", "Fast", "Fast", "Fancy"));
        OPT_AO = POTATO_OPT.addSetting(new Setting.ModeSetting("SmoothLight", "Off", "Off", "Minimum", "Maximum"));
        OPT_CLOUDS = POTATO_OPT.addSetting(new Setting.ModeSetting("CloudsOpt", "Off", "Off", "Fast", "Fancy"));
        OPT_MIPMAP = POTATO_OPT.addSetting(new Setting.SliderSetting("Mipmaps", 0.0, 0.0, 4.0, 1.0));
        OPT_FPS = POTATO_OPT.addSetting(new Setting.SliderSetting("MaxFPS", 120.0, 30.0, 260.0, 10.0));

        WAYPOINTS = register(new Module("Waypoints", "3D beacons and distance markers (Key: M)", Category.VISUALS, true));
        WP_BEAM = WAYPOINTS.addSetting(new Setting.BoolSetting("3D Beam", true));
        WP_LABELS = WAYPOINTS.addSetting(new Setting.BoolSetting("3D Labels", true));
        WP_ALPHA = WAYPOINTS.addSetting(new Setting.SliderSetting("Beam Alpha", 0.55, 0.15, 1.0, 0.05));

        NEARBY_PLAYERS = register(new Module("NearbyPlayers", "Radar & alerts when players are nearby", Category.HUD, true));
        NEARBY_RADIUS = NEARBY_PLAYERS.addSetting(new Setting.SliderSetting("Radius (m)", 150.0, 20.0, 500.0, 10.0));
        NEARBY_ALERT = NEARBY_PLAYERS.addSetting(new Setting.BoolSetting("Alert Toast", true));
        NEARBY_SOUND = NEARBY_PLAYERS.addSetting(new Setting.BoolSetting("Sound Alert", true));
        NEARBY_HEALTH = NEARBY_PLAYERS.addSetting(new Setting.BoolSetting("Show Health", true));

        MUSIC_HUD = register(new Module("MusicPlayerHUD", "Visible on-screen music player & equalizer", Category.HUD, true));
        MUSIC_EQ = MUSIC_HUD.addSetting(new Setting.BoolSetting("Equalizer", true));
        MUSIC_PROGRESS = MUSIC_HUD.addSetting(new Setting.BoolSetting("Progress Bar", true));

        // === MOVEMENT / PLAYER ===
        TOGGLE_SPRINT = register(new Module("ToggleSprint", "Auto-sprint when moving forward", Category.MOVEMENT, true));
        TOGGLE_SNEAK = register(new Module("ToggleSneak", "Keeps sneaking without holding Shift", Category.MOVEMENT, false));
        AUTO_RESPAWN = register(new Module("AutoRespawn", "Automatically respawns on death", Category.MOVEMENT, false));
        CHAT_TIMESTAMPS = register(new Module("ChatTime", "Adds [HH:MM] prefix to chat", Category.MOVEMENT, true));
        ANTI_AFK = register(new Module("AntiAFK", "Prevents idle kick on servers", Category.MOVEMENT, false));

        // === INTERFACE ===
        CLIENT_THEME = register(new Module("Appearance", "Rounded UI, RGB, Font, Splash & Anim", Category.INTERFACE, true));
        THEME_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Palette", "Lime", "Lime", "Citrus", "Mint", "CyberCyan", "Amethyst", "Crimson", "CreamGold"));
        COLOR_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("ColorMode", "Breathing", "Breathing", "Rainbow", "Static", "CustomRGB"));
        CUSTOM_R = CLIENT_THEME.addSetting(new Setting.SliderSetting("Custom R", 163, 0, 255, 5));
        CUSTOM_G = CLIENT_THEME.addSetting(new Setting.SliderSetting("Custom G", 255, 0, 255, 5));
        CUSTOM_B = CLIENT_THEME.addSetting(new Setting.SliderSetting("Custom B", 18, 0, 255, 5));
        CORNER_RADIUS = CLIENT_THEME.addSetting(new Setting.SliderSetting("Corner Radius", 5.0, 0.0, 10.0, 1.0));
        FONT_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Font", "Vanilla", "Vanilla", "CreamHD"));
        FONT_SCALE = CLIENT_THEME.addSetting(new Setting.SliderSetting("FontScale", 0.65, 0.50, 0.85, 0.05));
        LANG_MODE = CLIENT_THEME.addSetting(new Setting.ModeSetting("Language", "RU", "RU", "EN"));
        PANEL_ALPHA = CLIENT_THEME.addSetting(new Setting.SliderSetting("Panel Alpha", 0.85, 0.25, 1.0, 0.05));
        ANIM_SPEED = CLIENT_THEME.addSetting(new Setting.SliderSetting("Anim Speed", 0.18, 0.05, 0.45, 0.05));
        BORDER_ALPHA = CLIENT_THEME.addSetting(new Setting.SliderSetting("Border Alpha", 0.70, 0.0, 1.0, 0.05));
        HUD_PADDING = CLIENT_THEME.addSetting(new Setting.SliderSetting("HUD Padding", 6.0, 2.0, 14.0, 1.0));
        GLOW_INTENSITY = CLIENT_THEME.addSetting(new Setting.SliderSetting("Glow Alpha", 0.35, 0.0, 1.0, 0.05));
        MENU_LOGO = CLIENT_THEME.addSetting(new Setting.BoolSetting("Menu Logo", true));
        MENU_PARTICLES = CLIENT_THEME.addSetting(new Setting.BoolSetting("Particles", true));
        MENU_LINES = CLIENT_THEME.addSetting(new Setting.BoolSetting("Constellations", true));
        SPLASH_ANIM = CLIENT_THEME.addSetting(new Setting.BoolSetting("Splash Screen", true));

        CUSTOM_MENUS = register(new Module("CustomMenus", "Custom Main, SP, MP, Options & Inv", Category.INTERFACE, true));
        INV_STYLING = CUSTOM_MENUS.addSetting(new Setting.BoolSetting("Style Inventory", true));

        CUSTOM_CURSOR = register(new Module("BlackCursor", "Custom black gaming cursor in GUI", Category.INTERFACE, true));

        DISCORD_RPC = register(new Module("DiscordRPC", "Shows CreamClient activity in Discord", Category.INTERFACE, true) {
            @Override protected void onEnable() { DiscordRPC.start(); }
            @Override protected void onDisable() { DiscordRPC.stop(); }
        });
        RPC_SHOW_SERVER = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Server", true));
        RPC_SHOW_NICK = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Nick", true));
        RPC_BUTTONS = DISCORD_RPC.addSetting(new Setting.BoolSetting("Show Buttons", true));

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
