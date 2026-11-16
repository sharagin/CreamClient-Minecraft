package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CreamChatScreen extends Screen {
    private static final List<String> SENT_HISTORY = new ArrayList<>();
    private final String initialText;
    private TextFieldWidget input;
    private int historyIndex = -1;
    private boolean draggingChat = false;
    private int dragOffsetX = 0, dragOffsetY = 0;

    public CreamChatScreen(String initialText) { super(new StringTextComponent("CreamChat")); this.initialText = initialText; }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        historyIndex = SENT_HISTORY.size();
        input = new TextFieldWidget(this.font, 12, this.height - 22, this.width - 24, 16, new StringTextComponent(""));
        input.setMaxLength(256); input.setBordered(false); input.setValue(initialText); input.setFocus(true);
        this.children.add(input); this.setInitialFocus(input);
    }

    @Override public void onClose() { CreamConfig.save(); super.onClose(); }
    @Override public void tick() { input.tick(); }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        if (draggingChat) {
            CreamChatManager.chatX = Math.max(6, Math.min(this.width - 296, mouseX - dragOffsetX));
            CreamChatManager.chatY = Math.max(40, Math.min(this.height - 46, mouseY - dragOffsetY));
        }
        int accent = CreamTheme.getAccent();
        int hx = Math.max(6, Math.min(CreamChatManager.chatX, this.width - 296));
        int hy = Math.max(8, CreamChatManager.getResolvedY(this.height) - 120);
        CreamTheme.drawBorderedRect(ms, hx, hy, hx + 112, hy + 14, CreamTheme.BG_CARD, accent);
        CreamTheme.drawText(ms, ":: Drag Chat ::", hx + 8, hy + 3, accent);
        CreamChatManager.render(ms, this.width, this.height, true);
        CreamTheme.drawBorderedRect(ms, 8, this.height - 26, this.width - 8, this.height - 6, CreamTheme.BG_PANEL, accent);
        input.render(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String msg = input.getValue().trim();
            if (!msg.isEmpty() && this.minecraft.player != null) { SENT_HISTORY.add(msg); this.minecraft.player.chat(msg); }
            this.minecraft.setScreen(null);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP && historyIndex > 0) { historyIndex--; input.setValue(SENT_HISTORY.get(historyIndex)); return true; }
        if (keyCode == GLFW.GLFW_KEY_DOWN && historyIndex < SENT_HISTORY.size() - 1) { historyIndex++; input.setValue(SENT_HISTORY.get(historyIndex)); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int hx = Math.max(6, Math.min(CreamChatManager.chatX, this.width - 296));
            int hy = Math.max(8, CreamChatManager.getResolvedY(this.height) - 120);
            if (mouseX >= hx && mouseX <= hx + 112 && mouseY >= hy && mouseY <= hy + 14) {
                draggingChat = true; dragOffsetX = (int) mouseX - hx; dragOffsetY = (int) mouseY - CreamChatManager.getResolvedY(this.height);
                return true;
            }
        }
        input.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingChat) { draggingChat = false; CreamConfig.save(); }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean isPauseScreen() { return false; }
}
