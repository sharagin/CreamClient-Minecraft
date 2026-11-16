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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudOverlay {
    private static int currentFps = 0, frameCount = 0;
    private static long lastFpsTime = System.currentTimeMillis();
    private static float hotbarSmoothX = 0.0f, targetHpSmooth = 0.0f;

    private static final List<Long> LMB_CLICKS = new ArrayList<>();
    private static final List<Long> RMB_CLICKS = new ArrayList<>();
    private static final List<Integer> FPS_HISTORY = new ArrayList<>();

    private HudOverlay() {}

    public static void registerClick(int button) {
        long now = System.currentTimeMillis();
        if (button == 0) LMB_CLICKS.add(now); else if (button == 1) RMB_CLICKS.add(now);
    }

    private static int getCps(List<Long> list) {
        long now = System.currentTimeMillis();
        list.removeIf(t -> now - t > 1000L);
        return list.size();
    }

    public static void render(MatrixStack ms, int screenWidth, int screenHeight) {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFpsTime >= 1000L) {
            currentFps = frameCount; frameCount = 0; lastFpsTime = now;
            FPS_HISTORY.add(currentFps);
            if (FPS_HISTORY.size() > 40) FPS_HISTORY.remove(0);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.renderDebug || !ModuleManager.HUD.isEnabled()) return;

        int accent = CreamTheme.getAccent();
        int panelBg = CreamTheme.withAlpha(0x0E130E, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255));

        if (ModuleManager.VIGNETTE.isEnabled()) {
            int glow = CreamTheme.withAlpha(accent, (int) (ModuleManager.VIGNETTE_ALPHA.getFloat() * 90));
            AbstractGui.fill(ms, 0, 0, screenWidth, 3, glow);
            AbstractGui.fill(ms, 0, screenHeight - 3, screenWidth, screenHeight, glow);
            AbstractGui.fill(ms, 0, 0, 3, screenHeight, glow);
            AbstractGui.fill(ms, screenWidth - 3, 0, screenWidth, screenHeight, glow);
        }

        if (ModuleManager.COMPASS_HUD.isEnabled()) {
            int cw = 160, cx = (screenWidth - cw) / 2, cy = 8;
            CreamTheme.drawBorderedRect(ms, cx, cy, cx + cw, cy + 15, panelBg, CreamTheme.BORDER);
            float yaw = ((mc.player.yRot % 360.0f) + 360.0f) % 360.0f;
            CreamTheme.drawTextCentered(ms, yawToDir(yaw) + "  (" + (int) yaw + "°)", screenWidth / 2.0f, cy + 3.5f, accent);
        }

        int leftY = 8;
        if (ModuleManager.WATERMARK.isEnabled()) {
            StringBuilder sb = new StringBuilder(CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION);
            if (ModuleManager.WM_FPS.isEnabled()) sb.append("  |  ").append(currentFps).append(" FPS");
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
            CreamTheme.drawBorderedRect(ms, 8, leftY, 8 + w, leftY + 16, panelBg, accent);
            if (showLogo) {
                mc.getTextureManager().bind(CreamTheme.LOGO_TEX);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                AbstractGui.blit(ms, 13, leftY + 2, 0, 0, 12, 12, 12, 12);
            }
            CreamTheme.drawText(ms, text, 14 + logoOffset, leftY + 4, CreamTheme.TEXT_PRIMARY);
            leftY += 20;
        }

        if (ModuleManager.SESSION_INFO.isEnabled()) {
            long sec = (now - CreamClientMod.SESSION_START) / 1000L;
            String timeStr = String.format("Session: %02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
            int w = CreamTheme.getTextWidth(timeStr) + 12;
            CreamTheme.drawBorderedRect(ms, 8, leftY, 8 + w, leftY + 15, panelBg, CreamTheme.BORDER);
            CreamTheme.drawText(ms, timeStr, 14, leftY + 3.5f, CreamTheme.TEXT_MUTED);
            leftY += 19;
        }

        if (ModuleManager.FPS_GRAPH.isEnabled() && FPS_HISTORY.size() > 1) {
            int gw = 90, gh = 30;
            CreamTheme.drawBorderedRect(ms, 8, leftY, 8 + gw, leftY + gh, panelBg, CreamTheme.BORDER);
            int maxFps = 60;
            for (int f : FPS_HISTORY) if (f > maxFps) maxFps = f;
            for (int i = 1; i < FPS_HISTORY.size(); i++) {
                float x1 = 10 + (i - 1) * ((gw - 4) / 39.0f), x2 = 10 + i * ((gw - 4) / 39.0f);
                float y1 = leftY + gh - 3 - (FPS_HISTORY.get(i - 1) / (float) maxFps) * (gh - 6);
                float y2 = leftY + gh - 3 - (FPS_HISTORY.get(i) / (float) maxFps) * (gh - 6);
                CreamTheme.drawLine2D(ms, x1, y1, x2, y2, accent);
            }
            leftY += gh + 4;
        }

        if (ModuleManager.RADAR_HUD.isEnabled() && mc.level != null) {
            int rs = 64, rx = 8, ry = leftY;
            CreamTheme.drawBorderedRect(ms, rx, ry, rx + rs, ry + rs, panelBg, accent);
            int rcx = rx + rs / 2, rcy = ry + rs / 2;
            AbstractGui.fill(ms, rcx - 1, rcy - 1, rcx + 2, rcy + 2, 0xFFFFFFFF);
            double range = ModuleManager.RADAR_RANGE.getValue(), yawRad = Math.toRadians(-mc.player.yRot);
            for (Entity e : mc.level.entitiesForRendering()) {
                if (!(e instanceof LivingEntity) || e == mc.player) continue;
                double dx = e.getX() - mc.player.getX(), dz = e.getZ() - mc.player.getZ();
                if (dx * dx + dz * dz > range * range) continue;
                double rxPos = (dx * Math.cos(yawRad) - dz * Math.sin(yawRad)) / range * (rs / 2.0 - 4);
                double rzPos = (dx * Math.sin(yawRad) + dz * Math.cos(yawRad)) / range * (rs / 2.0 - 4);
                AbstractGui.fill(ms, (int) (rcx + rxPos) - 1, (int) (rcy + rzPos) - 1, (int) (rcx + rxPos) + 2, (int) (rcy + rzPos) + 2,
                        (e instanceof PlayerEntity) ? accent : 0xFFFF5555);
            }
            leftY += rs + 4;
        }

        if (ModuleManager.POTION_HUD.isEnabled() && !mc.player.getActiveEffects().isEmpty()) {
            for (EffectInstance eff : mc.player.getActiveEffects()) {
                int secs = eff.getDuration() / 20;
                String name = eff.getEffect().getDisplayName().getString() + " " + (secs / 60) + ":" + String.format("%02d", secs % 60);
                int w = CreamTheme.getTextWidth(name) + 10;
                CreamTheme.drawBorderedRect(ms, 8, leftY, 8 + w, leftY + 14, panelBg, CreamTheme.BORDER);
                CreamTheme.drawText(ms, name, 13, leftY + 3, CreamTheme.getAccentSoft());
                leftY += 16;
            }
        }

        if (ModuleManager.TARGET_HUD.isEnabled() && mc.crosshairPickEntity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) mc.crosshairPickEntity;
            int tx = screenWidth / 2 + 16, ty = screenHeight / 2 - 16, tw = 136, th = 34;
            CreamTheme.drawBorderedRect(ms, tx, ty, tx + tw, ty + th, panelBg, accent);
            String hpText = String.format("%.1f HP | %.1fm", target.getHealth(), mc.player.distanceTo(target));
            CreamTheme.drawText(ms, target.getName().getString(), tx + 6, ty + 4, CreamTheme.TEXT_PRIMARY);
            CreamTheme.drawText(ms, hpText, tx + 6, ty + 13, CreamTheme.TEXT_MUTED);
            float hpPct = Math.max(0.0f, Math.min(1.0f, target.getHealth() / target.getMaxHealth()));
            targetHpSmooth = CreamTheme.smooth(targetHpSmooth, hpPct, 0.25f);
            CreamTheme.drawBorderedRect(ms, tx + 6, ty + 24, tx + tw - 6, ty + 29, CreamTheme.BG_DARK, CreamTheme.BORDER);
            CreamTheme.drawRoundedRect(ms, tx + 7, ty + 25, Math.max(2, (tw - 14) * targetHpSmooth), 3, 1.0f, accent);
        }

        int bottomY = screenHeight - 24;
        if (ModuleManager.COORDS.isEnabled() && !(mc.screen instanceof CreamChatScreen)) {
            int x = (int) Math.floor(mc.player.getX()), y = (int) Math.floor(mc.player.getY()), z = (int) Math.floor(mc.player.getZ());
            String coords = String.format("XYZ %d, %d, %d", x, y, z);
            if (ModuleManager.COORDS_BIOME.isEnabled() && mc.level != null) {
                Biome b = mc.level.getBiome(new BlockPos(x, y, z));
                ResourceLocation reg = b != null ? b.getRegistryName() : null;
                coords += "  |  " + (reg != null ? reg.getPath().replace("_", " ") : "unknown");
            }
            int w = CreamTheme.getTextWidth(coords) + 12;
            CreamTheme.drawBorderedRect(ms, 8, bottomY, 8 + w, bottomY + 16, panelBg, CreamTheme.BORDER);
            CreamTheme.drawText(ms, coords, 14, bottomY + 4, CreamTheme.getAccentSoft());
            bottomY -= 20;
        }
        if (ModuleManager.SPEEDOMETER.isEnabled() && !(mc.screen instanceof CreamChatScreen)) {
            double dx = mc.player.getX() - mc.player.xOld, dz = mc.player.getZ() - mc.player.zOld;
            String sp = String.format("Speed: %.1f b/s", Math.sqrt(dx * dx + dz * dz) * 20.0);
            int w = CreamTheme.getTextWidth(sp) + 12;
            CreamTheme.drawBorderedRect(ms, 8, bottomY, 8 + w, bottomY + 16, panelBg, CreamTheme.BORDER);
            CreamTheme.drawText(ms, sp, 14, bottomY + 4, CreamTheme.TEXT_PRIMARY);
        }

        if (ModuleManager.KEYSTROKES.isEnabled()) {
            boolean showCps = ModuleManager.KEY_CPS.isEnabled();
            int baseX = screenWidth - 74, baseY = screenHeight - (showCps ? 86 : 68);
            drawKey(ms, "W", baseX + 22, baseY, 20, 18, mc.options.keyUp, panelBg, accent);
            drawKey(ms, "A", baseX, baseY + 20, 20, 18, mc.options.keyLeft, panelBg, accent);
            drawKey(ms, "S", baseX + 22, baseY + 20, 20, 18, mc.options.keyDown, panelBg, accent);
            drawKey(ms, "D", baseX + 44, baseY + 20, 20, 18, mc.options.keyRight, panelBg, accent);
            drawKey(ms, "SPACE", baseX, baseY + 40, 64, 16, mc.options.keyJump, panelBg, accent);
            if (showCps) {
                drawKey(ms, getCps(LMB_CLICKS) + " LMB", baseX, baseY + 58, 31, 16, mc.options.keyAttack, panelBg, accent);
                drawKey(ms, getCps(RMB_CLICKS) + " RMB", baseX + 33, baseY + 58, 31, 16, mc.options.keyUse, panelBg, accent);
            }
        }

        if (ModuleManager.ARRAYLIST.isEnabled()) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.getModules()) if (m.isEnabled() && m != ModuleManager.HUD) active.add(m);
            active.sort(Comparator.comparingInt((Module m) -> CreamTheme.getTextWidth(m.getName())).reversed());
            int yOffset = 8, idx = 0;
            for (Module m : active) {
                int col = CreamTheme.getAccentOffset(idx++);
                int tw = CreamTheme.getTextWidth(m.getName());
                int x1 = screenWidth - tw - 16, x2 = screenWidth - 8;
                CreamTheme.drawBorderedRect(ms, x1, yOffset, x2, yOffset + 13, panelBg, col);
                CreamTheme.drawText(ms, m.getName(), x1 + 5, yOffset + 2.5f, CreamTheme.TEXT_PRIMARY);
                yOffset += 15;
            }
        }

        if (ModuleManager.ARMOR_HUD.isEnabled()) {
            int ax = screenWidth / 2 + 12, ay = screenHeight - 54;
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
            int cx = screenWidth / 2, cy = screenHeight / 2, sz = (int) ModuleManager.CROSSHAIR_SIZE.getValue();
            AbstractGui.fill(ms, cx - sz, cy, cx - 2, cy + 1, accent);
            AbstractGui.fill(ms, cx + 3, cy, cx + sz + 1, cy + 1, accent);
            AbstractGui.fill(ms, cx, cy - sz, cx + 1, cy - 2, accent);
            AbstractGui.fill(ms, cx, cy + 3, cx + 1, cy + sz + 1, accent);
        }

        if (!(mc.screen instanceof CreamChatScreen)) {
            CreamChatManager.render(ms, screenWidth, screenHeight, false);
        }

        NotificationManager.render(ms, screenWidth, screenHeight);
    }

    private static String yawToDir(float yaw) {
        if (yaw >= 337.5 || yaw < 22.5) return "SOUTH";
        if (yaw < 67.5) return "SW";
        if (yaw < 112.5) return "WEST";
        if (yaw < 157.5) return "NW";
        if (yaw < 202.5) return "NORTH";
        if (yaw < 247.5) return "NE";
        if (yaw < 292.5) return "EAST";
        return "SE";
    }

    public static void renderCustomHotbar(MatrixStack ms, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        int accent = CreamTheme.getAccent();
        int panelBg = CreamTheme.withAlpha(0x0E130E, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255));
        int barW = 182, barH = 22, x = (screenWidth - barW) / 2, y = screenHeight - barH - 2;
        CreamTheme.drawBorderedRect(ms, x, y, x + barW, y + barH, panelBg, CreamTheme.BORDER);
        int targetSlotX = x + mc.player.inventory.selected * 20;
        if (hotbarSmoothX == 0.0f) hotbarSmoothX = targetSlotX;
        hotbarSmoothX = CreamTheme.smooth(hotbarSmoothX, targetSlotX, 0.35f);
        int selX = Math.round(hotbarSmoothX);
        CreamTheme.drawBorderedRect(ms, selX, y, selX + 22, y + barH, CreamTheme.withAlpha(accent, 45), accent);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.items.get(i);
            if (!stack.isEmpty()) {
                int ix = x + 3 + i * 20, iy = y + 3;
                mc.getItemRenderer().renderAndDecorateItem(stack, ix, iy);
                mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, ix, iy);
            }
        }
    }

    private static void drawKey(MatrixStack ms, String label, int x, int y, int w, int h, KeyBinding key, int bg, int accent) {
        boolean pressed = key.isDown();
        CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, pressed ? CreamTheme.getAccentDim() : bg, pressed ? accent : CreamTheme.BORDER);
        CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, pressed ? accent : CreamTheme.TEXT_MUTED);
    }
}
