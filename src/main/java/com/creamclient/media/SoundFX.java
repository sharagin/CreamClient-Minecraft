package com.creamclient.media;

import com.creamclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public final class SoundFX {
    private SoundFX() {}

    private static boolean enabled() {
        return ModuleManager.UI_SOUNDS == null || ModuleManager.UI_SOUNDS.isEnabled();
    }

    public static void play(SoundEvent evt, float pitch, float vol) {
        if (!enabled()) return;
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().play(SimpleSound.forUI(evt, pitch, vol));
            }
        } catch (Exception ignored) {}
    }

    public static void click() { play(SoundEvents.UI_BUTTON_CLICK, 1.25f, 0.35f); }
    public static void tab() { play(SoundEvents.UI_BUTTON_CLICK, 1.45f, 0.30f); }
    public static void toggle(boolean on) { play(SoundEvents.EXPERIENCE_ORB_PICKUP, on ? 1.35f : 0.78f, 0.32f); }
    public static void equip() { play(SoundEvents.ARMOR_EQUIP_DIAMOND, 1.18f, 0.38f); }
    public static void waypoint() { play(SoundEvents.NOTE_BLOCK_CHIME, 1.40f, 0.45f); }
    public static void save() { play(SoundEvents.PLAYER_LEVELUP, 1.55f, 0.25f); }
}
