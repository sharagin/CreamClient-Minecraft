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

    private static long lastFrameNs = System.nanoTime();
    private static float deltaSec = 0.016f;

    public static final int BG_DARK      = 0xFF090C09;
    public static final int BG_PANEL     = 0xDC0F140F;
    public static final int BG_CARD      = 0xE8141B14;
    public static final int BG_HOVER     = 0xFF1D291C;
    public static final int BORDER       = 0xFF263524;

    public static final int TEXT_PRIMARY = 0xFFF2F7EE;
    public static final int TEXT_MUTED   = 0xFF93A88D;

    public static final ResourceLocation THIN_FONT   = new ResourceLocation("creamclient", "thin");
    public static final ResourceLocation BANNER_FONT = new ResourceLocation("creamclient", "banner");
    public static final ResourceLocation LOGO_TEX    = new ResourceLocation("creamclient", "textures/logo.png");
    public static final ResourceLocation DEFAULT_BG_TEX = new ResourceLocation("creamclient", "textures/default_bg.png");
    public static final ResourceLocation CURSOR_TEX  = new ResourceLocation("creamclient", "textures/cursor.png");
    public static final ResourceLocation GITHUB_TEX  = new ResourceLocation("creamclient", "textures/github.png");

    public static void updateDelta() {
        long now = System.nanoTime();
        deltaSec = Math.min(0.05f, Math.max(0.001f, (now - lastFrameNs) / 1_000_000_000.0f));
        lastFrameNs = now;
    }

    public static float getDelta() { return deltaSec; }

    public static float smooth(float current, float target, float speed) {
        float t = 1.0f - (float) Math.exp(-speed * 60.0f * deltaSec);
        return current + (target - current) * Math.min(1.0f, Math.max(0.0f, t));
    }

    public static float getCornerRadius() {
        return ModuleManager.CORNER_RADIUS != null ? ModuleManager.CORNER_RADIUS.getFloat() : 5.0f;
    }

    public static int getBasePalette() {
        String mode = ModuleManager.COLOR_MODE != null ? ModuleManager.COLOR_MODE.getMode() : "Breathing";
        if ("CustomRGB".equals(mode) && ModuleManager.CUSTOM_R != null) {
            int r = (int) ModuleManager.CUSTOM_R.getValue();
            int g = (int) ModuleManager.CUSTOM_G.getValue();
            int b = (int) ModuleManager.CUSTOM_B.getValue();
            return (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
        }
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
        return (0xFF << 24) | ((((c >> 16) & 0xFF) / 3) << 16) | ((((c >> 8) & 0xFF) / 3) << 8) | ((c & 0xFF) / 3);
    }

    public static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }

    public static boolean useVanillaFont() {
        return ModuleManager.FONT_MODE != null && "Vanilla".equals(ModuleManager.FONT_MODE.getMode());
    }

    private static float getFontScale() {
        return ModuleManager.FONT_SCALE != null ? ModuleManager.FONT_SCALE.getFloat() : 0.65f;
    }

    public static IFormattableTextComponent thinText(String text) {
        return new StringTextComponent(text).withStyle(style -> style.withFont(THIN_FONT));
    }

    public static void drawBannerTitle(MatrixStack ms, String text, float centerX, float y, float scale, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        IFormattableTextComponent comp = new StringTextComponent(text).withStyle(style -> style.withFont(BANNER_FONT));
        float w = fr.width(comp) * scale;
        ms.pushPose();
        ms.translate(centerX - (w / 2.0f), y, 0);
        ms.scale(scale, scale, 1.0f);
        fr.draw(ms, comp, 0, 0, color);
        ms.popPose();
    }

    public static void drawText(MatrixStack ms, String text, float x, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        if (useVanillaFont()) {
            fr.drawShadow(ms, text, x, y, color);
        } else {
            float sc = getFontScale();
            ms.pushPose();
            ms.translate(x, y, 0);
            ms.scale(sc, sc, 1.0f);
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
        if (useVanillaFont()) return fr.width(text);
        return Math.round(fr.width(thinText(text)) * getFontScale());
    }

    public static void drawRoundedRect(MatrixStack ms, float x, float y, float w, float h, float radius, int color) {
        float r = Math.max(0.0f, Math.min(radius, Math.min(w, h) / 2.0f));
        if (r < 0.8f) {
            AbstractGui.fill(ms, (int) x, (int) y, (int) (x + w), (int) (y + h), color);
            return;
        }
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, x + w / 2.0f, y + h / 2.0f, 0).color(red, green, blue, a).endVertex();
        emitRoundedPerimeter(buf, mat, x, y, w, h, r, red, green, blue, a);
        buf.vertex(mat, x + w - r, y, 0).color(red, green, blue, a).endVertex();
        tess.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void drawRoundedOutline(MatrixStack ms, float x, float y, float w, float h, float radius, int color) {
        float r = Math.max(0.0f, Math.min(radius, Math.min(w, h) / 2.0f));
        if (r < 0.8f) {
            int x1 = (int) x, y1 = (int) y, x2 = (int) (x + w), y2 = (int) (y + h);
            AbstractGui.fill(ms, x1, y1, x2, y1 + 1, color);
            AbstractGui.fill(ms, x1, y2 - 1, x2, y2, color);
            AbstractGui.fill(ms, x1, y1, x1 + 1, y2, color);
            AbstractGui.fill(ms, x2 - 1, y1, x2, y2, color);
            return;
        }
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.0f);
        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        emitRoundedPerimeter(buf, mat, x, y, w, h, r, red, green, blue, a);
        tess.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void emitRoundedPerimeter(BufferBuilder buf, Matrix4f mat, float x, float y, float w, float h, float r, float red, float green, float blue, float a) {
        int segs = 7;
        for (int i = 0; i <= segs; i++) {
            double ang = -Math.PI / 2.0 + (Math.PI / 2.0) * (i / (double) segs);
            buf.vertex(mat, (float) (x + w - r + Math.cos(ang) * r), (float) (y + r + Math.sin(ang) * r), 0).color(red, green, blue, a).endVertex();
        }
        for (int i = 0; i <= segs; i++) {
            double ang = (Math.PI / 2.0) * (i / (double) segs);
            buf.vertex(mat, (float) (x + w - r + Math.cos(ang) * r), (float) (y + h - r + Math.sin(ang) * r), 0).color(red, green, blue, a).endVertex();
        }
        for (int i = 0; i <= segs; i++) {
            double ang = Math.PI / 2.0 + (Math.PI / 2.0) * (i / (double) segs);
            buf.vertex(mat, (float) (x + r + Math.cos(ang) * r), (float) (y + h - r + Math.sin(ang) * r), 0).color(red, green, blue, a).endVertex();
        }
        for (int i = 0; i <= segs; i++) {
            double ang = Math.PI + (Math.PI / 2.0) * (i / (double) segs);
            buf.vertex(mat, (float) (x + r + Math.cos(ang) * r), (float) (y + r + Math.sin(ang) * r), 0).color(red, green, blue, a).endVertex();
        }
    }

    public static void drawBorderedRect(MatrixStack ms, int x1, int y1, int x2, int y2, int fillColor, int borderColor) {
        float w = x2 - x1, h = y2 - y1;
        float r = Math.min(getCornerRadius(), Math.min(w, h) / 2.5f);
        drawRoundedRect(ms, x1, y1, w, h, r, fillColor);
        drawRoundedOutline(ms, x1, y1, w, h, r, borderColor);
    }

    public static void drawLine2D(MatrixStack ms, float x1, float y1, float x2, float y2, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f, r = ((color >> 16) & 0xFF) / 255.0f, g = ((color >> 8) & 0xFF) / 255.0f, b = (color & 0xFF) / 255.0f;
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

    public static float lerp(float a, float b, float t) { return a + (b - a) * Math.min(1.0f, Math.max(0.0f, t)); }
    public static float easeOutCubic(float t) { float c = Math.min(1.0f, Math.max(0.0f, t)); float inv = 1.0f - c; return 1.0f - inv * inv * inv; }
    public static float easeOutExpo(float t) { float c = Math.min(1.0f, Math.max(0.0f, t)); return c >= 1.0f ? 1.0f : 1.0f - (float) Math.pow(2.0, -10.0 * c); }
}
