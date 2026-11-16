package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class HudEditorScreen extends Screen {
    private final Screen parent;
    private HudLayoutManager.WidgetRect dragging = null;
    private float dragOffX, dragOffY;

    public HudEditorScreen(Screen parent) {
        super(new StringTextComponent("CreamClient HUD Layout Editor"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        CreamTheme.updateDelta();
        fill(ms, 0, 0, width, height, 0x88080A08);

        // Subtle Linux-style alignment grid
        for (int gx = 0; gx < width; gx += 32) {
            fill(ms, gx, 0, gx + 1, height, 0x16FFFFFF);
        }
        for (int gy = 0; gy < height; gy += 32) {
            fill(ms, 0, gy, width, gy + 1, 0x16FFFFFF);
        }

        int accent = CreamTheme.getAccent();
        CreamTheme.drawTextCentered(ms, "HUD Layout Editor  |  Drag: Move  |  Scroll: Scale  |  Right-Click: Toggle", width / 2f, 10, accent);

        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            boolean hov = mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh;
            int bg = w.visible ? (hov ? 0xDD1A241A : 0xCC111611) : 0x88141010;
            int border = hov ? accent : (w.visible ? CreamTheme.withAlpha(accent, 140) : 0xFF553333);

            CreamTheme.drawRoundedRect(ms, w.x, w.y, rw, rh, 6f, bg);
            CreamTheme.drawRoundedOutline(ms, w.x, w.y, rw, rh, 6f, border);
            String title = w.label + (w.visible ? "" : " [OFF]");
            CreamTheme.drawText(ms, title, w.x + 6, w.y + 5, w.visible ? 0xFFF2F7EE : 0xFFAA7777);
            CreamTheme.drawText(ms, (int) w.x + "," + (int) w.y + "  x" + String.format("%.2f", w.scale), w.x + 6, w.y + rh - 12, 0xFF93A88D);
        }

        // Bottom control buttons: Reset & Done
        int bw = 100, bh = 22;
        int rx = width / 2 - bw - 8, dx = width / 2 + 8, by = height - 34;
        boolean hovR = mouseX >= rx && mouseX <= rx + bw && mouseY >= by && mouseY <= by + bh;
        boolean hovD = mouseX >= dx && mouseX <= dx + bw && mouseY >= by && mouseY <= by + bh;
        CreamTheme.drawRoundedRect(ms, rx, by, bw, bh, 6f, hovR ? 0xFF282020 : 0xFF1A1414);
        CreamTheme.drawRoundedOutline(ms, rx, by, bw, bh, 6f, 0xFFCC5555);
        CreamTheme.drawTextCentered(ms, "Reset Layout", rx + bw / 2f, by + 7, 0xFFFFAAAA);

        CreamTheme.drawRoundedRect(ms, dx, by, bw, bh, 6f, hovD ? CreamTheme.withAlpha(accent, 220) : CreamTheme.withAlpha(accent, 170));
        CreamTheme.drawTextCentered(ms, "Save & Back", dx + bw / 2f, by + 7, 0xFF0A0F0A);

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int bw = 100, bh = 22;
        int rx = width / 2 - bw - 8, dx = width / 2 + 8, by = height - 34;
        if (button == 0 && mouseX >= rx && mouseX <= rx + bw && mouseY >= by && mouseY <= by + bh) {
            HudLayoutManager.resetDefaults();
            CreamConfig.save();
            return true;
        }
        if (button == 0 && mouseX >= dx && mouseX <= dx + bw && mouseY >= by && mouseY <= by + bh) {
            CreamConfig.save();
            minecraft.setScreen(parent);
            return true;
        }

        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            if (mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh) {
                if (button == 0) {
                    dragging = w;
                    dragOffX = (float) mouseX - w.x;
                    dragOffY = (float) mouseY - w.y;
                    return true;
                } else if (button == 1) {
                    w.visible = !w.visible;
                    CreamConfig.save();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging != null && button == 0) {
            float nx = (float) mouseX - dragOffX;
            float ny = (float) mouseY - dragOffY;
            // Snap to 4px grid
            nx = Math.round(nx / 4f) * 4f;
            ny = Math.round(ny / 4f) * 4f;
            dragging.x = Math.max(2, Math.min(width - dragging.baseW * dragging.scale - 2, nx));
            dragging.y = Math.max(2, Math.min(height - dragging.baseH * dragging.scale - 2, ny));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging = null;
            CreamConfig.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (HudLayoutManager.WidgetRect w : HudLayoutManager.all().values()) {
            float rw = w.baseW * w.scale;
            float rh = w.baseH * w.scale;
            if (mouseX >= w.x && mouseX <= w.x + rw && mouseY >= w.y && mouseY <= w.y + rh) {
                w.scale = Math.max(0.6f, Math.min(1.8f, w.scale + (float) delta * 0.05f));
                CreamConfig.save();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
