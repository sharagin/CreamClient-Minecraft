package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.media.MediaManager;
import com.creamclient.module.Category;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.module.Setting;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int COL_WIDTH = 132, COL_GAP = 8, HEADER_H = 22, MOD_H = 20, SET_H = 18;
    private final Screen parent;
    private float openProgress = 0.0f;
    private float targetScrollY = 0.0f, smoothScrollY = 0.0f;
    private Setting.SliderSetting draggingSlider = null;
    private int dragSliderX = 0, dragSliderW = 100;

    public ClickGuiScreen() { this(null); }
    public ClickGuiScreen(Screen parent) { super(new StringTextComponent("CreamClient ClickGUI")); this.parent = parent; }

    @Override protected void init() { openProgress = 0.0f; targetScrollY = 0.0f; smoothScrollY = 0.0f; }
    @Override public void onClose() { CreamConfig.save(); if (this.minecraft != null) this.minecraft.setScreen(parent); }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        float speed = ModuleManager.ANIM_SPEED.getFloat();
        openProgress = Math.min(1.0f, openProgress + CreamTheme.getDelta() * (1.8f + speed * 4.0f));
        float eased = CreamTheme.easeOutExpo(openProgress);
        smoothScrollY = CreamTheme.smooth(smoothScrollY, targetScrollY, 0.25f);

        fill(ms, 0, 0, this.width, this.height, ((int) (145 * eased) << 24) | 0x060806);

        if (draggingSlider != null) {
            double pct = Math.max(0.0, Math.min(1.0, (mouseX - dragSliderX) / (double) dragSliderW));
            draggingSlider.setValue(draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin()));
            if (draggingSlider == ModuleManager.MUSIC_VOL) MediaManager.updateVolume();
        }

        int accent = CreamTheme.getAccent();
        Category[] cats = Category.values();
        int totalW = cats.length * COL_WIDTH + (cats.length - 1) * COL_GAP;
        int startX = (this.width - totalW) / 2;
        int startY = (int) (24 + smoothScrollY + (1.0f - eased) * -22.0f);
        int panelBg = CreamTheme.withAlpha(0x0E130E, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255));

        for (int c = 0; c < cats.length; c++) {
            Category cat = cats[c];
            int cx = startX + c * (COL_WIDTH + COL_GAP), cy = startY;
            CreamTheme.drawBorderedRect(ms, cx, cy, cx + COL_WIDTH, cy + HEADER_H, CreamTheme.BG_CARD, accent);
            CreamTheme.drawTextCentered(ms, cat.getTitle().toUpperCase(), cx + COL_WIDTH / 2.0f, cy + 7, accent);
            cy += HEADER_H + 2;

            List<Module> mods = ModuleManager.getByCategory(cat);
            for (Module m : mods) {
                m.setAnimProgress(CreamTheme.smooth(m.getAnimProgress(), m.isEnabled() ? 1.0f : 0.0f, speed));
                boolean hovered = mouseX >= cx && mouseX <= cx + COL_WIDTH && mouseY >= cy && mouseY <= cy + MOD_H;
                CreamTheme.drawBorderedRect(ms, cx, cy, cx + COL_WIDTH, cy + MOD_H, hovered ? CreamTheme.BG_HOVER : panelBg, m.isEnabled() ? accent : CreamTheme.BORDER);
                CreamTheme.drawText(ms, m.getName(), cx + 7, cy + 6, m.isEnabled() ? CreamTheme.getAccentSoft() : CreamTheme.TEXT_PRIMARY);
                if (!m.getSettings().isEmpty()) CreamTheme.drawText(ms, m.isExpanded() ? "-" : "+", cx + COL_WIDTH - 11, cy + 6, CreamTheme.TEXT_MUTED);
                cy += MOD_H + 1;

                if (m.isExpanded()) {
                    for (Setting s : m.getSettings()) {
                        CreamTheme.drawBorderedRect(ms, cx + 2, cy, cx + COL_WIDTH - 2, cy + SET_H, CreamTheme.BG_DARK, CreamTheme.BORDER);
                        if (s instanceof Setting.BoolSetting) {
                            Setting.BoolSetting bs = (Setting.BoolSetting) s;
                            CreamTheme.drawText(ms, s.getName(), cx + 8, cy + 5, CreamTheme.TEXT_MUTED);
                            CreamTheme.drawBorderedRect(ms, cx + COL_WIDTH - 14, cy + 5, cx + COL_WIDTH - 6, cy + 13, bs.isEnabled() ? accent : CreamTheme.BG_CARD, CreamTheme.BORDER);
                        } else if (s instanceof Setting.ModeSetting) {
                            Setting.ModeSetting msSet = (Setting.ModeSetting) s;
                            CreamTheme.drawText(ms, s.getName() + ":", cx + 8, cy + 5, CreamTheme.TEXT_MUTED);
                            String val = msSet.getMode();
                            CreamTheme.drawText(ms, val, cx + COL_WIDTH - CreamTheme.getTextWidth(val) - 8, cy + 5, accent);
                        } else if (s instanceof Setting.SliderSetting) {
                            Setting.SliderSetting ss = (Setting.SliderSetting) s;
                            double pct = (ss.getValue() - ss.getMin()) / (ss.getMax() - ss.getMin());
                            CreamTheme.drawRoundedRect(ms, cx + 4, cy + SET_H - 4, Math.max(2, (float) ((COL_WIDTH - 8) * pct)), 2, 1.0f, accent);
                            CreamTheme.drawText(ms, s.getName(), cx + 8, cy + 4, CreamTheme.TEXT_MUTED);
                            String val = String.valueOf(ss.getValue());
                            CreamTheme.drawText(ms, val, cx + COL_WIDTH - CreamTheme.getTextWidth(val) - 8, cy + 4, CreamTheme.TEXT_PRIMARY);
                        }
                        cy += SET_H + 1;
                    }
                }
            }
        }

        CreamTheme.drawTextCentered(ms, Lang.tr("gui.hint"), this.width / 2.0f, this.height - 15, CreamTheme.TEXT_MUTED);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        targetScrollY = Math.max(-420.0f, Math.min(40.0f, targetScrollY + (float) (delta * 24.0)));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Category[] cats = Category.values();
        int totalW = cats.length * COL_WIDTH + (cats.length - 1) * COL_GAP;
        int startX = (this.width - totalW) / 2;

        for (int c = 0; c < cats.length; c++) {
            int cx = startX + c * (COL_WIDTH + COL_GAP), cy = (int) (24 + smoothScrollY) + HEADER_H + 2;
            for (Module m : ModuleManager.getByCategory(cats[c])) {
                if (mouseX >= cx && mouseX <= cx + COL_WIDTH && mouseY >= cy && mouseY <= cy + MOD_H) {
                    if (button == 0) m.toggle();
                    else if (button == 1 && !m.getSettings().isEmpty()) m.setExpanded(!m.isExpanded());
                    return true;
                }
                cy += MOD_H + 1;
                if (m.isExpanded()) {
                    for (Setting s : m.getSettings()) {
                        if (mouseX >= cx && mouseX <= cx + COL_WIDTH && mouseY >= cy && mouseY <= cy + SET_H) {
                            if (s instanceof Setting.BoolSetting && button == 0) ((Setting.BoolSetting) s).toggle();
                            else if (s instanceof Setting.ModeSetting && button == 0) {
                                ((Setting.ModeSetting) s).cycle();
                                if (s == ModuleManager.BG_MODE && "CustomImage".equals(ModuleManager.BG_MODE.getMode())) MediaManager.reloadAssets();
                            } else if (s instanceof Setting.SliderSetting && button == 0) {
                                draggingSlider = (Setting.SliderSetting) s;
                                dragSliderX = cx + 4; dragSliderW = COL_WIDTH - 8;
                            }
                            return true;
                        }
                        cy += SET_H + 1;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) { draggingSlider = null; return super.mouseReleased(mouseX, mouseY, button); }
    @Override public boolean isPauseScreen() { return false; }
}
