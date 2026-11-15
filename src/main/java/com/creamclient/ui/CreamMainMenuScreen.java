package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.config.CreamConfig;
import com.creamclient.media.MediaManager;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreamMainMenuScreen extends Screen {

    private static final String[] CUTE_BANNER = {
        "  ✧  ╭───────────────────────────────────────╮  ✧",
        "     │   ▄████▄  ██▀███  ▓█████ ▄▄▄   ██▄ ██ │   ",
        "  ⋆  │  ▒██▀ ▀█ ▓██ ▒ ██▒▓█   ▀▒████▄ ▓██▒██ │  ⋆",
        "     │  ▒▓█   ▄ ▓██ ░▄█ ▒▒███  ▒██ ▀█▄▓██░██ │   ",
        "  ✧  │  ▒▓▓▄██▒ ▒██▀▀█▄  ▒▓█  ▄░██▄▄██▒██ ██ │  ✧",
        "     ╰───────  ♡  c r e a m  c l i e n t  ♡  ─╯   "
    };

    private final List<MenuActionButton> menuButtons = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private float openAnim = 0.0f;

    public CreamMainMenuScreen() {
        super(new StringTextComponent("CreamClient Main Menu"));
    }

    @Override
    protected void init() {
        menuButtons.clear();
        openAnim = 0.0f;

        if (particles.isEmpty()) {
            Random rnd = new Random();
            for (int i = 0; i < 42; i++) {
                particles.add(new Particle(rnd.nextFloat() * 1920, rnd.nextFloat() * 1080,
                        (rnd.nextFloat() - 0.5f) * 0.6f, (rnd.nextFloat() - 0.5f) * 0.6f,
                        1.5f + rnd.nextFloat() * 2.0f));
            }
        }

        int btnWidth = 190;
        int btnHeight = 22;
        int startX = (this.width - btnWidth) / 2;
        int startY = this.height / 2 - 4;
        int gap = 26;

        menuButtons.add(new MenuActionButton(startX, startY, btnWidth, btnHeight, "Singleplayer",
                () -> Minecraft.getInstance().setScreen(new WorldSelectionScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap, btnWidth, btnHeight, "Multiplayer",
                () -> Minecraft.getInstance().setScreen(new MultiplayerScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 2, btnWidth, btnHeight, "Mods",
                () -> Minecraft.getInstance().setScreen(new ModListScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 3, btnWidth, btnHeight, "Options",
                () -> Minecraft.getInstance().setScreen(new OptionsScreen(this, Minecraft.getInstance().options))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 4, btnWidth, btnHeight, "Quit",
                () -> Minecraft.getInstance().stop()));

        int qx = this.width - 134;
        menuButtons.add(new MenuActionButton(qx, 10, 124, 18, "Client Settings",
                () -> Minecraft.getInstance().setScreen(new ClickGuiScreen(this))));
        menuButtons.add(new MenuActionButton(qx, 32, 124, 18, "Switch Background", () -> {
            if ("DefaultImage".equals(ModuleManager.BG_MODE.getMode())) {
                ModuleManager.BG_MODE.setMode("Animated");
            } else if ("Animated".equals(ModuleManager.BG_MODE.getMode())) {
                ModuleManager.BG_MODE.setMode("CustomImage");
                MediaManager.nextBackground();
            } else {
                ModuleManager.BG_MODE.setMode("DefaultImage");
            }
            CreamConfig.save();
        }));
        menuButtons.add(new MenuActionButton(qx, 54, 124, 18, "Music: Play / Next", () -> {
            if (!ModuleManager.MUSIC_MOD.isEnabled()) {
                ModuleManager.MUSIC_MOD.setEnabled(true);
            } else {
                MediaManager.nextTrack();
            }
            CreamConfig.save();
        }));
        menuButtons.add(new MenuActionButton(qx, 76, 124, 18, "Toggle Font", () -> {
            ModuleManager.FONT_MODE.cycle();
            CreamConfig.save();
        }));

        // Bottom-left GitHub link button
        menuButtons.add(new MenuActionButton(10, this.height - 38, 126, 18, "[GH] GitHub Repo", () -> {
            Util.getPlatform().openUri(CreamClientMod.GITHUB_URL);
        }));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        openAnim = CreamTheme.lerp(openAnim, 1.0f, ModuleManager.ANIM_SPEED.getFloat());
        int accent = CreamTheme.getAccent();

        String bgMode = ModuleManager.BG_MODE.getMode();
        ResourceLocation customTex = MediaManager.getCurrentBgTexture();
        if ("DefaultImage".equals(bgMode)) {
            this.minecraft.getTextureManager().bind(CreamTheme.DEFAULT_BG_TEX);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            AbstractGui.blit(ms, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            int dimAlpha = (int) (ModuleManager.BG_DIM.getFloat() * 255);
            fill(ms, 0, 0, this.width, this.height, (dimAlpha << 24));
        } else if ("CustomImage".equals(bgMode) && customTex != null) {
            this.minecraft.getTextureManager().bind(customTex);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            AbstractGui.blit(ms, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            int dimAlpha = (int) (ModuleManager.BG_DIM.getFloat() * 255);
            fill(ms, 0, 0, this.width, this.height, (dimAlpha << 24));
        } else {
            fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
            int gridColor = CreamTheme.withAlpha(accent, 14);
            for (int gx = 0; gx < this.width; gx += 32) {
                fill(ms, gx, 0, gx + 1, this.height, gridColor);
            }
            for (int gy = 0; gy < this.height; gy += 32) {
                fill(ms, 0, gy, this.width, gy + 1, gridColor);
            }
        }

        if (ModuleManager.MENU_PARTICLES.isEnabled()) {
            int pColor = CreamTheme.withAlpha(accent, 115);
            for (Particle p : particles) {
                p.update(this.width, this.height);
                int px = (int) p.x;
                int py = (int) p.y;
                int sz = (int) p.size;
                fill(ms, px, py, px + sz, py + sz, pColor);
            }
        }

        fill(ms, 0, 0, this.width, 2, accent);

        if (ModuleManager.MENU_LOGO.isEnabled()) {
            CreamTheme.drawBorderedRect(ms, 10, 10, 46, 46, CreamTheme.BG_PANEL, accent);
            this.minecraft.getTextureManager().bind(CreamTheme.LOGO_TEX);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            AbstractGui.blit(ms, 12, 12, 0, 0, 32, 32, 32, 32);
            CreamTheme.drawText(ms, CreamClientMod.CLIENT_NAME, 52, 17, accent);
            CreamTheme.drawText(ms, "Version " + CreamClientMod.CLIENT_VERSION, 52, 29, CreamTheme.TEXT_MUTED);
        }

        float slideOffset = (1.0f - openAnim) * -16.0f;
        int bannerStartY = Math.max(24, this.height / 2 - 112) + (int) slideOffset;
        float pulse = ModuleManager.BANNER_PULSE.isEnabled()
                ? (float) (0.78 + 0.22 * Math.sin(System.currentTimeMillis() / 650.0))
                : 1.0f;
        int r = (int) (((accent >> 16) & 0xFF) * pulse);
        int g = (int) (((accent >> 8) & 0xFF) * pulse);
        int b = (int) ((accent & 0xFF) * pulse);
        int bannerColor = (0xFF << 24) | (r << 16) | (g << 8) | b;

        for (int i = 0; i < CUTE_BANNER.length; i++) {
            CreamTheme.drawMonoCentered(ms, CUTE_BANNER[i], this.width / 2.0f, bannerStartY + i * 10, bannerColor);
        }

        int subY = bannerStartY + CUTE_BANNER.length * 10 + 8;
        CreamTheme.drawTextCentered(ms, "c r e a m   c l i e n t   " + CreamClientMod.CLIENT_VERSION + "   //   1 . 1 6 . 5",
                this.width / 2.0f, subY, CreamTheme.TEXT_PRIMARY);

        for (MenuActionButton btn : menuButtons) {
            btn.render(ms, mouseX, mouseY);
        }

        int footerY = this.height - 15;
        String leftInfo = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION
                + "  |  Font: " + ModuleManager.FONT_MODE.getMode()
                + "  |  Bg: " + bgMode;
        CreamTheme.drawText(ms, leftInfo, 10, footerY, CreamTheme.TEXT_PRIMARY);

        String trackInfo = ModuleManager.MUSIC_MOD.isEnabled()
                ? ("Playing: " + MediaManager.getCurrentTrackName())
                : "[RSHIFT] ClickGUI in-game";
        CreamTheme.drawText(ms, trackInfo, this.width - CreamTheme.getTextWidth(trackInfo) - 10, footerY, CreamTheme.getAccentSoft());

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (MenuActionButton btn : menuButtons) {
                if (btn.isHovered(mouseX, mouseY)) {
                    btn.action.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class Particle {
        float x, y, vx, vy, size;
        Particle(float x, float y, float vx, float vy, float size) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.size = size;
        }
        void update(int w, int h) {
            x += vx;
            y += vy;
            if (x < 0) x = w;
            if (x > w) x = 0;
            if (y < 0) y = h;
            if (y > h) y = 0;
        }
    }

    private static class MenuActionButton {
        final int x, y, w, h;
        final String label;
        final Runnable action;
        float hoverAnim = 0.0f;

        MenuActionButton(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.label = label; this.action = action;
        }

        boolean isHovered(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }

        void render(MatrixStack ms, int mx, int my) {
            boolean hovered = isHovered(mx, my);
            hoverAnim = CreamTheme.lerp(hoverAnim, hovered ? 1.0f : 0.0f, 0.2f);
            int accent = CreamTheme.getAccent();
            int fill = hovered ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD;
            int border = hovered ? accent : CreamTheme.BORDER;
            int textColor = hovered ? accent : CreamTheme.TEXT_PRIMARY;

            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, fill, border);
            if (hoverAnim > 0.05f) {
                int barW = Math.max(1, (int) (3 * hoverAnim));
                AbstractGui.fill(ms, x + 1, y + 1, x + 1 + barW, y + h - 1, accent);
            }
            CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, textColor);
        }
    }
}
