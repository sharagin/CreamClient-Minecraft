package com.creamclient.ui;

import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 280;
    private static final int ROW_HEIGHT = 28;

    public ClickGuiScreen() {
        super(new StringTextComponent("CreamClient ClickGUI"));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        fill(ms, 0, 0, this.width, this.height, 0x80060806);

        List<Module> modules = ModuleManager.getModules();
        int panelHeight = 42 + modules.size() * (ROW_HEIGHT + 4) + 12;
        int x = (this.width - PANEL_WIDTH) / 2;
        int y = (this.height - panelHeight) / 2;

        // Main panel
        CreamTheme.drawBorderedRect(ms, x, y, x + PANEL_WIDTH, y + panelHeight, CreamTheme.BG_PANEL, CreamTheme.LIME_PRIMARY);
        fill(ms, x, y, x + PANEL_WIDTH, y + 26, CreamTheme.BG_CARD);
        fill(ms, x, y + 26, x + PANEL_WIDTH, y + 27, CreamTheme.BORDER);
        CreamTheme.drawThin(ms, "CREAMCLIENT  //  MODULES", x + 12, y + 8, CreamTheme.LIME_PRIMARY);

        int rowY = y + 34;
        for (Module m : modules) {
            int rowX1 = x + 10;
            int rowX2 = x + PANEL_WIDTH - 10;
            boolean hovered = mouseX >= rowX1 && mouseX <= rowX2 && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT;

            int rowFill = hovered ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD;
            int rowBorder = m.isEnabled() ? CreamTheme.LIME_DIM : CreamTheme.BORDER;
            CreamTheme.drawBorderedRect(ms, rowX1, rowY, rowX2, rowY + ROW_HEIGHT, rowFill, rowBorder);

            CreamTheme.drawThin(ms, m.getName(), rowX1 + 8, rowY + 4, m.isEnabled() ? CreamTheme.LIME_SOFT : CreamTheme.TEXT_PRIMARY);
            CreamTheme.drawThin(ms, m.getDescription(), rowX1 + 8, rowY + 15, CreamTheme.TEXT_MUTED);

            // Toggle pill
            int pillW = 28;
            int pillH = 12;
            int px = rowX2 - pillW - 8;
            int py = rowY + (ROW_HEIGHT - pillH) / 2;
            int pillColor = m.isEnabled() ? CreamTheme.LIME_PRIMARY : 0xFF2B3529;
            CreamTheme.drawBorderedRect(ms, px, py, px + pillW, py + pillH, pillColor, CreamTheme.BORDER);

            int knobX = m.isEnabled() ? (px + pillW - 10) : (px + 2);
            fill(ms, knobX, py + 2, knobX + 8, py + pillH - 2, CreamTheme.BG_DARK);

            rowY += ROW_HEIGHT + 4;
        }

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            List<Module> modules = ModuleManager.getModules();
            int panelHeight = 42 + modules.size() * (ROW_HEIGHT + 4) + 12;
            int x = (this.width - PANEL_WIDTH) / 2;
            int y = (this.height - panelHeight) / 2;

            int rowY = y + 34;
            for (Module m : modules) {
                int rowX1 = x + 10;
                int rowX2 = x + PANEL_WIDTH - 10;
                if (mouseX >= rowX1 && mouseX <= rowX2 && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT) {
                    m.toggle();
                    return true;
                }
                rowY += ROW_HEIGHT + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
