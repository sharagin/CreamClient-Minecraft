package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CreamOptionsScreen extends Screen {
    private final Screen parent;
    private int activeTab = 0;
    private final List<OptControl> controls = new ArrayList<>();
    private OptControl dragging = null;

    public CreamOptionsScreen(Screen parent) { super(new StringTextComponent("CreamClient Options")); this.parent = parent; }

    @Override protected void init() { rebuild(); }

    private void rebuild() {
        controls.clear();
        Minecraft mc = Minecraft.getInstance();
        int cx = this.width / 2 - 150, w = 300;
        controls.add(new OptControl(cx, 34, 96, 20, Lang.tr("opt.tab.game"), () -> { activeTab = 0; rebuild(); }, activeTab == 0));
        controls.add(new OptControl(cx + 102, 34, 96, 20, Lang.tr("opt.tab.sound"), () -> { activeTab = 1; rebuild(); }, activeTab == 1));
        controls.add(new OptControl(cx + 204, 34, 96, 20, Lang.tr("opt.tab.client"), () -> { activeTab = 2; rebuild(); }, activeTab == 2));

        int y = 64;
        if (activeTab == 0) {
            controls.add(new OptControl(cx, y, w, 20, Lang.tr("opt.fov"), 30, 110, mc.options.fov, val -> mc.options.fov = val));
            controls.add(new OptControl(cx, y + 25, w, 20, Lang.tr("opt.gamma"), 0.0, 2.0, mc.options.gamma, val -> mc.options.gamma = val));
            controls.add(new OptControl(cx, y + 50, w, 20, Lang.tr("opt.dist"), 2, 24, mc.options.renderDistance, val -> mc.options.renderDistance = (int) Math.round(val)));
            controls.add(new OptControl(cx, y + 75, w, 20, Lang.tr("opt.fps"), 30, 260, mc.options.framerateLimit, val -> mc.options.framerateLimit = (int) Math.round(val)));
            controls.add(new OptControl(cx, y + 102, 146, 20, Lang.tr("opt.bob") + (mc.options.bobView ? "ON" : "OFF"), () -> { mc.options.bobView = !mc.options.bobView; rebuild(); }, false));
            controls.add(new OptControl(cx + 154, y + 102, 146, 20, Lang.tr("opt.shadows") + (mc.options.entityShadows ? "ON" : "OFF"), () -> { mc.options.entityShadows = !mc.options.entityShadows; rebuild(); }, false));
            controls.add(new OptControl(cx, y + 128, w, 20, Lang.tr("opt.vanilla"), () -> mc.setScreen(new OptionsScreen(this, mc.options)), false));
        } else if (activeTab == 1) {
            SoundCategory[] cats = {SoundCategory.MASTER, SoundCategory.MUSIC, SoundCategory.BLOCKS, SoundCategory.HOSTILE, SoundCategory.PLAYERS, SoundCategory.WEATHER};
            for (int i = 0; i < cats.length; i++) {
                SoundCategory cat = cats[i];
                controls.add(new OptControl(cx, y + i * 25, w, 20, cat.getName().toUpperCase(), 0.0, 1.0, mc.options.getSoundSourceVolume(cat),
                        val -> mc.options.setSoundCategoryVolume(cat, (float) val)));
            }
        } else {
            controls.add(new OptControl(cx, y, 146, 20, "Palette: " + ModuleManager.THEME_MODE.getMode(), () -> { ModuleManager.THEME_MODE.cycle(); CreamConfig.save(); rebuild(); }, false));
            controls.add(new OptControl(cx + 154, y, 146, 20, "Mode: " + ModuleManager.COLOR_MODE.getMode(), () -> { ModuleManager.COLOR_MODE.cycle(); CreamConfig.save(); rebuild(); }, false));
            controls.add(new OptControl(cx, y + 25, 146, 20, Lang.tr("menu.font") + ModuleManager.FONT_MODE.getMode(), () -> { ModuleManager.FONT_MODE.cycle(); CreamConfig.save(); rebuild(); }, false));
            controls.add(new OptControl(cx + 154, y + 25, 146, 20, Lang.tr("menu.lang") + ModuleManager.LANG_MODE.getMode(), () -> { ModuleManager.LANG_MODE.cycle(); CreamConfig.save(); rebuild(); }, false));
            controls.add(new OptControl(cx, y + 52, w, 20, "Corner Radius", 0, 10, ModuleManager.CORNER_RADIUS.getValue(), val -> ModuleManager.CORNER_RADIUS.setValue(val)));
            controls.add(new OptControl(cx, y + 77, w, 20, "Panel Alpha", 0.25, 1.0, ModuleManager.PANEL_ALPHA.getValue(), val -> ModuleManager.PANEL_ALPHA.setValue(val)));
            controls.add(new OptControl(cx, y + 102, w, 20, "Font Scale", 0.50, 0.85, ModuleManager.FONT_SCALE.getValue(), val -> ModuleManager.FONT_SCALE.setValue(val)));
            controls.add(new OptControl(cx, y + 128, w, 20, "Open Full ClickGUI (All Modules)", () -> mc.setScreen(new ClickGuiScreen(this)), false));
        }
        controls.add(new OptControl(cx, this.height - 32, w, 22, Lang.tr("opt.back"), () -> { mc.options.save(); CreamConfig.save(); mc.setScreen(parent); }, false));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
        int accent = CreamTheme.getAccent();
        fill(ms, 0, 0, this.width, 2, accent);
        CreamTheme.drawTextCentered(ms, Lang.tr("opt.title"), this.width / 2.0f, 14, accent);
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
        final int x, y, w, h; final String label; final boolean isSlider, active;
        final double min, max; double value; final DoubleConsumer onSlide; final Runnable action;
        OptControl(int x, int y, int w, int h, String label, double min, double max, double value, DoubleConsumer onSlide) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label;
            this.isSlider = true; this.active = false; this.min = min; this.max = max; this.value = value; this.onSlide = onSlide; this.action = null;
        }
        OptControl(int x, int y, int w, int h, String label, Runnable action, boolean active) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label;
            this.isSlider = false; this.active = active; this.min = 0; this.max = 1; this.value = 0; this.onSlide = null; this.action = action;
        }
        boolean isHovered(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
        void updateFromMouse(double mx) {
            double pct = Math.max(0.0, Math.min(1.0, (mx - x) / (double) w));
            value = min + pct * (max - min);
            if (onSlide != null) onSlide.accept(value);
        }
        void render(MatrixStack ms, int mx, int my) {
            boolean hov = isHovered(mx, my); int accent = CreamTheme.getAccent();
            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, (hov || active) ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD, (hov || active) ? accent : CreamTheme.BORDER);
            if (isSlider) {
                double pct = Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
                CreamTheme.drawRoundedRect(ms, x + 2, y + h - 4, Math.max(2, (float) ((w - 4) * pct)), 2, 1.0f, accent);
                CreamTheme.drawText(ms, label + ": " + String.format("%.2f", value), x + 8, y + 5, CreamTheme.TEXT_PRIMARY);
            } else {
                CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, (hov || active) ? accent : CreamTheme.TEXT_PRIMARY);
            }
        }
    }
}
