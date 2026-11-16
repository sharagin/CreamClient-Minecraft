package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.media.MediaManager;
import com.creamclient.module.Category;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.module.Setting;
import com.creamclient.theme.CreamTheme;
import com.creamclient.theme.CustomColorManager;
import com.creamclient.theme.Lang;
import com.creamclient.visual.WaypointManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final String[] TABS_EN = {
        "Visuals", "Cosmetics (54)", "Sky & World", "Hand & Camera", "Waypoints & Radar",
        "Music Player", "HUD Constructor", "Color Studio", "Resource Packs", "Linux Config", "Profiles"
    };
    private static final String[] TABS_RU = {
        "Визуалы", "Косметика (54)", "Небо и Мир", "Рука и Камера", "Метки и Радар",
        "Муз. Плеер", "Конструктор HUD", "Свои Цвета", "Ресурс-паки", "Настройки Linux", "Профили"
    };

    private int activeTab = 0;
    private float scrollY = 0f;
    private float targetScrollY = 0f;
    private float maxScrollY = 600f;
    private float openAnim = 0f;
    private float tabIndicatorY = 0f;
    private List<com.creamclient.media.ResourcePackManager.PackEntry> cachedPacks = null;
    private Setting.SliderSetting draggingSlider = null;
    private int sliderX, sliderW;
    private String profileInput = "my_setup";
    private boolean typingProfile = false;
    private String searchQuery = "";
    private boolean typingSearch = false;
    private String colorNameInput = "MyLime";
    private boolean typingColorName = false;
    private String hexInput = "#A3FF12";
    private boolean typingHex = false;
    private final Screen parent;
    private Module bindingModule = null;

    public ClickGuiScreen() { this(null); }

    public ClickGuiScreen(Screen parent) {
        super(new StringTextComponent("CreamClient Control Panel"));
        this.parent = parent;
    }

    private boolean isRu() {
        return ModuleManager.LANG_MODE == null || "RU".equals(ModuleManager.LANG_MODE.getMode());
    }

    @Override
    protected void init() {
        super.init();
        openAnim = 0.05f;
        hexInput = CustomColorManager.currentHex();
    }

    @Override
    public void onClose() {
        if (minecraft != null && parent != null) minecraft.setScreen(parent);
        else super.onClose();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        CreamTheme.updateDelta();
        openAnim = CreamTheme.smooth(openAnim, 1.0f, 12.0f);
        scrollY = CreamTheme.smooth(scrollY, targetScrollY, 6.5f);

        int accent = CreamTheme.getAccent();
        float ease = CreamTheme.easeOutCubic(openAnim);
        fill(ms, 0, 0, width, height, CreamTheme.withAlpha(0x060806, (int) (145 * ease)));

        int pw = Math.min(620, width - 20);
        int ph = Math.min(370, height - 20);
        int px = (width - pw) / 2;
        int py = (height - ph) / 2;

        ms.pushPose();
        ms.translate(px + pw / 2f, py + ph / 2f, 0);
        float sc = 0.94f + 0.06f * ease;
        ms.scale(sc, sc, 1.0f);
        ms.translate(-(px + pw / 2f), -(py + ph / 2f), 0);

        float r = Math.max(6f, CreamTheme.getCornerRadius() + 2f);
        int panelBg = CreamTheme.withAlpha(0x0D120D, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 245));
        int sideBg = CreamTheme.withAlpha(0x090D09, 245);

        CreamTheme.drawRoundedRect(ms, px, py, pw, ph, r, panelBg);
        CreamTheme.drawRoundedRect(ms, px, py, 152, ph, r, sideBg);
        CreamTheme.drawRoundedOutline(ms, px, py, pw, ph, r, CreamTheme.withAlpha(accent, (int) ((ModuleManager.BORDER_ALPHA != null ? ModuleManager.BORDER_ALPHA.getFloat() : 0.7f) * 200)));

        if (minecraft != null) {
            minecraft.getTextureManager().bind(CreamTheme.LOGO_TEX);
            RenderSystem.enableBlend();
            AbstractGui.blit(ms, px + 12, py + 12, 0, 0, 22, 22, 22, 22);
        }
        CreamTheme.drawText(ms, "CreamClient", px + 40, py + 14, 0xFFF2F7EE);
        int activeCount = 0;
        for (Module m : ModuleManager.getModules()) if (m.isEnabled()) activeCount++;
        CreamTheme.drawText(ms, (isRu() ? "Активно: " : "Active: ") + activeCount, px + 40, py + 25, accent);

        int sx = px + 10, sy = py + 44, sw = 132, sh = 18;
        CreamTheme.drawRoundedRect(ms, sx, sy, sw, sh, 4f, typingSearch ? 0xFF1A241A : 0xFF121812);
        CreamTheme.drawRoundedOutline(ms, sx, sy, sw, sh, 4f, typingSearch ? accent : 0xFF283628);
        String phText = isRu() ? "Поиск модуля..." : "Search...";
        String sLabel = searchQuery.isEmpty() ? (typingSearch ? "_" : phText) : searchQuery + (typingSearch ? "_" : "");
        CreamTheme.drawText(ms, sLabel, sx + 6, sy + 5, searchQuery.isEmpty() ? 0xFF7D9278 : 0xFFF2F7EE);

        String[] tabs = isRu() ? TABS_RU : TABS_EN;
        float targetTabY = py + 66 + activeTab * 22f;
        if (tabIndicatorY <= 1f) tabIndicatorY = targetTabY;
        tabIndicatorY = CreamTheme.smooth(tabIndicatorY, targetTabY, 12.0f);
        CreamTheme.drawRoundedRect(ms, px + 10, tabIndicatorY, 132, 19, 5f, CreamTheme.withAlpha(accent, 48));
        AbstractGui.fill(ms, px + 10, (int) tabIndicatorY + 4, px + 12, (int) tabIndicatorY + 15, accent);
        for (int i = 0; i < tabs.length; i++) {
            int tx = px + 10, ty = py + 66 + i * 22, tw = 132, th = 19;
            boolean sel = (i == activeTab);
            boolean hov = mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th;
            if (!sel && hov) {
                CreamTheme.drawRoundedRect(ms, tx, ty, tw, th, 5f, 0x22FFFFFF);
            }
            CreamTheme.drawText(ms, tabs[i], tx + 10, ty + 6, sel ? accent : (hov ? 0xFFF2F7EE : 0xFF93A88D));
        }

        int cx = px + 164, cy = py + 12, cw = pw - 176, ch = ph - 24;
        CreamTheme.drawText(ms, tabs[activeTab], cx + 4, cy + 3, accent);
        String rpcBadge = (ModuleManager.DISCORD_RPC != null && ModuleManager.DISCORD_RPC.isEnabled()) ? "Discord: ON" : "Discord: OFF";
        drawButton(ms, cx + cw - 142, cy, 82, 16, rpcBadge, mouseX, mouseY, accent, false);
        drawButton(ms, cx + cw - 54, cy, 52, 16, isRu() ? "RU / EN" : "EN / RU", mouseX, mouseY, accent, false);
        AbstractGui.fill(ms, cx, cy + 19, cx + cw, cy + 20, 0x22FFFFFF);

        int bodyY = cy + 26, bodyH = ch - 26;
        if (minecraft != null) {
            double guiScale = minecraft.getWindow().getGuiScale();
            int sxSc = (int) (cx * guiScale);
            int sySc = (int) ((height - (bodyY + bodyH)) * guiScale);
            int swSc = (int) (cw * guiScale);
            int shSc = (int) (bodyH * guiScale);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(Math.max(0, sxSc), Math.max(0, sySc), Math.max(1, swSc), Math.max(1, shSc));
        }
        renderTabContent(ms, cx, bodyY, cw, bodyH, mouseX, mouseY, accent);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        ms.popPose();

        if (draggingSlider != null) {
            double pct = Math.max(0.0, Math.min(1.0, (double) (mouseX - sliderX) / (double) sliderW));
            draggingSlider.setValue(draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin()));
        }

        if (ModuleManager.CUSTOM_CURSOR != null && ModuleManager.CUSTOM_CURSOR.isEnabled()) {
            CreamTheme.drawCustomCursor(ms, mouseX, mouseY);
        }
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    private List<Module> getTabModules(int tab) {
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.getModules()) {
            String n = m.getName();
            if (!searchQuery.isEmpty()) {
                String q = searchQuery.toLowerCase();
                if (!n.toLowerCase().contains(q) && !Lang.trModule(n, isRu()).toLowerCase().contains(q)) continue;
            }
            if (tab == 0) {
                if (m.getCategory() == Category.VISUALS && !n.equals("CustomSky") && !n.equals("CustomFog") && !n.equals("TimeChanger") && !n.equals("NoWeather") && !n.equals("Fullbright") && !n.equals("HandVisuals") && !n.equals("Zoom") && !n.equals("NoHurtCam") && !n.equals("Waypoints")) {
                    list.add(m);
                }
            } else if (tab == 2) {
                if (n.equals("CustomSky") || n.equals("CustomFog") || n.equals("TimeChanger") || n.equals("NoWeather") || n.equals("Fullbright") || n.equals("BlockOverlay") || n.equals("Background")) {
                    list.add(m);
                }
            } else if (tab == 3) {
                if (n.equals("HandVisuals") || n.equals("SmoothSwing") || n.equals("Zoom") || n.equals("NoHurtCam") || n.equals("NoFireOverlay")) {
                    list.add(m);
                }
            } else if (tab == 4) {
                if (n.equals("Waypoints") || n.equals("NearbyPlayers") || n.equals("RadarHUD") || n.equals("CompassHUD")) {
                    list.add(m);
                }
            } else if (tab == 6) {
                if ((m.getCategory() == Category.HUD && !n.equals("NearbyPlayers")) || n.equals("CustomChat")) {
                    list.add(m);
                }
            } else if (tab == 9) {
                if (m.getCategory() == Category.INTERFACE || m.getCategory() == Category.MOVEMENT) {
                    list.add(m);
                }
            }
        }
        return list;
    }

    private void renderTabContent(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        if (activeTab == 1) { renderCosmeticsTab(ms, x, y, w, h, mx, my, accent); return; }
        if (activeTab == 5) { renderMusicTab(ms, x, y, w, h, mx, my, accent); return; }
        if (activeTab == 7) { renderColorStudioTab(ms, x, y, w, h, mx, my, accent); return; }
        if (activeTab == 8) { renderResourcePacksTab(ms, x, y, w, h, mx, my, accent); return; }
        if (activeTab == 10) { renderProfilesTab(ms, x, y, w, h, mx, my, accent); return; }

        float curY = y - scrollY;
        if (activeTab == 4) {
            if (curY >= y - 30 && curY <= y + h) {
                drawButton(ms, x, (int) curY, 175, 22, isRu() ? "+ Добавить метку [M]" : "+ Add Waypoint [M]", mx, my, accent, true);
                drawButton(ms, x + 182, (int) curY, 118, 22, isRu() ? "Удалить все" : "Clear All", mx, my, 0xFFFF6B6B, false);
            }
            curY += 28;
            List<WaypointManager.Waypoint> wps = WaypointManager.getWaypoints();
            for (int i = 0; i < wps.size(); i++) {
                WaypointManager.Waypoint wp = wps.get(i);
                if (curY >= y - 24 && curY <= y + h) {
                    CreamTheme.drawRoundedRect(ms, x, curY, w, 22, 4f, 0xAA141A14);
                    CreamTheme.drawText(ms, wp.name + "  (" + (int) wp.x + ", " + (int) wp.y + ", " + (int) wp.z + ")", x + 8, curY + 7, wp.enabled ? wp.color : 0xFF666666);
                    drawButton(ms, x + w - 94, (int) curY + 2, 44, 18, wp.enabled ? "ON" : "OFF", mx, my, wp.enabled ? accent : 0xFF777777, false);
                    drawButton(ms, x + w - 46, (int) curY + 2, 42, 18, isRu() ? "Удал" : "Del", mx, my, 0xFFFF6B6B, false);
                }
                curY += 26;
            }
        }

        if (activeTab == 6) {
            if (curY >= y - 30 && curY <= y + h) {
                drawButton(ms, x, (int) curY, 210, 22, isRu() ? "Открыть Конструктор HUD" : "Open HUD Constructor", mx, my, accent, true);
                drawButton(ms, x + 218, (int) curY, 120, 22, isRu() ? "Сбросить макет" : "Reset Layout", mx, my, 0xFF93A88D, false);
            }
            curY += 28;
            // Exact X / Y widget position controls right inside HUD tab
            for (HudLayoutManager.WidgetRect wr : HudLayoutManager.all().values()) {
                if (curY >= y - 24 && curY <= y + h) {
                    CreamTheme.drawRoundedRect(ms, x, curY, w, 22, 4f, 0x99131913);
                    String wName = isRu() ? wr.labelRu : wr.labelEn;
                    CreamTheme.drawText(ms, wName + "  (X:" + (int) wr.x + " Y:" + (int) wr.y + ")", x + 8, curY + 7, wr.visible ? 0xFFF2F7EE : 0xFF777777);
                    drawButton(ms, x + w - 168, (int) curY + 3, 24, 16, "X-", mx, my, accent, false);
                    drawButton(ms, x + w - 142, (int) curY + 3, 24, 16, "X+", mx, my, accent, false);
                    drawButton(ms, x + w - 114, (int) curY + 3, 24, 16, "Y-", mx, my, accent, false);
                    drawButton(ms, x + w - 88, (int) curY + 3, 24, 16, "Y+", mx, my, accent, false);
                    drawButton(ms, x + w - 58, (int) curY + 3, 52, 16, wr.visible ? "ВКЛ" : "ВЫКЛ", mx, my, wr.visible ? accent : 0xFF777777, false);
                }
                curY += 25;
            }
            curY += 6;
        }

        List<Module> mods = getTabModules(activeTab);
        for (Module m : mods) {
            int cardH = 30 + m.getSettings().size() * 22 + 6;
            if (curY + cardH >= y && curY <= y + h) {
                CreamTheme.drawRoundedRect(ms, x, curY, w, cardH, 6f, 0xCC131913);
                CreamTheme.drawRoundedOutline(ms, x, curY, w, cardH, 6f, m.isEnabled() ? CreamTheme.withAlpha(accent, 110) : 0x33FFFFFF);
                CreamTheme.drawText(ms, Lang.trModule(m.getName(), isRu()), x + 10, curY + 7, m.isEnabled() ? 0xFFF2F7EE : 0xFF93A88D);
                CreamTheme.drawText(ms, Lang.trDesc(m.getName(), m.getDescription(), isRu()), x + 10, curY + 18, 0xFF7C9277);

                String bindTxt = (bindingModule == m) ? (isRu() ? "[Жми...]" : "[Key...]") : ("[" + m.getKeyName() + "]");
                drawButton(ms, x + w - 92, (int) curY + 6, 48, 15, bindTxt, mx, my, bindingModule == m ? 0xFFFACC15 : 0xFF93A88D, false);
                int tx = x + w - 38, ty = (int) curY + 7;
                CreamTheme.drawRoundedRect(ms, tx, ty, 28, 13, 6.5f, m.isEnabled() ? CreamTheme.withAlpha(accent, 200) : 0xFF252E25);
                CreamTheme.drawRoundedRect(ms, m.isEnabled() ? tx + 16 : tx + 2, ty + 2, 9, 9, 4.5f, 0xFFF2F7EE);

                float sy = curY + 33;
                for (Setting s : m.getSettings()) {
                    String sLabel = Lang.trSetting(s.getName(), isRu());
                    if (s instanceof Setting.BoolSetting) {
                        Setting.BoolSetting bs = (Setting.BoolSetting) s;
                        CreamTheme.drawText(ms, sLabel, x + 14, sy + 3, 0xFFD0DDD0);
                        drawButton(ms, x + w - 52, (int) sy, 42, 16, bs.isEnabled() ? (isRu() ? "ВКЛ" : "ON") : (isRu() ? "ВЫКЛ" : "OFF"), mx, my, bs.isEnabled() ? accent : 0xFF777777, false);
                    } else if (s instanceof Setting.ModeSetting) {
                        Setting.ModeSetting msSet = (Setting.ModeSetting) s;
                        CreamTheme.drawText(ms, sLabel, x + 14, sy + 3, 0xFFD0DDD0);
                        drawButton(ms, x + w - 126, (int) sy, 116, 16, msSet.getMode(), mx, my, accent, false);
                    } else if (s instanceof Setting.SliderSetting) {
                        Setting.SliderSetting sl = (Setting.SliderSetting) s;
                        CreamTheme.drawText(ms, sLabel + ": " + String.format("%.2f", sl.getValue()), x + 14, sy + 3, 0xFFD0DDD0);
                        drawButton(ms, x + w - 178, (int) sy, 16, 15, "-", mx, my, accent, false);
                        int slx = x + w - 158, slw = 128;
                        CreamTheme.drawRoundedRect(ms, slx, sy + 5, slw, 5, 2.5f, 0xFF232C23);
                        float pct = (float) ((sl.getValue() - sl.getMin()) / (sl.getMax() - sl.getMin()));
                        CreamTheme.drawRoundedRect(ms, slx, sy + 5, slw * pct, 5, 2.5f, accent);
                        CreamTheme.drawRoundedRect(ms, slx + slw * pct - 3, sy + 3, 6, 9, 3f, 0xFFF2F7EE);
                        drawButton(ms, x + w - 26, (int) sy, 16, 15, "+", mx, my, accent, false);
                    }
                    sy += 22;
                }
            }
            curY += cardH + 6;
        }
        float totalH = (curY + scrollY) - y;
        maxScrollY = Math.max(0f, totalH - h + 20f);
    }

    private void renderColorStudioTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        CreamTheme.drawRoundedRect(ms, x, y, w, 92, 6f, 0xCC131913);
        CreamTheme.drawRoundedOutline(ms, x, y, w, 92, 6f, CreamTheme.withAlpha(accent, 140));

        // Live preview swatch
        int curCol = CustomColorManager.currentHex().equals("#000000") ? accent : CreamTheme.getBasePalette();
        CreamTheme.drawRoundedRect(ms, x + 10, y + 10, 34, 34, 6f, curCol);
        CreamTheme.drawRoundedOutline(ms, x + 10, y + 10, 34, 34, 6f, 0xFFFFFFFF);
        CreamTheme.drawText(ms, (isRu() ? "Текущий цвет: " : "Current Color: ") + CustomColorManager.currentHex(), x + 52, y + 12, 0xFFF2F7EE);

        // HEX input
        CreamTheme.drawRoundedRect(ms, x + 52, y + 26, 86, 18, 4f, typingHex ? 0xFF1A241A : 0xFF0E130E);
        CreamTheme.drawRoundedOutline(ms, x + 52, y + 26, 86, 18, 4f, typingHex ? accent : 0xFF283628);
        CreamTheme.drawText(ms, hexInput + (typingHex ? "_" : ""), x + 58, y + 31, 0xFFF2F7EE);
        drawButton(ms, x + 144, y + 26, 82, 18, isRu() ? "Применить" : "Apply HEX", mx, my, accent, false);

        // Save new unique color name
        CreamTheme.drawText(ms, isRu() ? "Название своего цвета:" : "Custom Color Name:", x + 10, y + 52, 0xFF93A88D);
        CreamTheme.drawRoundedRect(ms, x + 10, y + 64, 150, 18, 4f, typingColorName ? 0xFF1A241A : 0xFF0E130E);
        CreamTheme.drawRoundedOutline(ms, x + 10, y + 64, 150, 18, 4f, typingColorName ? accent : 0xFF283628);
        CreamTheme.drawText(ms, colorNameInput + (typingColorName ? "_" : ""), x + 16, y + 69, 0xFFF2F7EE);
        drawButton(ms, x + 166, y + 64, 130, 18, isRu() ? "+ Сохранить цвет" : "+ Save Unique Color", mx, my, accent, true);

        // Saved custom colors list
        List<CustomColorManager.SavedColor> cols = CustomColorManager.getColors();
        CreamTheme.drawText(ms, (isRu() ? "Твои сохранённые цвета (" : "Your Saved Colors (") + cols.size() + "):", x + 4, y + 100, accent);
        float cy = y + 114 - scrollY;
        for (int i = 0; i < cols.size(); i++) {
            CustomColorManager.SavedColor sc = cols.get(i);
            if (cy >= y + 106 && cy <= y + h - 20) {
                CreamTheme.drawRoundedRect(ms, x, cy, w, 24, 4f, 0xAA141A14);
                CreamTheme.drawRoundedRect(ms, x + 8, cy + 5, 14, 14, 3f, sc.toArgb());
                CreamTheme.drawText(ms, sc.name + "  " + sc.toHex(), x + 28, cy + 8, 0xFFF2F7EE);
                drawButton(ms, x + w - 122, (int) cy + 3, 66, 18, isRu() ? "Выбрать" : "Use", mx, my, accent, false);
                drawButton(ms, x + w - 50, (int) cy + 3, 44, 18, isRu() ? "Удал" : "Del", mx, my, 0xFFFF6B6B, false);
            }
            cy += 28;
        }
        maxScrollY = Math.max(0f, (cols.size() * 28f) - (h - 120f));
    }



    private void renderCosmeticsTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        drawButton(ms, x, y, 130, 20, isRu() ? "Снять всю косметику" : "Clear All Cosmetics", mx, my, 0xFFFF6B6B, false);
        List<com.creamclient.visual.CosmeticManager.CosmeticItem> items = com.creamclient.visual.CosmeticManager.getItems();
        int activeCos = 0;
        for (com.creamclient.visual.CosmeticManager.CosmeticItem ci : items) if (ci.enabled) activeCos++;
        CreamTheme.drawText(ms, (isRu() ? "Доступно: 54 | Вкл: " : "Total: 54 | Active: ") + activeCos, x + 140, y + 6, accent);

        float cyc = y + 28 - scrollY;
        int shown = 0;
        for (com.creamclient.visual.CosmeticManager.CosmeticItem ci : items) {
            if (!searchQuery.isEmpty() && !ci.nameRu.toLowerCase().contains(searchQuery.toLowerCase())) continue;
            shown++;
            if (cyc >= y + 22 && cyc <= y + h - 20) {
                CreamTheme.drawRoundedRect(ms, x, cyc, w, 22, 4f, ci.enabled ? CreamTheme.withAlpha(ci.color, 40) : 0xAA141A14);
                CreamTheme.drawRoundedRect(ms, x + 6, cyc + 5, 10, 12, 2f, ci.color);
                CreamTheme.drawText(ms, "[" + ci.categoryRu + "] " + ci.nameRu, x + 22, cyc + 7, ci.enabled ? 0xFFF2F7EE : 0xFF93A88D);
                drawButton(ms, x + w - 78, (int) cyc + 2, 72, 18, ci.enabled ? (isRu() ? "Надето" : "ON") : (isRu() ? "Надеть" : "Equip"), mx, my, ci.enabled ? ci.color : accent, false);
            }
            cyc += 24;
        }
        maxScrollY = Math.max(0f, (shown * 24f) - (h - 36f));
    }

    private void renderResourcePacksTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        if (cachedPacks == null) cachedPacks = com.creamclient.media.ResourcePackManager.listPacks();
        drawButton(ms, x, y, 125, 22, isRu() ? "Применить паки" : "Apply Packs", mx, my, accent, true);
        drawButton(ms, x + 132, y, 125, 22, isRu() ? "Открыть папку" : "Open Folder", mx, my, 0xFF93A88D, false);
        drawButton(ms, x + 264, y, 90, 22, isRu() ? "Обновить" : "Refresh", mx, my, accent, false);

        float ryp = y + 30 - scrollY;
        if (cachedPacks.isEmpty()) {
            CreamTheme.drawText(ms, isRu() ? "Закинь .zip ресурс-пак в папку resourcepacks и нажми Обновить" : "Drop .zip packs in resourcepacks/ and click Refresh", x + 6, y + 40, 0xFF93A88D);
        }
        for (com.creamclient.media.ResourcePackManager.PackEntry pe : cachedPacks) {
            if (ryp >= y + 26 && ryp <= y + h - 20) {
                CreamTheme.drawRoundedRect(ms, x, ryp, w, 22, 4f, pe.enabled ? CreamTheme.withAlpha(accent, 35) : 0xAA141A14);
                CreamTheme.drawRoundedOutline(ms, x, ryp, w, 22, 4f, pe.enabled ? accent : 0x33FFFFFF);
                String t = pe.title.length() > 32 ? pe.title.substring(0, 30) + ".." : pe.title;
                CreamTheme.drawText(ms, t, x + 10, ryp + 7, pe.enabled ? 0xFFF2F7EE : 0xFF93A88D);
                drawButton(ms, x + w - 86, (int) ryp + 2, 80, 18, pe.enabled ? (isRu() ? "Выключить" : "Disable") : (isRu() ? "Включить" : "Enable"), mx, my, pe.enabled ? 0xFFFF6B6B : accent, false);
            }
            ryp += 26;
        }
        maxScrollY = Math.max(0f, (cachedPacks.size() * 26f) - (h - 40f));
    }

    private void renderMusicTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        CreamTheme.drawRoundedRect(ms, x, y, w, 82, 6f, 0xCC131913);
        CreamTheme.drawRoundedOutline(ms, x, y, w, 82, 6f, CreamTheme.withAlpha(accent, 130));
        CreamTheme.drawText(ms, (isRu() ? "Сейчас играет: " : "Now Playing: ") + MediaManager.getCurrentTrackName(), x + 12, y + 9, 0xFFF2F7EE);
        CreamTheme.drawText(ms, MediaManager.getPositionFormatted() + " / " + MediaManager.getDurationFormatted() + (isRu() ? "  (клик по полосе — перемотка)" : "  (click bar to seek)"), x + 12, y + 21, accent);

        float prog = MediaManager.getProgress();
        CreamTheme.drawRoundedRect(ms, x + 12, y + 34, w - 24, 7, 3.5f, 0x33FFFFFF);
        if (prog > 0.005f) CreamTheme.drawRoundedRect(ms, x + 12, y + 34, (w - 24) * prog, 7, 3.5f, accent);

        drawButton(ms, x + 12, y + 50, 44, 20, "Prev", mx, my, accent, false);
        drawButton(ms, x + 60, y + 50, 40, 20, "-10s", mx, my, accent, false);
        drawButton(ms, x + 104, y + 50, 64, 20, MediaManager.isPlaying() ? (isRu() ? "Пауза" : "Pause") : (isRu() ? "Играть" : "Play"), mx, my, accent, true);
        drawButton(ms, x + 172, y + 50, 40, 20, "+10s", mx, my, accent, false);
        drawButton(ms, x + 216, y + 50, 44, 20, "Next", mx, my, accent, false);
        drawButton(ms, x + 264, y + 50, 44, 20, isRu() ? "Стоп" : "Stop", mx, my, 0xFFFF6B6B, false);
        drawButton(ms, x + w - 92, y + 50, 82, 20, isRu() ? "Папка .wav" : "Folder", mx, my, 0xFF93A88D, false);

        List<File> tracks = MediaManager.getTracks();
        CreamTheme.drawText(ms, (isRu() ? "Треки в creamclient/music (.wav): " : "Tracks in creamclient/music (.wav): ") + tracks.size(), x + 4, y + 90, 0xFF93A88D);
        float ty = y + 104 - scrollY;
        for (int i = 0; i < tracks.size(); i++) {
            if (ty >= y + 98 && ty <= y + h - 20) {
                boolean cur = (i == MediaManager.getCurrentIndex() && MediaManager.isPlaying());
                CreamTheme.drawRoundedRect(ms, x, ty, w, 20, 4f, cur ? CreamTheme.withAlpha(accent, 40) : 0x99141A14);
                CreamTheme.drawText(ms, (i + 1) + ". " + tracks.get(i).getName().replace(".wav", ""), x + 10, ty + 6, cur ? accent : 0xFFF2F7EE);
            }
            ty += 24;
        }
        maxScrollY = Math.max(0f, (tracks.size() * 24f) - (h - 110f));
    }

    private void renderProfilesTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        CreamTheme.drawRoundedRect(ms, x, y, w, 58, 6f, 0xCC131913);
        CreamTheme.drawRoundedOutline(ms, x, y, w, 58, 6f, CreamTheme.withAlpha(accent, 120));
        CreamTheme.drawText(ms, isRu() ? "Имя профиля конфига:" : "Config Profile Name:", x + 10, y + 8, 0xFF93A88D);
        CreamTheme.drawRoundedRect(ms, x + 10, y + 20, 180, 18, 4f, typingProfile ? 0xFF1A241A : 0xFF0E130E);
        CreamTheme.drawRoundedOutline(ms, x + 10, y + 20, 180, 18, 4f, typingProfile ? accent : 0xFF283628);
        CreamTheme.drawText(ms, profileInput + (typingProfile ? "_" : ""), x + 16, y + 25, 0xFFF2F7EE);

        drawButton(ms, x + 198, y + 20, 92, 18, isRu() ? "Сохранить" : "Save Profile", mx, my, accent, true);
        drawButton(ms, x + 296, y + 20, 96, 18, isRu() ? "Папка" : "Open Folder", mx, my, 0xFF93A88D, false);

        List<String> profiles = CreamConfig.listProfiles();
        CreamTheme.drawText(ms, (isRu() ? "Сохранённые профили (" : "Saved Profiles (") + profiles.size() + "):", x + 4, y + 66, accent);
        float py = y + 80 - scrollY;
        for (String name : profiles) {
            if (py >= y + 76 && py <= y + h - 20) {
                CreamTheme.drawRoundedRect(ms, x, py, w, 22, 4f, 0xAA141A14);
                CreamTheme.drawText(ms, name + ".json", x + 10, py + 7, 0xFFF2F7EE);
                drawButton(ms, x + w - 108, (int) py + 2, 54, 18, isRu() ? "Загр." : "Load", mx, my, accent, false);
                drawButton(ms, x + w - 50, (int) py + 2, 44, 18, isRu() ? "Удал" : "Del", mx, my, 0xFFFF6B6B, false);
            }
            py += 26;
        }
        maxScrollY = Math.max(0f, (profiles.size() * 26f) - (h - 90f));
    }

    private void drawButton(MatrixStack ms, int x, int y, int w, int h, String text, int mx, int my, int col, boolean filled) {
        boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int bg = filled ? (hov ? col : CreamTheme.withAlpha(col, 190)) : (hov ? 0x33FFFFFF : 0x22000000);
        CreamTheme.drawRoundedRect(ms, x, y, w, h, 4f, bg);
        if (!filled) CreamTheme.drawRoundedOutline(ms, x, y, w, h, 4f, CreamTheme.withAlpha(col, 150));
        CreamTheme.drawTextCentered(ms, text, x + w / 2f, y + (h - 8) / 2f, filled ? 0xFF0A0F0A : col);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int pw = Math.min(620, width - 20), ph = Math.min(370, height - 20);
        int px = (width - pw) / 2, py = (height - ph) / 2;

        int sx = px + 10, sy = py + 44, sw = 132, sh = 18;
        typingSearch = hit(mouseX, mouseY, sx, sy, sw, sh);
        typingProfile = false; typingColorName = false; typingHex = false;

        int cx = px + 164, cy = py + 12, cw = pw - 176;
        if (hit(mouseX, mouseY, cx + cw - 142, cy, 82, 16)) {
            if (ModuleManager.DISCORD_RPC != null) { ModuleManager.DISCORD_RPC.toggle(); CreamConfig.save(); }
            return true;
        }
        if (hit(mouseX, mouseY, cx + cw - 54, cy, 52, 16)) {
            if (ModuleManager.LANG_MODE != null) { ModuleManager.LANG_MODE.setMode(isRu() ? "EN" : "RU"); CreamConfig.save(); }
            return true;
        }

        for (int i = 0; i < TABS_EN.length; i++) {
            int tx = px + 10, ty = py + 66 + i * 22, tw = 132, th = 19;
            if (hit(mouseX, mouseY, tx, ty, tw, th)) {
                activeTab = i; targetScrollY = 0; scrollY = 0;
                return true;
            }
        }

        int bodyY = cy + 26;
        if (activeTab == 1) {
            if (hit(mouseX, mouseY, cx, bodyY, 130, 20)) { com.creamclient.visual.CosmeticManager.disableAll(); return true; }
            float cyc = bodyY + 28 - scrollY;
            for (com.creamclient.visual.CosmeticManager.CosmeticItem ci : com.creamclient.visual.CosmeticManager.getItems()) {
                if (!searchQuery.isEmpty() && !ci.nameRu.toLowerCase().contains(searchQuery.toLowerCase())) continue;
                if (hit(mouseX, mouseY, cx + cw - 78, cyc + 2, 72, 18)) {
                    ci.enabled = !ci.enabled;
                    com.creamclient.visual.CosmeticManager.save();
                    return true;
                }
                cyc += 24;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (activeTab == 5) {
            if (hit(mouseX, mouseY, cx + 12, bodyY + 32, cw - 24, 12)) {
                MediaManager.seekTo((float) ((mouseX - (cx + 12)) / (double) (cw - 24)));
                return true;
            }
            if (hit(mouseX, mouseY, cx + 12, bodyY + 50, 44, 20)) { MediaManager.prevTrack(); return true; }
            if (hit(mouseX, mouseY, cx + 60, bodyY + 50, 40, 20)) { MediaManager.seekRelativeSeconds(-10); return true; }
            if (hit(mouseX, mouseY, cx + 104, bodyY + 50, 64, 20)) { MediaManager.togglePause(); return true; }
            if (hit(mouseX, mouseY, cx + 172, bodyY + 50, 40, 20)) { MediaManager.seekRelativeSeconds(10); return true; }
            if (hit(mouseX, mouseY, cx + 216, bodyY + 50, 44, 20)) { MediaManager.nextTrack(); return true; }
            if (hit(mouseX, mouseY, cx + 264, bodyY + 50, 44, 20)) { MediaManager.stopMusic(); return true; }
            if (hit(mouseX, mouseY, cx + cw - 92, bodyY + 50, 82, 20)) { MediaManager.openMusicFolder(); return true; }
            List<File> tracks = MediaManager.getTracks();
            float ty = bodyY + 104 - scrollY;
            for (int i = 0; i < tracks.size(); i++) {
                if (hit(mouseX, mouseY, cx, ty, cw, 20)) { MediaManager.playTrack(i); return true; }
                ty += 24;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (activeTab == 7) {
            typingHex = hit(mouseX, mouseY, cx + 52, bodyY + 26, 86, 18);
            typingColorName = hit(mouseX, mouseY, cx + 10, bodyY + 64, 150, 18);
            if (hit(mouseX, mouseY, cx + 144, bodyY + 26, 82, 18)) {
                if (CustomColorManager.applyHex(hexInput)) {
                    CreamConfig.save();
                    NotificationManager.push("Color", "Applied " + hexInput, CreamTheme.getAccent());
                }
                return true;
            }
            if (hit(mouseX, mouseY, cx + 166, bodyY + 64, 130, 18)) {
                int rVal = ModuleManager.CUSTOM_R != null ? (int) ModuleManager.CUSTOM_R.getValue() : 163;
                int gVal = ModuleManager.CUSTOM_G != null ? (int) ModuleManager.CUSTOM_G.getValue() : 255;
                int bVal = ModuleManager.CUSTOM_B != null ? (int) ModuleManager.CUSTOM_B.getValue() : 18;
                CustomColorManager.addColor(colorNameInput, rVal, gVal, bVal);
                NotificationManager.push("Color", "Saved " + colorNameInput, CreamTheme.getAccent());
                return true;
            }
            List<CustomColorManager.SavedColor> cols = CustomColorManager.getColors();
            float cyList = bodyY + 114 - scrollY;
            for (int i = 0; i < cols.size(); i++) {
                if (hit(mouseX, mouseY, cx + cw - 122, cyList + 3, 66, 18)) {
                    CustomColorManager.applySaved(i);
                    hexInput = CustomColorManager.currentHex();
                    CreamConfig.save();
                    return true;
                }
                if (hit(mouseX, mouseY, cx + cw - 50, cyList + 3, 44, 18)) {
                    CustomColorManager.removeColor(i);
                    return true;
                }
                cyList += 28;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (activeTab == 8) {
            if (hit(mouseX, mouseY, cx, bodyY, 125, 22)) { com.creamclient.media.ResourcePackManager.applyPacks(); return true; }
            if (hit(mouseX, mouseY, cx + 132, bodyY, 125, 22)) { com.creamclient.media.ResourcePackManager.openFolder(); return true; }
            if (hit(mouseX, mouseY, cx + 264, bodyY, 90, 22)) { cachedPacks = com.creamclient.media.ResourcePackManager.listPacks(); return true; }
            if (cachedPacks != null) {
                float ryp = bodyY + 30 - scrollY;
                for (com.creamclient.media.ResourcePackManager.PackEntry pe : cachedPacks) {
                    if (hit(mouseX, mouseY, cx + cw - 86, ryp + 2, 80, 18)) {
                        com.creamclient.media.ResourcePackManager.togglePack(pe.id);
                        cachedPacks = com.creamclient.media.ResourcePackManager.listPacks();
                        return true;
                    }
                    ryp += 26;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (activeTab == 10) {
            typingProfile = hit(mouseX, mouseY, cx + 10, bodyY + 20, 180, 18);
            if (hit(mouseX, mouseY, cx + 198, bodyY + 20, 92, 18)) {
                CreamConfig.saveProfile(profileInput);
                NotificationManager.push("Config", "Saved: " + profileInput, CreamTheme.getAccent());
                return true;
            }
            if (hit(mouseX, mouseY, cx + 296, bodyY + 20, 96, 18)) { CreamConfig.openConfigsFolder(); return true; }
            List<String> profiles = CreamConfig.listProfiles();
            float pyList = bodyY + 80 - scrollY;
            for (String name : profiles) {
                if (hit(mouseX, mouseY, cx + cw - 108, pyList + 2, 54, 18)) {
                    CreamConfig.loadProfile(name);
                    NotificationManager.push("Config", "Loaded: " + name, CreamTheme.getAccent());
                    return true;
                }
                if (hit(mouseX, mouseY, cx + cw - 50, pyList + 2, 44, 18)) {
                    CreamConfig.deleteProfile(name);
                    return true;
                }
                pyList += 26;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        float curY = bodyY - scrollY;
        if (activeTab == 4) {
            if (hit(mouseX, mouseY, cx, curY, 175, 22)) { WaypointManager.addQuickWaypoint(); return true; }
            if (hit(mouseX, mouseY, cx + 182, curY, 118, 22)) { WaypointManager.clearAll(); return true; }
            curY += 28;
            List<WaypointManager.Waypoint> wps = WaypointManager.getWaypoints();
            for (int i = 0; i < wps.size(); i++) {
                if (hit(mouseX, mouseY, cx + cw - 94, curY + 2, 44, 18)) { WaypointManager.toggleWaypoint(i); return true; }
                if (hit(mouseX, mouseY, cx + cw - 46, curY + 2, 42, 18)) { WaypointManager.removeWaypoint(i); return true; }
                curY += 26;
            }
        }
        if (activeTab == 6) {
            if (hit(mouseX, mouseY, cx, curY, 210, 22)) { minecraft.setScreen(new HudEditorScreen(this)); return true; }
            if (hit(mouseX, mouseY, cx + 218, curY, 120, 22)) { HudLayoutManager.resetDefaults(); CreamConfig.save(); return true; }
            curY += 28;
            for (HudLayoutManager.WidgetRect wr : HudLayoutManager.all().values()) {
                if (hit(mouseX, mouseY, cx + cw - 168, curY + 3, 24, 16)) { wr.x = Math.max(0, wr.x - 10); CreamConfig.save(); return true; }
                if (hit(mouseX, mouseY, cx + cw - 142, curY + 3, 24, 16)) { wr.x += 10; CreamConfig.save(); return true; }
                if (hit(mouseX, mouseY, cx + cw - 114, curY + 3, 24, 16)) { wr.y = Math.max(0, wr.y - 10); CreamConfig.save(); return true; }
                if (hit(mouseX, mouseY, cx + cw - 88, curY + 3, 24, 16)) { wr.y += 10; CreamConfig.save(); return true; }
                if (hit(mouseX, mouseY, cx + cw - 58, curY + 3, 52, 16)) { wr.visible = !wr.visible; CreamConfig.save(); return true; }
                curY += 25;
            }
            curY += 6;
        }

        int bodyH = ph - 50;
        if (mouseY < bodyY || mouseY > bodyY + bodyH) return super.mouseClicked(mouseX, mouseY, button);
        for (Module m : getTabModules(activeTab)) {
            int cardH = 30 + m.getSettings().size() * 22 + 6;
            if (hit(mouseX, mouseY, cx + cw - 92, curY + 6, 48, 15)) {
                bindingModule = (bindingModule == m) ? null : m;
                return true;
            }
            if (hit(mouseX, mouseY, cx, curY, cw, 28)) {
                m.toggle(); CreamConfig.save(); return true;
            }
            float setY = curY + 33;
            for (Setting s : m.getSettings()) {
                if (s instanceof Setting.BoolSetting && hit(mouseX, mouseY, cx + cw - 52, setY, 42, 16)) {
                    ((Setting.BoolSetting) s).toggle(); CreamConfig.save(); return true;
                } else if (s instanceof Setting.ModeSetting && hit(mouseX, mouseY, cx + cw - 126, setY, 116, 16)) {
                    ((Setting.ModeSetting) s).cycle(); CreamConfig.save(); return true;
                } else if (s instanceof Setting.SliderSetting) {
                    Setting.SliderSetting sl = (Setting.SliderSetting) s;
                    if (hit(mouseX, mouseY, cx + cw - 178, setY, 16, 15)) { sl.setValue(sl.getValue() - sl.getStep()); CreamConfig.save(); return true; }
                    if (hit(mouseX, mouseY, cx + cw - 26, setY, 16, 15)) { sl.setValue(sl.getValue() + sl.getStep()); CreamConfig.save(); return true; }
                    if (hit(mouseX, mouseY, cx + cw - 158, setY, 128, 15)) { draggingSlider = sl; sliderX = cx + cw - 158; sliderW = 128; return true; }
                }
                setY += 22;
            }
            curY += cardH + 6;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean hit(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSlider != null) { draggingSlider = null; CreamConfig.save(); }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        targetScrollY = Math.max(0f, Math.min(maxScrollY, targetScrollY - (float) delta * 16f));
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (typingProfile && (Character.isLetterOrDigit(codePoint) || codePoint == '_' || codePoint == '-')) {
            if (profileInput.length() < 20) profileInput += codePoint;
            return true;
        }
        if (typingColorName && (Character.isLetterOrDigit(codePoint) || codePoint == '_' || codePoint == ' ')) {
            if (colorNameInput.length() < 18) colorNameInput += codePoint;
            return true;
        }
        if (typingHex && ("0123456789abcdefABCDEF#".indexOf(codePoint) >= 0)) {
            if (hexInput.length() < 7) hexInput += codePoint;
            return true;
        }
        if (typingSearch && !Character.isISOControl(codePoint)) {
            if (searchQuery.length() < 18) searchQuery += codePoint;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindingModule.setKey(-1);
            } else {
                bindingModule.setKey(keyCode);
            }
            bindingModule = null;
            CreamConfig.save();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (typingProfile && !profileInput.isEmpty()) { profileInput = profileInput.substring(0, profileInput.length() - 1); return true; }
            if (typingColorName && !colorNameInput.isEmpty()) { colorNameInput = colorNameInput.substring(0, colorNameInput.length() - 1); return true; }
            if (typingHex && !hexInput.isEmpty()) { hexInput = hexInput.substring(0, hexInput.length() - 1); return true; }
            if (typingSearch && !searchQuery.isEmpty()) { searchQuery = searchQuery.substring(0, searchQuery.length() - 1); return true; }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
