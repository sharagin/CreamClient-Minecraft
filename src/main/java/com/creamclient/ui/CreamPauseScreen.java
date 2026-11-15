package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CreamPauseScreen extends Screen {
    private final List<PauseBtn> buttons = new ArrayList<>();

    public CreamPauseScreen() { super(new StringTextComponent("CreamClient Pause")); }

    @Override
    protected void init() {
        buttons.clear();
        int w = 190, h = 22, x = (this.width - w) / 2, y = this.height / 2 - 64, gap = 27;
        buttons.add(new PauseBtn(x, y, w, h, Lang.tr("pause.resume"), () -> this.minecraft.setScreen(null)));
        buttons.add(new PauseBtn(x, y + gap, w, h, Lang.tr("pause.clickgui"), () -> this.minecraft.setScreen(new ClickGuiScreen(this))));
        buttons.add(new PauseBtn(x, y + gap * 2, w, h, Lang.tr("pause.options"), () -> this.minecraft.setScreen(new CreamOptionsScreen(this))));
        buttons.add(new PauseBtn(x, y + gap * 3, w, h, Lang.tr("pause.disconnect"), () -> {
            if (this.minecraft.level != null) {
                boolean single = this.minecraft.isLocalServer();
                this.minecraft.level.disconnect();
                this.minecraft.clearLevel();
                this.minecraft.setScreen(single ? new CreamMainMenuScreen() : new CreamMultiplayerScreen(new CreamMainMenuScreen()));
            }
        }));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        fill(ms, 0, 0, this.width, this.height, 0x99060906);
        int accent = CreamTheme.getAccent();
        fill(ms, 0, 0, this.width, 2, accent);
        CreamTheme.drawTextCentered(ms, CreamClientMod.CLIENT_NAME.toUpperCase() + " " + CreamClientMod.CLIENT_VERSION + "  //  PAUSED",
                this.width / 2.0f, this.height / 2.0f - 90, accent);
        for (PauseBtn b : buttons) b.render(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) for (PauseBtn b : buttons) if (b.isHovered(mouseX, mouseY)) { b.action.run(); return true; }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class PauseBtn {
        final int x, y, w, h; final String label; final Runnable action;
        PauseBtn(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label; this.action = action;
        }
        boolean isHovered(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
        void render(MatrixStack ms, int mx, int my) {
            boolean hov = isHovered(mx, my);
            int accent = CreamTheme.getAccent();
            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, hov ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD, hov ? accent : CreamTheme.BORDER);
            if (hov) AbstractGui.fill(ms, x + 1, y + 1, x + 3, y + h - 1, accent);
            CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, hov ? accent : CreamTheme.TEXT_PRIMARY);
        }
    }
}
