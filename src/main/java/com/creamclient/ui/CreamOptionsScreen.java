package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CreamOptionsScreen extends Screen {
    private final Screen parent;
    private final List<OptControl> controls = new ArrayList<>();
    private OptControl dragging = null;

    public CreamOptionsScreen(Screen parent) {
        super(new StringTextComponent("CreamClient Options"));
        this.parent = parent;
    }

    @Override protected void init() { rebuild(); }

    private void rebuild() {
        controls.clear();
        Minecraft mc = Minecraft.getInstance();
        int cx = this.width / 2 - 140, w = 280, y = 44;

        controls.add(new OptControl(cx, y, w, 22, Lang.tr("opt.fov"), 30, 110, mc.options.fov, val -> mc.options.fov = val));
        controls.add(new OptControl(cx, y + 28, w, 22, Lang.tr("opt.gamma"), 0.0, 2.0, mc.options.gamma, val -> mc.options.gamma = val));
        controls.add(new OptControl(cx, y + 56, w, 22, Lang.tr("opt.dist"), 2, 24, mc.options.renderDistance, val -> mc.options.renderDistance = (int) Math.round(val)));

        controls.add(new OptControl(cx, y + 90, 136, 22, Lang.tr("menu.font") + ModuleManager.FONT_MODE.getMode(), () -> {
            ModuleManager.FONT_MODE.cycle(); CreamConfig.save(); rebuild();
        }));
        controls.add(new OptControl(cx + 144, y + 90, 136, 22, Lang.tr("menu.lang") + ModuleManager.LANG_MODE.getMode(), () -> {
            ModuleManager.LANG_MODE.cycle(); CreamConfig.save(); rebuild();
        }));
        controls.add(new OptControl(cx, y + 120, w, 22, Lang.tr("opt.vanilla"), () -> mc.setScreen(new OptionsScreen(this, mc.options))));
        controls.add(new OptControl(cx, y + 150, w, 22, Lang.tr("opt.back"), () -> { mc.options.save(); CreamConfig.save(); mc.setScreen(parent); }));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
        int accent = CreamTheme.getAccent();
        fill(ms, 0, 0, this.width, 2, accent);
        CreamTheme.drawTextCentered(ms, Lang.tr("opt.title"), this.width / 2.0f, 16, accent);
        if (dragging != null) dragging.updateFromMouse(mouseX);
        for (OptControl c : controls) c.render(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (OptControl c : controls) {
                if (c.isHovered(mouseX, mouseY)) {
                    if (c.isSlider) { dragging = c; c.updateFromMouse(mouseX); }
                    else if (c.action != null) c.action.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) { dragging = null; return super.mouseReleased(mouseX, mouseY, button); }

    private interface DoubleConsumer { void accept(double val); }

    private static class OptControl {
        final int x, y, w, h; final String label; final boolean isSlider;
        final double min, max; double value; final DoubleConsumer onSlide; final Runnable action;

        OptControl(int x, int y, int w, int h, String label, double min, double max, double value, DoubleConsumer onSlide) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label;
            this.isSlider = true; this.min = min; this.max = max; this.value = value; this.onSlide = onSlide; this.action = null;
        }
        OptControl(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label;
            this.isSlider = false; this.min = 0; this.max = 1; this.value = 0; this.onSlide = null; this.action = action;
        }
        boolean isHovered(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
        void updateFromMouse(double mx) {
            double pct = Math.max(0.0, Math.min(1.0, (mx - x) / (double) w));
            value = min + pct * (max - min);
            if (onSlide != null) onSlide.accept(value);
        }
        void render(MatrixStack ms, int mx, int my) {
            boolean hov = isHovered(mx, my);
            int accent = CreamTheme.getAccent();
            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, hov ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD, hov ? accent : CreamTheme.BORDER);
            if (isSlider) {
                double pct = Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
                fill(ms, x + 2, y + h - 3, x + 2 + (int) ((w - 4) * pct), y + h - 1, accent);
                CreamTheme.drawText(ms, label + ": " + String.format("%.1f", value), x + 8, y + 6, CreamTheme.TEXT_PRIMARY);
            } else {
                CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, hov ? accent : CreamTheme.TEXT_PRIMARY);
            }
        }
    }
}
