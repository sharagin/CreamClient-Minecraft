package com.creamclient.ui;

import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NotificationManager {
    private static final List<Notif> LIST = new ArrayList<>();

    private NotificationManager() {}

    public static void push(String title, boolean enabled) {
        if (ModuleManager.NOTIFICATIONS == null || !ModuleManager.NOTIFICATIONS.isEnabled()) return;
        LIST.add(new Notif(title, enabled));
        if (LIST.size() > 5) LIST.remove(0);
    }

    public static void render(MatrixStack ms, int screenWidth, int screenHeight) {
        if (LIST.isEmpty()) return;
        long now = System.currentTimeMillis();
        Iterator<Notif> it = LIST.iterator();
        while (it.hasNext()) {
            if (now - it.next().created > 2400L) it.remove();
        }

        int accent = CreamTheme.getAccent();
        int panelBg = CreamTheme.withAlpha(0x0E130E, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 255));
        int y = screenHeight - 36;

        for (int i = LIST.size() - 1; i >= 0; i--) {
            Notif n = LIST.get(i);
            long age = now - n.created;
            float anim = age < 300 ? CreamTheme.easeOutExpo(age / 300.0f)
                    : (age > 2000 ? 1.0f - ((age - 2000) / 400.0f) : 1.0f);
            anim = Math.max(0.0f, Math.min(1.0f, anim));

            String msg = n.title + (n.enabled ? "  [ON]" : "  [OFF]");
            int w = CreamTheme.getTextWidth(msg) + 18;
            int x = (int) (screenWidth - (w + 10) * anim);
            int col = n.enabled ? accent : 0xFFFF5555;

            CreamTheme.drawBorderedRect(ms, x, y, x + w, y + 18, panelBg, CreamTheme.BORDER);
            AbstractGui.fill(ms, x, y, x + 3, y + 18, col);
            CreamTheme.drawText(ms, msg, x + 7, y + 5, CreamTheme.TEXT_PRIMARY);
            y -= 22;
        }
    }

    private static class Notif {
        final String title;
        final boolean enabled;
        final long created = System.currentTimeMillis();
        Notif(String title, boolean enabled) { this.title = title; this.enabled = enabled; }
    }
}
