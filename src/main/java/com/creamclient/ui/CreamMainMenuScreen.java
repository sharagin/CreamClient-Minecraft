package com.creamclient.ui;

import com.creamclient.CreamClientMod;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.util.ArrayList;
import java.util.List;

public class CreamMainMenuScreen extends Screen {

    private static final String[] ASCII_BANNER = {
        "  ______   _______   ________   ______   __       __ ",
        " /      \\ /       \\ /        | /      \\ /  \\     /  |",
        "/$$$$$$\vert{}$$$$$$$\vert{}$$$$$$$$/ /$$$$$$  |$$\\   /$$ |",
        "$$\vert{}$$/ $$\vert{}__$$ |$$\vert{}__$$ |__$$\vert{}$$$\\ /$$$ \vert{}", "$$ |      $$    $$< $$\vert{}$$    $$\vert{}$$$$/$$$$ |",
        "$$\vert{}   __$$$$$$$\vert{}$$$$$/$$$$$$$$ |$$ $$ $$/$$ |",
        "$$\\__/  \vert{}$$ |  $$\vert{}$$ |_____ $$\vert{}$$ |$$\vert{}$$$/ $$ \vert{}", "$$    $$/$$\vert{}$$\vert{}$$\vert{}$$\vert{}$$\vert{}$$\vert{}$/  $$ \vert{}", " $$$$$$/$$/   $$/$$$$$$$$/ $$/$$/ $$/$$/ "
    };

    private final List<MenuActionButton> menuButtons = new ArrayList<>();

    public CreamMainMenuScreen() {
        super(new StringTextComponent("CreamClient Main Menu"));
    }

    @Override
    protected void init() {
        menuButtons.clear();

        int btnWidth = 190;
        int btnHeight = 22;
        int startX = (this.width - btnWidth) / 2;
        int startY = this.height / 2 - 4;
        int gap = 27;

        menuButtons.add(new MenuActionButton(startX, startY, btnWidth, btnHeight, "Singleplayer",
                () -> this.minecraft.setScreen(new WorldSelectionScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap, btnWidth, btnHeight, "Multiplayer",
                () -> this.minecraft.setScreen(new MultiplayerScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 2, btnWidth, btnHeight, "Mods",
                () -> this.minecraft.setScreen(new ModListScreen(this))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 3, btnWidth, btnHeight, "Options",
                () -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));
        menuButtons.add(new MenuActionButton(startX, startY + gap * 4, btnWidth, btnHeight, "Quit",
                () -> this.minecraft.stop()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        fill(ms, 0, 0, this.width, this.height, CreamTheme.BG_DARK);

        int gridStep = 32;
        for (int gx = 0; gx < this.width; gx += gridStep) {
            fill(ms, gx, 0, gx + 1, this.height, 0x0A3E6310);
        }
        for (int gy = 0; gy < this.height; gy += gridStep) {
            fill(ms, 0, gy, this.width, gy + 1, 0x0A3E6310);
        }

        fill(ms, 0, 0, this.width, 2, CreamTheme.LIME_PRIMARY);

        int bannerStartY = Math.max(22, this.height / 2 - 128);
        float pulse = (float) (0.75 + 0.25 * Math.sin(System.currentTimeMillis() / 700.0));
        int alpha = 255;
        int r = (int) (0xA3 * pulse);
        int g = 0xFF;
        int b = (int) (0x12 + 0x20 * (1.0f - pulse));
        int bannerColor = (alpha << 24) | (r << 16) | (g << 8) | b;

        for (int i = 0; i < ASCII_BANNER.length; i++) {
            CreamTheme.drawMonoCentered(ms, ASCII_BANNER[i], this.width / 2.0f, bannerStartY + i * 9, bannerColor);
        }

        int subY = bannerStartY + ASCII_BANNER.length * 9 + 8;
        CreamTheme.drawThinCentered(ms, "c r e a m   c l i e n t   //   1 . 1 6 . 5", this.width / 2.0f, subY, CreamTheme.TEXT_MUTED);

        for (MenuActionButton btn : menuButtons) {
            btn.render(ms, mouseX, mouseY);
        }

        int footerY = this.height - 16;
        CreamTheme.drawThin(ms, CreamClientMod.CLIENT_NAME + " v" + CreamClientMod.CLIENT_VERSION + "  |  Forge 1.16.5", 12, footerY, CreamTheme.TEXT_MUTED);
        String rightHint = "[RSHIFT] ClickGUI in-game";
        CreamTheme.drawThin(ms, rightHint, this.width - CreamTheme.widthThin(rightHint) - 12, footerY, CreamTheme.LIME_SOFT);

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (MenuActionButton btn : menuButtons) {
                if (btn.isHovered(mouseX, mouseY)) {
                    btn.action.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class MenuActionButton {
        final int x, y, w, h;
        final String label;
        final Runnable action;

        MenuActionButton(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.label = label;
            this.action = action;
        }

        boolean isHovered(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }

        void render(MatrixStack ms, int mx, int my) {
            boolean hovered = isHovered(mx, my);
            int fill = hovered ? CreamTheme.BG_HOVER : CreamTheme.BG_CARD;
            int border = hovered ? CreamTheme.LIME_PRIMARY : CreamTheme.BORDER;
            int textColor = hovered ? CreamTheme.LIME_PRIMARY : CreamTheme.TEXT_PRIMARY;

            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + h, fill, border);
            if (hovered) {
                AbstractGui.fill(ms, x + 1, y + 1, x + 3, y + h - 1, CreamTheme.LIME_PRIMARY);
            }
            CreamTheme.drawThinCentered(ms, label, x + w / 2.0f, y + (h - 9) / 2.0f, textColor);
        }
    }
}
