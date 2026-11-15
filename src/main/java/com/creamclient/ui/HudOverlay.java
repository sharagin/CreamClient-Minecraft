package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudOverlay {
    private HudOverlay() {}

    public static void render(MatrixStack ms, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.renderDebug || !ModuleManager.HUD.isEnabled()) {
            return;
        }

        // 1. Watermark
        if (ModuleManager.WATERMARK.isEnabled()) {
            int fps = Minecraft.getInstance().fps;
            String text = CreamClientMod.CLIENT_NAME + "  |  1.16.5  |  " + fps + " FPS";
            int w = CreamTheme.widthThin(text) + 14;
            CreamTheme.drawBorderedRect(ms, 8, 8, 8 + w, 24, CreamTheme.BG_PANEL, CreamTheme.BORDER);
            AbstractGui.fill(ms, 8, 8, 10, 24, CreamTheme.LIME_PRIMARY);
            CreamTheme.drawThin(ms, text, 15, 11.5f, CreamTheme.TEXT_PRIMARY);
        }

        // 2. Coordinates
        if (ModuleManager.COORDS.isEnabled()) {
            int x = (int) Math.floor(mc.player.getX());
            int y = (int) Math.floor(mc.player.getY());
            int z = (int) Math.floor(mc.player.getZ());
            String coords = String.format("XYZ  %d, %d, %d", x, y, z);
            int w = CreamTheme.widthThin(coords) + 12;
            int cy = screenHeight - 24;
            CreamTheme.drawBorderedRect(ms, 8, cy, 8 + w, cy + 16, CreamTheme.BG_PANEL, CreamTheme.BORDER);
            CreamTheme.drawThin(ms, coords, 14, cy + 3.5f, CreamTheme.LIME_SOFT);
        }

        // 3. Keystrokes (bottom-right)
        if (ModuleManager.KEYSTROKES.isEnabled()) {
            int baseX = screenWidth - 76;
            int baseY = screenHeight - 68;
            drawKey(ms, "W", baseX + 22, baseY, 20, 18, mc.options.keyUp);
            drawKey(ms, "A", baseX, baseY + 20, 20, 18, mc.options.keyLeft);
            drawKey(ms, "S", baseX + 22, baseY + 20, 20, 18, mc.options.keyDown);
            drawKey(ms, "D", baseX + 44, baseY + 20, 20, 18, mc.options.keyRight);
            drawKey(ms, "SPACE", baseX, baseY + 40, 64, 16, mc.options.keyJump);
        }

        // 4. Active Modules ArrayList (top-right)
        if (ModuleManager.ARRAYLIST.isEnabled()) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.getModules()) {
                if (m.isEnabled() && m != ModuleManager.HUD) {
                    active.add(m);
                }
            }
            active.sort(Comparator.comparingInt((Module m) -> CreamTheme.widthThin(m.getName())).reversed());

            int yOffset = 8;
            for (Module m : active) {
                int tw = CreamTheme.widthThin(m.getName());
                int x1 = screenWidth - tw - 16;
                int x2 = screenWidth - 8;
                AbstractGui.fill(ms, x1, yOffset, x2, yOffset + 13, CreamTheme.BG_PANEL);
                AbstractGui.fill(ms, x2 - 2, yOffset, x2, yOffset + 13, CreamTheme.LIME_PRIMARY);
                CreamTheme.drawThin(ms, m.getName(), x1 + 5, yOffset + 2, CreamTheme.TEXT_PRIMARY);
                yOffset += 14;
            }
        }
    }

    private static void drawKey(MatrixStack ms, String label, int x, int y, int w, int h, KeyBinding key) {
        boolean pressed = key.isDown();
        int fill = pressed ? CreamTheme.LIME_DIM : CreamTheme.BG_PANEL;
        int border = pressed ? CreamTheme.LIME_PRIMARY : CreamTheme.BORDER;
        int textColor = pressed ? CreamTheme.LIME_PRIMARY : CreamTheme.TEXT_MUTED;
        CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, fill, border);
        CreamTheme.drawThinCentered(ms, label, x + w / 2.0f, y + (h - 9) / 2.0f, textColor);
    }
}
