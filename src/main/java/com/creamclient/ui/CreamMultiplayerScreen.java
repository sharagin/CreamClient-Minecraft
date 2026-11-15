package com.creamclient.ui;

import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CreamMultiplayerScreen extends Screen {
    private final Screen parent;
    private ServerList serverList;
    private int selectedIndex = -1;
    private TextFieldWidget ipField;
    private final List<MpButton> buttons = new ArrayList<>();
    private float openProgress = 0.0f;

    public CreamMultiplayerScreen(Screen parent) {
        super(new StringTextComponent("CreamClient Multiplayer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        openProgress = 0.0f;
        this.serverList = new ServerList(this.minecraft);
        this.serverList.load();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        ipField = new TextFieldWidget(this.font, this.width / 2 - 150, this.height - 62, 200, 18, new StringTextComponent("IP"));
        ipField.setMaxLength(128);
        ipField.setValue("play.example.com");
        this.children.add(ipField);

        buttons.clear();
        int by = this.height - 34;
        buttons.add(new MpButton(this.width / 2 - 205, by, 95, 20, Lang.tr("mp.connect"), () -> {
            if (selectedIndex >= 0 && selectedIndex < serverList.size()) connectTo(serverList.get(selectedIndex));
            else if (!ipField.getValue().trim().isEmpty()) connectTo(new ServerData("Direct", ipField.getValue().trim(), false));
        }));
        buttons.add(new MpButton(this.width / 2 + 56, this.height - 63, 94, 20, Lang.tr("mp.add"), () -> {
            String ip = ipField.getValue().trim();
            if (!ip.isEmpty()) { serverList.add(new ServerData(ip, ip, false)); serverList.save(); selectedIndex = serverList.size() - 1; }
        }));
        buttons.add(new MpButton(this.width / 2 - 102, by, 95, 20, Lang.tr("mp.delete"), () -> {
            if (selectedIndex >= 0 && selectedIndex < serverList.size()) { serverList.remove(serverList.get(selectedIndex)); serverList.save(); selectedIndex = -1; }
        }));
        buttons.add(new MpButton(this.width / 2 + 1, by, 95, 20, Lang.tr("mp.refresh"), () -> serverList.load()));
        buttons.add(new MpButton(this.width / 2 + 104, by, 95, 20, Lang.tr("mp.back"), () -> this.minecraft.setScreen(parent)));
    }

    private void connectTo(ServerData data) {
        if (data != null && this.minecraft != null) this.minecraft.setScreen(new ConnectingScreen(this, this.minecraft, data));
    }

    @Override public void tick() { ipField.tick(); }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        openProgress = Math.min(1.0f, openProgress + CreamTheme.getDelta() * 3.0f);
        float eased = CreamTheme.easeOutExpo(openProgress);
        fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
        int accent = CreamTheme.getAccent();
        fill(ms, 0, 0, this.width, 2, accent);
        CreamTheme.drawTextCentered(ms, Lang.tr("mp.title"), this.width / 2.0f, 14 + (1.0f - eased) * -10.0f, accent);

        int listX = this.width / 2 - 190, listW = 380, listY = 36, listH = this.height - 110;
        CreamTheme.drawBorderedRect(ms, listX, listY, listX + listW, listY + listH, CreamTheme.BG_PANEL, CreamTheme.BORDER);
        if (serverList.size() == 0) {
            CreamTheme.drawTextCentered(ms, Lang.tr("mp.empty"), this.width / 2.0f, listY + listH / 2.0f - 4, CreamTheme.TEXT_MUTED);
        } else {
            int rowY = listY + 6;
            for (int i = 0; i < serverList.size(); i++) {
                if (rowY + 26 > listY + listH) break;
                ServerData sd = serverList.get(i);
                boolean selected = (i == selectedIndex);
                boolean hov = mouseX >= listX + 6 && mouseX <= listX + listW - 6 && mouseY >= rowY && mouseY <= rowY + 24;
                CreamTheme.drawBorderedRect(ms, listX + 6, rowY, listX + listW - 6, rowY + 24,
                        selected ? CreamTheme.BG_HOVER : (hov ? 0xFF161E16 : CreamTheme.BG_CARD), selected ? accent : CreamTheme.BORDER);
                if (selected) AbstractGui.fill(ms, listX + 6, rowY, listX + 9, rowY + 24, accent);
                CreamTheme.drawText(ms, sd.name, listX + 14, rowY + 4, CreamTheme.TEXT_PRIMARY);
                CreamTheme.drawText(ms, sd.ip, listX + 14, rowY + 14, CreamTheme.TEXT_MUTED);
                rowY += 28;
            }
        }
        CreamTheme.drawText(ms, Lang.tr("mp.direct"), this.width / 2 - 150, this.height - 74, CreamTheme.TEXT_MUTED);
        ipField.render(ms, mouseX, mouseY, partialTicks);
        for (MpButton b : buttons) b.render(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ipField.mouseClicked(mouseX, mouseY, button);
        if (button == 0) {
            int listX = this.width / 2 - 190, listW = 380, rowY = 42;
            for (int i = 0; i < serverList.size(); i++) {
                if (mouseX >= listX + 6 && mouseX <= listX + listW - 6 && mouseY >= rowY && mouseY <= rowY + 24) {
                    if (selectedIndex == i) connectTo(serverList.get(i));
                    else { selectedIndex = i; ipField.setValue(serverList.get(i).ip); }
                    return true;
                }
                rowY += 28;
            }
            for (MpButton b : buttons) if (b.isHovered(mouseX, mouseY)) { b.action.run(); return true; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class MpButton {
        final int x, y, w, h; final String label; final Runnable action;
        MpButton(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label; this.action = action;
        }
        boolean isHovered(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
        void render(MatrixStack ms, int mx, int my) {
            boolean hov = isHovered(mx, my);
            int accent = CreamTheme.getAccent();
            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, hov ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD, hov ? accent : CreamTheme.BORDER);
            CreamTheme.drawTextCentered(ms, label, x + w / 2.0f, y + (h - 8) / 2.0f, hov ? accent : CreamTheme.TEXT_PRIMARY);
        }
    }
}
