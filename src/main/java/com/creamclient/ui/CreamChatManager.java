package com.creamclient.ui;

import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public final class CreamChatManager {
    private static final List<ChatEntry> MESSAGES = new ArrayList<>();
    public static int chatX = 10;
    public static int chatY = -1; // -1 = auto bottom-left

    private CreamChatManager() {}

    public static void addMessage(ITextComponent msg) {
        if (msg == null) return;
        MESSAGES.add(new ChatEntry(msg.getString()));
        if (MESSAGES.size() > 80) MESSAGES.remove(0);
    }

    public static int getResolvedY(int screenHeight) {
        if (chatY >= 0) return Math.min(chatY, screenHeight - 60);
        return screenHeight - 54;
    }

    public static void render(MatrixStack ms, int screenWidth, int screenHeight, boolean chatOpen) {
        if (!ModuleManager.CUSTOM_CHAT.isEnabled() || MESSAGES.isEmpty()) return;

        long now = System.currentTimeMillis();
        int maxShow = chatOpen ? 12 : 7;
        int chatW = 290;
        int x = Math.max(6, Math.min(chatX, screenWidth - chatW - 6));
        int baseY = getResolvedY(screenHeight);

        int accent = CreamTheme.getAccent();
        int panelBg = CreamTheme.withAlpha(0x0B100B, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 230));

        int shown = 0;
        int curY = baseY;
        for (int i = MESSAGES.size() - 1; i >= 0 && shown < maxShow; i--) {
            ChatEntry entry = MESSAGES.get(i);
            long age = now - entry.createdAt;
            if (!chatOpen && age > 8500L) continue;

            float slide = age < 320L ? CreamTheme.easeOutExpo(age / 320.0f) : 1.0f;
            float fade = chatOpen ? 1.0f : (age > 7500L ? Math.max(0.0f, 1.0f - (age - 7500L) / 1000.0f) : 1.0f);
            if (fade <= 0.02f) continue;

            int lineH = 14;
            curY -= lineH;
            int drawX = (int) (x - (1.0f - slide) * 28.0f);
            int bg = CreamTheme.withAlpha(panelBg, (int) (200 * fade));
            int border = CreamTheme.withAlpha(accent, (int) (130 * fade));
            int txtCol = CreamTheme.withAlpha(CreamTheme.TEXT_PRIMARY, (int) (255 * fade));

            CreamTheme.drawRoundedRect(ms, drawX, curY, chatW, lineH - 1, 3.0f, bg);
            CreamTheme.drawRoundedRect(ms, drawX, curY + 1, 2.0f, lineH - 3, 1.0f, border);

            String text = entry.text;
            while (CreamTheme.getTextWidth(text) > chatW - 12 && text.length() > 4) {
                text = text.substring(0, text.length() - 1);
            }
            CreamTheme.drawText(ms, text, drawX + 6, curY + 3, txtCol);
            shown++;
        }
    }

    private static class ChatEntry {
        final String text;
        final long createdAt = System.currentTimeMillis();
        ChatEntry(String text) { this.text = text; }
    }
}
