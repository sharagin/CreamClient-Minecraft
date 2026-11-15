package com.creamclient.module;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static Module HUD;
    public static Module WATERMARK;
    public static Module COORDS;
    public static Module KEYSTROKES;
    public static Module ARRAYLIST;
    public static Module FULLBRIGHT;
    public static Module TOGGLE_SPRINT;

    private static double savedGamma = 1.0D;

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();

        HUD = register(new Module("HUD", "Master switch for CreamClient overlay", true));
        WATERMARK = register(new Module("Watermark", "Displays CreamClient tag and FPS", true));
        COORDS = register(new Module("Coordinates", "Shows XYZ and Nether coordinates", true));
        KEYSTROKES = register(new Module("Keystrokes", "Minimalist WASD & click indicators", true));
        ARRAYLIST = register(new Module("ModuleList", "Shows active modules on screen", true));
        TOGGLE_SPRINT = register(new Module("ToggleSprint", "Automatically sprints when moving forward", true));

        FULLBRIGHT = register(new Module("Fullbright", "Maximizes brightness in dark areas", false) {
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
    }

    private static Module register(Module m) {
        MODULES.add(m);
        return m;
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }
}
