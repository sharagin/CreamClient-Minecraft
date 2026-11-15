package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudOverlay {
    private static int currentFps = 0;
    private static int frameCount = 0;
    private static long lastFpsTime = System.currentTimeMillis();
    private static float hotbarSmoothX = 0.0f;

    private HudOverlay() {}

    public static void render(MatrixStack ms, int screenWidth, int screenHeight) {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFpsTime >= 1000L) {
            currentFps = frameCount;
            frameCount = 0;
            lastFpsTime = now;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.renderDebug || !ModuleManager.HUD.isEnabled()) {
            return;
        }

        int accent = CreamTheme.getAccent();
        int panelAlpha = (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255);
        int panelBg = CreamTheme.withAlpha(0x0E130E, panelAlpha);

        if (ModuleManager.WATERMARK.isEnabled()) {
            StringBuilder sb = new StringBuilder(CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION);
            if (ModuleManager.WM_FPS.isEnabled()) {
                sb.append("  |  ").append(currentFps).append(" FPS");
            }
            if (ModuleManager.WM_PING.isEnabled()) {
                int ping = 0;
                if (mc.getConnection() != null) {
                    NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
                    if (info != null) ping = info.getLatency();
                }
                sb.append("  |  ").append(ping).append("ms");
            }
            String text = sb.toString();
            boolean showLogo = ModuleManager.WM_LOGO.isEnabled();
            int logoOffset = showLogo ? 16 : 0;
            int w = CreamTheme.getTextWidth(text) + 14 + logoOffset;

            CreamTheme.drawBorderedRect(ms, 8, 8, 8 + w, 24, panelBg, CreamTheme.BORDER);
            AbstractGui.fill(ms, 8, 8, 10, 24, accent);
            if (showLogo) {
                mc.getTextureManager().bind(CreamTheme.LOGO_TEX);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                AbstractGui.blit(ms, 13, 10, 0, 0, 12, 12, 12, 12);
            }
            CreamTheme.drawText(ms, text, 14 + logoOffset, 12, CreamTheme.TEXT_PRIMARY);
        }

        if (ModuleManager.COORDS.isEnabled()) {
            int x = (int) Math.floor(mc.player.getX());
            int y = (int) Math.floor(mc.player.getY());
            int z = (int) Math.floor(mc.player.getZ());
            String coords = String.format("XYZ %d, %d, %d", x, y, z);
            if (ModuleManager.COORDS_BIOME.isEnabled() && mc.level != null) {
                Biome b = mc.level.getBiome(new BlockPos(x, y, z));
                ResourceLocation reg = b != null ? b.getRegistryName() : null;
                String biome = reg != null ? reg.getPath().replace("_", " ") : "unknown";
                coords += "  |  " + biome;
            }
            int w = CreamTheme.getTextWidth(coords) + 12;
            int cy = screenHeight - 24;
            CreamTheme.drawBorderedRect(ms, 8, cy, 8 + w, cy + 16, panelBg, CreamTheme.BORDER);
            CreamTheme.drawText(ms, coords, 14, cy + 4, CreamTheme.getAccentSoft());
        }

        if (ModuleManager.KEYSTROKES.isEnabled()) {
            int baseX = screenWidth - 74;
            int baseY = screenHeight - 68;
            drawKey(ms, "W", baseX + 22, baseY, 20, 18, mc.options.keyUp, panelBg, accent);
            drawKey(ms, "A", baseX, baseY + 20, 20, 18, mc.options.keyLeft, panelBg, accent);
            drawKey(ms, "S", baseX + 22, baseY + 20, 20, 18, mc.options.keyDown, panelBg, accent);
            drawKey(ms, "D", baseX + 44, baseY + 20, 20, 18, mc.options.keyRight, panelBg, accent);
            drawKey(ms, "SPACE", baseX, baseY + 40, 64, 16, mc.options.keyJump, panelBg, accent);
        }

        if (ModuleManager.ARRAYLIST.isEnabled()) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.getModules()) {
                if (m.isEnabled() && m != ModuleManager.HUD) {
                    active.add(m);
                }
            }
            active.sort(Comparator.comparingInt((Module m) -> CreamTheme.getTextWidth(m.getName())).reversed());

            int yOffset = 8;
            for (Module m : active) {
                int tw = CreamTheme.getTextWidth(m.getName());
                int x1 = screenWidth - tw - 16;
                int x2 = screenWidth - 8;
                AbstractGui.fill(ms, x1, yOffset, x2, yOffset + 13, panelBg);
                AbstractGui.fill(ms, x2 - 2, yOffset, x2, yOffset + 13, accent);
                CreamTheme.drawText(ms, m.getName(), x1 + 5, yOffset + 2.5f, CreamTheme.TEXT_PRIMARY);
                yOffset += 14;
            }
        }

        if (ModuleManager.ARMOR_HUD.isEnabled()) {
            int ax = screenWidth / 2 + 12;
            int ay = screenHeight - 54;
            for (int i = 3; i >= 0; i--) {
                ItemStack stack = mc.player.inventory.armor.get(i);
                if (!stack.isEmpty()) {
                    mc.getItemRenderer().renderAndDecorateItem(stack, ax, ay);
                    if (stack.isDamageableItem()) {
                        int pct = Math.max(0, 100 - (int) ((stack.getDamageValue() * 100.0) / stack.getMaxDamage()));
                        CreamTheme.drawText(ms, pct + "%", ax + 1, ay - 9, pct > 30 ? CreamTheme.getAccentSoft() : 0xFFFF5555);
                    }
                    ax += 20;
                }
            }
        }

        if (ModuleManager.CROSSHAIR.isEnabled()) {
            int cx = screenWidth / 2;
            int cy = screenHeight / 2;
            int sz = (int) ModuleManager.CROSSHAIR_SIZE.getValue();
            AbstractGui.fill(ms, cx - sz, cy, cx - 2, cy + 1, accent);
            AbstractGui.fill(ms, cx + 3, cy, cx + sz + 1, cy + 1, accent);
            AbstractGui.fill(ms, cx, cy - sz, cx + 1, cy - 2, accent);
            AbstractGui.fill(ms, cx, cy + 3, cx + 1, cy + sz + 1, accent);
        }
    }

    public static void renderCustomHotbar(MatrixStack ms, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int accent = CreamTheme.getAccent();
        int panelAlpha = (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255);
        int panelBg = CreamTheme.withAlpha(0x0E130E, panelAlpha);

        int barW = 182;
        int barH = 22;
        int x = (screenWidth - barW) / 2;
        int y = screenHeight - barH - 2;

        CreamTheme.drawBorderedRect(ms, x, y, x + barW, y + barH, panelBg, CreamTheme.BORDER);

        int targetSlotX = x + mc.player.inventory.selected * 20;
        if (hotbarSmoothX == 0.0f) hotbarSmoothX = targetSlotX;
        hotbarSmoothX = CreamTheme.lerp(hotbarSmoothX, targetSlotX, 0.25f);

        int selX = Math.round(hotbarSmoothX);
        CreamTheme.drawBorderedRect(ms, selX, y, selX + 22, y + barH, CreamTheme.withAlpha(accent, 45), accent);

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.items.get(i);
            if (!stack.isEmpty()) {
                int ix = x + 3 + i * 20;
                int iy = y + 3;
                mc.getItemRenderer().renderAndDecorateItem(stack, ix, iy);
                mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, ix, iy);
            }
        }
    }

    private static void drawKey(MatrixStack ms, String label, int x, int y, int w, int h, KeyBinding key, int bg, int accent) {
        boolean pressed = key.isDown();
        int fill = pressed ? CreamTheme.getAccentDim() : bg;
        int border = pressed ? accent : CreamTheme.BORDER;
        int textColor = pressed ? accent : CreamTheme.TEXT_MUTED;
        CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, fill, border);
        CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, textColor);
    }
}
