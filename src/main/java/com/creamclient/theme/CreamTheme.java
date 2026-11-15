package com.creamclient.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public final class CreamTheme {
    private CreamTheme() {}

    // Lime & Dark Palette
    public static final int LIME_PRIMARY = 0xFFA3FF12;
    public static final int LIME_ACCENT  = 0xFF7CE800;
    public static final int LIME_SOFT    = 0xFFC4FF66;
    public static final int LIME_DIM     = 0xFF3E6310;
    public static final int LIME_GLOW    = 0x26A3FF12;

    public static final int BG_DARK      = 0xFF0A0D0A;
    public static final int BG_PANEL     = 0xD9101510;
    public static final int BG_CARD      = 0xE6151C15;
    public static final int BG_HOVER     = 0xFF1D281C;
    public static final int BORDER       = 0xFF273625;

    public static final int TEXT_PRIMARY = 0xFFF2F7EE;
    public static final int TEXT_MUTED   = 0xFF8AA084;

    public static final ResourceLocation THIN_FONT = new ResourceLocation("creamclient", "thin");
    public static final ResourceLocation MONO_FONT = new ResourceLocation("creamclient", "mono");

    public static IFormattableTextComponent thinText(String text) {
        return new StringTextComponent(text).withStyle(style -> style.withFont(THIN_FONT));
    }

    public static IFormattableTextComponent monoText(String text) {
        return new StringTextComponent(text).withStyle(style -> style.withFont(MONO_FONT));
    }

    public static void drawThin(MatrixStack ms, String text, float x, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        fr.draw(ms, thinText(text), x, y, color);
    }

    public static void drawThinCentered(MatrixStack ms, String text, float centerX, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        IFormattableTextComponent comp = thinText(text);
        int w = fr.width(comp);
        fr.draw(ms, comp, centerX - (w / 2.0f), y, color);
    }

    public static int widthThin(String text) {
        return Minecraft.getInstance().font.width(thinText(text));
    }

    public static void drawMonoCentered(MatrixStack ms, String text, float centerX, float y, int color) {
        FontRenderer fr = Minecraft.getInstance().font;
        IFormattableTextComponent comp = monoText(text);
        int w = fr.width(comp);
        fr.draw(ms, comp, centerX - (w / 2.0f), y, color);
    }

    public static void drawBorderedRect(MatrixStack ms, int x1, int y1, int x2, int y2, int fillColor, int borderColor) {
        AbstractGui.fill(ms, x1, y1, x2, y2, fillColor);
        AbstractGui.fill(ms, x1, y1, x2, y1 + 1, borderColor);
        AbstractGui.fill(ms, x1, y2 - 1, x2, y2, borderColor);
        AbstractGui.fill(ms, x1, y1, x1 + 1, y2, borderColor);
        AbstractGui.fill(ms, x2 - 1, y1, x2, y2, borderColor);
    }
}
