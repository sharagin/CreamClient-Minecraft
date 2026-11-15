package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.config.CreamConfig;
import com.creamclient.media.MediaManager;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
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
    private float openProgress = 0.0f;

    public CreamMainMenuScreen() {
        super(new StringTextComponent("CreamClient Main Menu"));
    }

    @Override
    protected void init() {
        rebuildButtons();
        openProgress = 0.0f;

        if (particles.isEmpty()) {
            Random rnd = new Random();
            for (int i = 0; i < 48; i++) {
                particles.add(new Particle(rnd.nextFloat() * 1920, rnd.nextFloat() * 1080,
                        (rnd.nextFloat() - 0.5f) * 0.55f, (rnd.nextFloat() - 0.5f) * 0.55f,
                        1.5f + rnd.nextFloat() * 2.0f));
            }
        }
    }

    private void rebuildButtons() {
        menuButtons.clear();
        int btnWidth = 196;
        int btnHeight = 22;
        int startX = (this.width - btnWidth) / 2;
        int startY = this.height / 2 - 4;
        int gap = 26;

        menuButtons.add(new MenuActionButton(startX, startY, btnWidth, btnHeight, Lang.tr("menu.singleplayer"),
                () -> Minecraft.getInstance().setScreen(new WorldSelectionScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap, btnWidth, btnHeight, Lang.tr("menu.multiplayer"),
                () -> Minecraft.getInstance().setScreen(new CreamMultiplayerScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 2, btnWidth, btnHeight, Lang.tr("menu.mods"),
                () -> Minecraft.getInstance().setScreen(new ModListScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 3, btnWidth, btnHeight, Lang.tr("menu.options"),
                () -> Minecraft.getInstance().setScreen(new CreamOptionsScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 4, btnWidth, btnHeight, Lang.tr("menu.quit"),
                () -> Minecraft.getInstance().stop()));

        int qx = this.width - 140;
        menuButtons.add(new MenuActionButton(qx, 10, 130, 18, Lang.tr("menu.settings"),
                () -> Minecraft.getInstance().setScreen(new ClickGuiScreen(this))));
        menuButtons.add(new MenuActionButton(qx, 32, 130, 18, Lang.tr("menu.bg"), () -> {
            if ("DefaultImage".equals(ModuleManager.BG_MODE.getMode())) ModuleManager.BG_MODE.setMode("Animated");
            else if ("Animated".equals(ModuleManager.BG_MODE.getMode())) {
                ModuleManager.BG_MODE.setMode("CustomImage"); MediaManager.nextBackground();
            } else ModuleManager.BG_MODE.setMode("DefaultImage");
            CreamConfig.save();
        }));
        menuButtons.add(new MenuActionButton(qx, 54, 130, 18, Lang.tr("menu.music"), () -> {
            if (!ModuleManager.MUSIC_MOD.isEnabled()) ModuleManager.MUSIC_MOD.setEnabled(true);
            else MediaManager.nextTrack();
            CreamConfig.save();
        }));
        menuButtons.add(new MenuActionButton(qx, 76, 130, 18, Lang.tr("menu.font") + ModuleManager.FONT_MODE.getMode(), () -> {
            ModuleManager.FONT_MODE.cycle(); CreamConfig.save(); rebuildButtons();
        }));
        menuButtons.add(new MenuActionButton(qx, 98, 130, 18, Lang.tr("menu.lang") + ModuleManager.LANG_MODE.getMode(), () -> {
            ModuleManager.LANG_MODE.cycle(); CreamConfig.save(); rebuildButtons();
        }));

        menuButtons.add(new MenuActionButton(10, this.height - 42, 118, 22, "  GitHub Repo", () -> {
            Util.getPlatform().openUri(CreamClientMod.GITHUB_URL);
        }, CreamTheme.GITHUB_TEX));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        openProgress = Math.min(1.0f, openProgress + 0.045f);
        float eased = CreamTheme.easeOutCubic(openProgress);
        int accent = CreamTheme.getAccent();

        String bgMode = ModuleManager.BG_MODE.getMode();
        ResourceLocation customTex = MediaManager.getCurrentBgTexture();
        if ("DefaultImage".equals(bgMode)) {
            this.minecraft.getTextureManager().bind(CreamTheme.DEFAULT_BG_TEX);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            AbstractGui.blit(ms, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            fill(ms, 0, 0, this.width, this.height, ((int) (ModuleManager.BG_DIM.getFloat() * 255) << 24));
        } else if ("CustomImage".equals(bgMode) && customTex != null) {
            this.minecraft.getTextureManager().bind(customTex);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            AbstractGui.blit(ms, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            fill(ms, 0, 0, this.width, this.height, ((int) (ModuleManager.BG_DIM.getFloat() * 255) << 24));
        } else {
            fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
            int gridColor = CreamTheme.withAlpha(accent, 14);
            for (int gx = 0; gx < this.width; gx += 32) fill(ms, gx, 0, gx + 1, this.height, gridColor);
            for (int gy = 0; gy < this.height; gy += 32) fill(ms, 0, gy, this.width, gy + 1, gridColor);
        }

        // Particles + Constellation Lines
        if (ModuleManager.MENU_PARTICLES.isEnabled()) {
            int pColor = CreamTheme.withAlpha(accent, 130);
            for (int i = 0; i < particles.size(); i++) {
                Particle p = particles.get(i);
                p.update(this.width, this.height);
                fill(ms, (int) p.x, (int) p.y, (int) (p.x + p.size), (int) (p.y + p.size), pColor);
                if (ModuleManager.MENU_LINES.isEnabled()) {
                    float mdx = p.x - mouseX, mdy = p.y - mouseY;
                    if (mdx * mdx + mdy * mdy < 9000.0f) {
                        CreamTheme.drawLine2D(ms, p.x, p.y, mouseX, mouseY, CreamTheme.withAlpha(accent, 70));
                    }
                    for (int j = i + 1; j < particles.size(); j++) {
                        Particle q = particles.get(j);
                        float dx = p.x - q.x, dy = p.y - q.y;
                        if (dx * dx + dy * dy < 6400.0f) {
                            CreamTheme.drawLine2D(ms, p.x, p.y, q.x, q.y, CreamTheme.withAlpha(accent, 40));
                        }
                    }
                }
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

        float slideOffset = (1.0f - eased) * -22.0f;
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

        for (MenuActionButton btn : menuButtons) btn.render(ms, mouseX, mouseY);

        int footerY = this.height - 14;
        String leftInfo = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION
                + "  |  " + ModuleManager.FONT_MODE.getMode() + "  |  " + bgMode;
        CreamTheme.drawText(ms, leftInfo, 10, footerY, CreamTheme.TEXT_PRIMARY);

        String trackInfo = ModuleManager.MUSIC_MOD.isEnabled()
                ? ("Playing: " + MediaManager.getCurrentTrackName()) : "[RSHIFT] ClickGUI";
        CreamTheme.drawText(ms, trackInfo, this.width - CreamTheme.getTextWidth(trackInfo) - 10, footerY, CreamTheme.getAccentSoft());

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (MenuActionButton btn : menuButtons) {
                if (btn.isHovered(mouseX, mouseY)) { btn.action.run(); return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private static class Particle {
        float x, y, vx, vy, size;
        Particle(float x, float y, float vx, float vy, float size) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.size = size;
        }
        void update(int w, int h) {
            x += vx; y += vy;
            if (x < 0) x = w; if (x > w) x = 0;
            if (y < 0) y = h; if (y > h) y = 0;
        }
    }

    private static class MenuActionButton {
        final int x, y, w, h;
        final String label;
        final Runnable action;
        final ResourceLocation icon;
        float hoverAnim = 0.0f;

        MenuActionButton(int x, int y, int w, int h, String label, Runnable action) {
            this(x, y, w, h, label, action, null);
        }
        MenuActionButton(int x, int y, int w, int h, String label, Runnable action, ResourceLocation icon) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label; this.action = action; this.icon = icon;
        }
        boolean isHovered(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
        void render(MatrixStack ms, int mx, int my) {
            boolean hovered = isHovered(mx, my);
            hoverAnim = CreamTheme.lerp(hoverAnim, hovered ? 1.0f : 0.0f, 0.22f);
            float eased = CreamTheme.easeOutCubic(hoverAnim);
            int accent = CreamTheme.getAccent();
            int fill = hovered ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD;
            int border = hovered ? accent : CreamTheme.BORDER;
            int textColor = hovered ? accent : CreamTheme.TEXT_PRIMARY;

            int offset = (int) (eased * 2.0f);
            CreamTheme.drawBorderedRect(ms, x - offset, y, x + w + offset, y + h, fill, border);
            if (eased > 0.05f) AbstractGui.fill(ms, x - offset + 1, y + 1, x - offset + 1 + Math.max(1, (int) (3 * eased)), y + h - 1, accent);
            if (icon != null) {
                Minecraft.getInstance().getTextureManager().bind(icon);
                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                AbstractGui.blit(ms, x + 6, y + (h - 14) / 2, 0, 0, 14, 14, 14, 14);
                RenderSystem.disableBlend();
                CreamTheme.drawText(ms, label, x + 22, y + (h - 8) / 2.0f, textColor);
            } else {
                CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, textColor);
            }
        }
    }
}
