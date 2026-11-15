package com.creamclient.theme;

import com.creamclient.module.ModuleManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

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
    public static final ResourceLocation CURSOR_TEX = new ResourceLocation("creamclient", "textures/cursor.png");
    public static final ResourceLocation GITHUB_TEX = new ResourceLocation("creamclient", "textures/github.png");

    public static int getBasePalette() {
        String theme = ModuleManager.THEME_MODE != null ? ModuleManager.THEME_MODE.getMode() : "Lime";
        switch (theme) {
            case "Citrus": return 0xFFFF9F1C;
            case "Mint": return 0xFF2EFFB2;
            case "CyberCyan": return 0xFF00E5FF;
            case "Amethyst": return 0xFFC06CFF;
            case "Crimson": return 0xFFFF3B5C;
            case "CreamGold": return 0xFFFFD166;
            default: return 0xFFA3FF12;
        }
    }

    public static int getAccent() {
        String mode = ModuleManager.COLOR_MODE != null ? ModuleManager.COLOR_MODE.getMode() : "Breathing";
        if ("Rainbow".equals(mode)) {
            float hue = (System.currentTimeMillis() % 4500L) / 4500.0f;
            return Color.HSBtoRGB(hue, 0.78f, 1.0f);
        }
        int base = getBasePalette();
        if ("Breathing".equals(mode)) {
            float wave = (float) (0.78 + 0.22 * Math.sin(System.currentTimeMillis() / 550.0));
            int r = (int) (((base >> 16) & 0xFF) * wave);
            int g = (int) (((base >> 8) & 0xFF) * wave);
            int b = (int) ((base & 0xFF) * wave);
            return (0xFF << 24) | (r << 16) | (g << 8) | b;
        }
        return base;
    }

    public static int getAccentOffset(int index) {
        String mode = ModuleManager.COLOR_MODE != null ? ModuleManager.COLOR_MODE.getMode() : "Breathing";
        if ("Rainbow".equals(mode)) {
            float hue = ((System.currentTimeMillis() + index * 160L) % 4500L) / 4500.0f;
            return Color.HSBtoRGB(hue, 0.78f, 1.0f);
        }
        return getAccent();
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

    public static void drawLine2D(MatrixStack ms, float x1, float y1, float x2, float y2, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, x1, y1, 0).color(r, g, b, a).endVertex();
        buf.vertex(mat, x2, y2, 0).color(r, g, b, a).endVertex();
        tess.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void drawCustomCursor(MatrixStack ms, int mx, int my) {
        Minecraft mc = Minecraft.getInstance();
        ms.pushPose();
        ms.translate(0, 0, 500);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bind(CURSOR_TEX);
        AbstractGui.blit(ms, mx - 2, my - 2, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        ms.popPose();
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(1.0f, Math.max(0.0f, t));
    }

    public static float easeOutCubic(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        float inv = 1.0f - clamped;
        return 1.0f - inv * inv * inv;
    }
}
