package com.creamclient.theme;

import com.creamclient.module.ModuleManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public final class CreamTheme {
    private CreamTheme() {}

    private static final float HD_SCALE = 0.65f;

    public static final int BG_DARK      = 0xFF090C09;
    public static final int BG_PANEL     = 0xDC0F140F;
    public static final int BG_CARD      = 0xE8141B14;
    public static final int BG_HOVER     = 0xFF1D291C;
    public static final int BORDER       = 0xFF263524;

    public static final int TEXT_PRIMARY = 0xFFF2F7EE;
    public static final int TEXT_MUTED   = 0xFF93A88D;

    public static final ResourceLocation THIN_FONT = new ResourceLocation("creamclient", "thin");
    public static final ResourceLocation MONO_FONT = new ResourceLocation("creamclient", "mono");
    public static final ResourceLocation LOGO_TEX  = new ResourceLocation("creamclient", "textures/logo.png");
    public static final ResourceLocation DEFAULT_BG_TEX = new ResourceLocation("creamclient", "textures/default_bg.png");

    public static int getAccent() {
        String theme = ModuleManager.THEME_MODE != null ? ModuleManager.THEME_MODE.getMode() : "Lime";
        switch (theme) {
            case "Citrus":
                return 0xFFFF9F1C;
            case "Mint":
                return 0xFF2EFFB2;
            case "CyberCyan":
                return 0xFF00E5FF;
            case "Amethyst":
                return 0xFFC06CFF;
            case "Crimson":
                return 0xFFFF3B5C;
            case "CreamGold":
                return 0xFFFFD166;
            default:
                return 0xFFA3FF12;
        }
    }

    public static int getAccentSoft() {
        int c = getAccent();
        int r = Math.min(255, ((c >> 16) & 0xFF) + 45);
        int g = Math.min(255, ((c >> 8) & 0xFF) + 45);
        int b = Math.min(255, (c & 0xFF) + 45);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getAccentDim() {
        int c = getAccent();
        int r = ((c >> 16) & 0xFF) / 3;
        int g = ((c >> 8) & 0xFF) / 3;
        int b = (c & 0xFF) / 3;
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }

    public static boolean useVanillaFont() {
        return ModuleManager.FONT_MODE != null && "Vanilla".equals(ModuleManager.FONT_MODE.getMode());
    }

    public static IFormattableTextComponent thinText(String text) {
        return new StringTextComponent(text).withStyle(style -> style.withFont(THIN_FONT));
    }

    public static IFormattableTextComponent monoText(String text) {
        return new StringTextComponent(text).withStyle(style -> style.withFont(MONO_FONT));
    }

    public static void drawText(MatrixStack ms, String text, float x, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        if (useVanillaFont()) {
            fr.drawShadow(ms, text, x, y, color);
        } else {
            ms.pushPose();
            ms.translate(x, y, 0);
            ms.scale(HD_SCALE, HD_SCALE, 1.0f);
            fr.draw(ms, thinText(text), 0, 0, color);
            ms.popPose();
        }
    }

    public static void drawTextCentered(MatrixStack ms, String text, float centerX, float y, int color) {
        int w = getTextWidth(text);
        drawText(ms, text, centerX - (w / 2.0f), y, color);
    }

    public static int getTextWidth(String text) {
        FontRenderer fr = Minecraft.getInstance().font;
        if (useVanillaFont()) {
            return fr.width(text);
        }
        return Math.round(fr.width(thinText(text)) * HD_SCALE);
    }

    public static void drawMonoCentered(MatrixStack ms, String text, float centerX, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        IFormattableTextComponent comp = monoText(text);
        float scale = 0.72f;
        float w = fr.width(comp) * scale;
        ms.pushPose();
        ms.translate(centerX - (w / 2.0f), y, 0);
        ms.scale(scale, scale, 1.0f);
        fr.draw(ms, comp, 0, 0, color);
        ms.popPose();
    }

    public static void drawBorderedRect(MatrixStack ms, int x1, int y1, int x2, int y2, int fillColor, int borderColor) {
        AbstractGui.fill(ms, x1, y1, x2, y2, fillColor);
        AbstractGui.fill(ms, x1, y1, x2, y1 + 1, borderColor);
        AbstractGui.fill(ms, x1, y2 - 1, x2, y2, borderColor);
        AbstractGui.fill(ms, x1, y1, x1 + 1, y2, borderColor);
        AbstractGui.fill(ms, x2 - 1, y1, x2, y2, borderColor);
    }

    // Black gaming cursor with crisp accent outline
    public static void drawCustomCursor(MatrixStack ms, int mx, int my) {
        int accent = getAccent();
        int black = 0xFF0A0A0A;

        ms.pushPose();
        ms.translate(0, 0, 450);

        // Outer accent outline
        for (int row = 0; row < 12; row++) {
            int width = Math.max(1, 9 - (row / 2));
            if (row > 8) width = 2;
            AbstractGui.fill(ms, mx, my + row, mx + width + 1, my + row + 1, accent);
        }
        // Inner black body
        for (int row = 1; row < 11; row++) {
            int width = Math.max(1, 8 - (row / 2));
            if (row > 8) width = 1;
            AbstractGui.fill(ms, mx + 1, my + row, mx + width, my + row + 1, black);
        }

        ms.popPose();
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(1.0f, Math.max(0.0f, t));
    }
}
