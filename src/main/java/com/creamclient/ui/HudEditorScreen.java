package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class HudEditorScreen extends Screen {
    private final Screen parent;
    private HudLayoutManager.WidgetRect dragging = null;
    private HudLayoutManager.WidgetRect selected = null;
    private float dragOffX, dragOffY;
    private boolean snapGrid = true;

    public HudEditorScreen(Screen parent) {
        super(new StringTextComponent("CreamClient UI Constructor"));
        this.parent = parent;
    }

    private boolean isRu() {
        return ModuleManager.LANG_MODE != null && "RU".equals(ModuleManager.LANG_MODE.getMode());
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        CreamTheme.updateDelta();
        fill(ms, 0, 0, width, height, 0x92080A08);

        if (snapGrid) {
            for (int gx = 0; gx < width; gx += 24) fill(ms, gx, 0, gx + 1, height, 0x16FFFFFF);
            for (int gy = 0; gy < height; gy += 24) fill(ms, 0, gy, width, gy + 1, 0x16FFFFFF);
        }

        int accent = CreamTheme.getAccent();
        String help = isRu()
                ? "Конструктор интерфейсов  |  ЛКМ: двигать  |  Колесо: размер  |  ПКМ: вкл/выкл  |  СКМ: стиль"
                : "UI Constructor  |  Drag: Move  |  Scroll: Scale  |  Right-Click: Toggle  |  Middle-Click: Style";
        CreamTheme.drawTextCentered(ms, help, width / 2f, 8, accent);

        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            boolean hov = mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh;
            boolean sel = (w == selected);
            int bg = w.visible ? (hov ? 0xDD1A261A : 0xCC111611) : 0x88181010;
            int border = sel ? 0xFFFFFFFF : (hov ? accent : (w.visible ? CreamTheme.withAlpha(accent, 140) : 0xFF553333));

            CreamTheme.drawRoundedRect(ms, w.x, w.y, rw, rh, 6f, bg);
            CreamTheme.drawRoundedOutline(ms, w.x, w.y, rw, rh, 6f, border);
            String title = (isRu() ? w.labelRu : w.labelEn) + (w.visible ? "" : " [OFF]");
            CreamTheme.drawText(ms, title, w.x + 6, w.y + 5, w.visible ? 0xFFF2F7EE : 0xFFAA7777);
            String meta = (int) w.x + "," + (int) w.y + "  x" + String.format("%.2f", w.scale) + (w.compact ? " [C]" : "");
            CreamTheme.drawText(ms, meta, w.x + 6, w.y + rh - 12, 0xFF93A88D);
        }

        // Bottom Constructor Inspector Bar
        int barW = 460, barH = 30, bx = (width - barW) / 2, by = height - 38;
        CreamTheme.drawRoundedRect(ms, bx, by, barW, barH, 6f, 0xE80D120D);
        CreamTheme.drawRoundedOutline(ms, bx, by, barW, barH, 6f, CreamTheme.withAlpha(accent, 150));

        drawBtn(ms, bx + 8, by + 5, 92, 20, (isRu() ? "Сетка: " : "Grid: ") + (snapGrid ? "ON" : "OFF"), mouseX, mouseY, accent, false);
        drawBtn(ms, bx + 106, by + 5, 86, 20, isRu() ? "Стиль [СКМ]" : "Style", mouseX, mouseY, accent, false);
        drawBtn(ms, bx + 198, by + 5, 82, 20, isRu() ? "Все ВКЛ" : "Show All", mouseX, mouseY, accent, false);
        drawBtn(ms, bx + 286, by + 5, 78, 20, isRu() ? "Сброс" : "Reset", mouseX, mouseY, 0xFFFF6B6B, false);
        drawBtn(ms, bx + 370, by + 5, 82, 20, isRu() ? "Готово" : "Save", mouseX, mouseY, accent, true);

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    private void drawBtn(MatrixStack ms, int x, int y, int w, int h, String txt, int mx, int my, int col, boolean filled) {
        boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + h;
        CreamTheme.drawRoundedRect(ms, x, y, w, h, 4f, filled ? (hov ? col : CreamTheme.withAlpha(col, 200)) : (hov ? 0x33FFFFFF : 0x22000000));
        if (!filled) CreamTheme.drawRoundedOutline(ms, x, y, w, h, 4f, CreamTheme.withAlpha(col, 150));
        CreamTheme.drawTextCentered(ms, txt, x + w / 2f, y + 6, filled ? 0xFF0A0F0A : col);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int barW = 460, bx = (width - barW) / 2, by = height - 38;
        if (button == 0 && mouseY >= by && mouseY <= by + 30) {
            if (hit(mouseX, mouseY, bx + 8, by + 5, 92, 20)) { snapGrid = !snapGrid; return true; }
            if (hit(mouseX, mouseY, bx + 106, by + 5, 86, 20)) {
                if (selected != null) { selected.compact = !selected.compact; CreamConfig.save(); }
                return true;
            }
            if (hit(mouseX, mouseY, bx + 198, by + 5, 82, 20)) {
                for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) w.visible = true;
                CreamConfig.save();
                return true;
            }
            if (hit(mouseX, mouseY, bx + 286, by + 5, 78, 20)) {
                HudLayoutManager.resetDefaults(); CreamConfig.save(); return true;
            }
            if (hit(mouseX, mouseY, bx + 370, by + 5, 82, 20)) {
                CreamConfig.save(); minecraft.setScreen(parent); return true;
            }
        }

        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            if (mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh) {
                selected = w;
                if (button == 0) {
                    dragging = w;
                    dragOffX = (float) mouseX - w.x;
                    dragOffY = (float) mouseY - w.y;
                    return true;
                } else if (button == 1) {
                    w.visible = !w.visible;
                    CreamConfig.save();
                    return true;
                } else if (button == 2) {
                    w.compact = !w.compact;
                    CreamConfig.save();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean hit(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging != null && button == 0) {
            float nx = (float) mouseX - dragOffX;
            float ny = (float) mouseY - dragOffY;
            if (snapGrid) {
                nx = Math.round(nx / 4f) * 4f;
                ny = Math.round(ny / 4f) * 4f;
            }
            dragging.x = Math.max(2, Math.min(width - dragging.baseW * dragging.scale - 2, nx));
            dragging.y = Math.max(2, Math.min(height - dragging.baseH * dragging.scale - 2, ny));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) { dragging = null; CreamConfig.save(); return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            if (mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh) {
                w.scale = Math.max(0.5f, Math.min(2.2f, w.scale + (float) delta * 0.05f));
                CreamConfig.save();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selected != null) {
            float step = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 ? 5f : 1f;
            if (keyCode == GLFW.GLFW_KEY_LEFT) { selected.x = Math.max(2, selected.x - step); CreamConfig.save(); return true; }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) { selected.x = Math.min(width - 20, selected.x + step); CreamConfig.save(); return true; }
            if (keyCode == GLFW.GLFW_KEY_UP) { selected.y = Math.max(2, selected.y - step); CreamConfig.save(); return true; }
            if (keyCode == GLFW.GLFW_KEY_DOWN) { selected.y = Math.min(height - 20, selected.y + step); CreamConfig.save(); return true; }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
