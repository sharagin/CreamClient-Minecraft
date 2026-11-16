package com.creamclient.ui;

import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.Lang;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.WorldSummary;
import net.minecraft.world.storage.SaveFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreamSingleplayerScreen extends Screen {
    private final Screen parent;
    private List<WorldSummary> worlds = new ArrayList<>();
    private int selectedIndex = -1;
    private float openProgress = 0.0f;
    private final List<SpBtn> buttons = new ArrayList<>();

    public CreamSingleplayerScreen(Screen parent) {
        super(new StringTextComponent("CreamClient Singleplayer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        openProgress = 0.0f;
        loadWorlds();
        buttons.clear();
        int by = this.height - 34;
        buttons.add(new SpBtn(this.width / 2 - 200, by, 95, 22, Lang.tr("sp.play"), () -> {
            if (selectedIndex >= 0 && selectedIndex < worlds.size()) {
                this.minecraft.loadLevel(worlds.get(selectedIndex).getLevelId());
            }
        }));
        buttons.add(new SpBtn(this.width / 2 - 98, by, 120, 22, Lang.tr("sp.create"), () -> {
            this.minecraft.setScreen(CreateWorldScreen.create(this));
        }));
        buttons.add(new SpBtn(this.width / 2 + 28, by, 80, 22, Lang.tr("sp.delete"), () -> {
            if (selectedIndex >= 0 && selectedIndex < worlds.size()) {
                try (SaveFormat.LevelSave save = this.minecraft.getLevelSource().createAccess(worlds.get(selectedIndex).getLevelId())) {
                    save.deleteLevel();
                } catch (Exception ignored) {}
                selectedIndex = -1;
                loadWorlds();
            }
        }));
        buttons.add(new SpBtn(this.width / 2 + 114, by, 86, 22, Lang.tr("mp.back"), () -> this.minecraft.setScreen(parent)));
    }

    private void loadWorlds() {
        try {
            worlds = this.minecraft.getLevelSource().getLevelList();
            worlds.sort(null);
        } catch (Exception e) {
            worlds = new ArrayList<>();
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        openProgress = Math.min(1.0f, openProgress + CreamTheme.getDelta() * 3.0f);
        float eased = CreamTheme.easeOutExpo(openProgress);

        fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);
        int accent = CreamTheme.getAccent();
        fill(ms, 0, 0, this.width, 2, accent);
        CreamTheme.drawTextCentered(ms, Lang.tr("sp.title"), this.width / 2.0f, 14 + (1.0f - eased) * -10.0f, accent);

        int listX = this.width / 2 - 200, listW = 400, listY = 36, listH = this.height - 82;
        CreamTheme.drawBorderedRect(ms, listX, listY, listX + listW, listY + listH, CreamTheme.BG_PANEL, CreamTheme.BORDER);

        if (worlds.isEmpty()) {
            CreamTheme.drawTextCentered(ms, Lang.tr("sp.empty"), this.width / 2.0f, listY + listH / 2.0f - 4, CreamTheme.TEXT_MUTED);
        } else {
            int rowY = listY + 6;
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            for (int i = 0; i < worlds.size(); i++) {
                if (rowY + 30 > listY + listH) break;
                WorldSummary ls = worlds.get(i);
                boolean sel = (i == selectedIndex);
                boolean hov = mouseX >= listX + 6 && mouseX <= listX + listW - 6 && mouseY >= rowY && mouseY <= rowY + 28;
                CreamTheme.drawBorderedRect(ms, listX + 6, rowY, listX + listW - 6, rowY + 28,
                        sel ? CreamTheme.BG_HOVER : (hov ? 0xFF161E16 : CreamTheme.BG_CARD), sel ? accent : CreamTheme.BORDER);

                CreamTheme.drawText(ms, ls.getLevelName(), listX + 14, rowY + 5, CreamTheme.TEXT_PRIMARY);
                String dateStr = sdf.format(new Date(ls.getLastPlayed()));
                CreamTheme.drawText(ms, ls.getLevelId() + "  |  " + dateStr, listX + 14, rowY + 16, CreamTheme.TEXT_MUTED);

                String mode = ls.isHardcore() ? "HARDCORE" : ls.getGameMode().getName().toUpperCase();
                int modeCol = ls.isHardcore() ? 0xFFFF4444 : accent;
                int mw = CreamTheme.getTextWidth(mode) + 10;
                CreamTheme.drawBorderedRect(ms, listX + listW - mw - 12, rowY + 6, listX + listW - 12, rowY + 21, CreamTheme.BG_DARK, modeCol);
                CreamTheme.drawText(ms, mode, listX + listW - mw - 7, rowY + 9.5f, modeCol);

                rowY += 32;
            }
        }

        for (SpBtn b : buttons) b.render(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int listX = this.width / 2 - 200, listW = 400, rowY = 42;
            for (int i = 0; i < worlds.size(); i++) {
                if (mouseX >= listX + 6 && mouseX <= listX + listW - 6 && mouseY >= rowY && mouseY <= rowY + 28) {
                    if (selectedIndex == i) this.minecraft.loadLevel(worlds.get(i).getLevelId());
                    else selectedIndex = i;
                    return true;
                }
                rowY += 32;
            }
            for (SpBtn b : buttons) if (b.isHovered(mouseX, mouseY)) { b.action.run(); return true; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class SpBtn {
        final int x, y, w, h; final String label; final Runnable action;
        SpBtn(int x, int y, int w, int h, String label, Runnable action) {
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
