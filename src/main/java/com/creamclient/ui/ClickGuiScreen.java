package com.creamclient.ui;

import com.creamclient.config.CreamConfig;
import com.creamclient.media.MediaManager;
import com.creamclient.module.Category;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.creamclient.module.Setting;
import com.creamclient.theme.CreamTheme;
import com.creamclient.visual.WaypointManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final String[] TABS = {
        "Visuals",
        "Sky & World",
        "Hand & Camera",
        "Waypoints & Radar",
        "Music Player",
        "HUD & Layout",
        "Linux Config",
        "Profiles"
    };

    private int activeTab = 0;
    private float scrollY = 0f;
    private float targetScrollY = 0f;
    private float openAnim = 0f;
    private Setting.SliderSetting draggingSlider = null;
    private int sliderX, sliderW;
    private String profileInput = "my_setup";
    private boolean typingProfile = false;
    private String searchQuery = "";
    private boolean typingSearch = false;
    private final Screen parent;

    public ClickGuiScreen() {
        this(null);
    }

    public ClickGuiScreen(Screen parent) {
        super(new StringTextComponent("CreamClient Control Panel"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (minecraft != null && parent != null) minecraft.setScreen(parent);
        else super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        openAnim = 0.05f;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        CreamTheme.updateDelta();
        openAnim = CreamTheme.smooth(openAnim, 1.0f, 12.0f);
        scrollY = CreamTheme.smooth(scrollY, targetScrollY, 14.0f);

        int accent = CreamTheme.getAccent();
        float ease = CreamTheme.easeOutCubic(openAnim);
        int bgDim = CreamTheme.withAlpha(0x060806, (int) (140 * ease));
        fill(ms, 0, 0, width, height, bgDim);

        int pw = Math.min(580, width - 30);
        int ph = Math.min(350, height - 30);
        int px = (width - pw) / 2;
        int py = (height - ph) / 2;

        ms.pushPose();
        ms.translate(px + pw / 2f, py + ph / 2f, 0);
        float sc = 0.92f + 0.08f * ease;
        ms.scale(sc, sc, 1.0f);
        ms.translate(-(px + pw / 2f), -(py + ph / 2f), 0);

        float r = Math.max(6f, CreamTheme.getCornerRadius() + 2f);
        int panelBg = CreamTheme.withAlpha(0x0D120D, (int) (ModuleManager.PANEL_ALPHA.getFloat() * 245));
        int sideBg = CreamTheme.withAlpha(0x090D09, 245);

        CreamTheme.drawRoundedRect(ms, px, py, pw, ph, r, panelBg);
        CreamTheme.drawRoundedRect(ms, px, py, 144, ph, r, sideBg);
        CreamTheme.drawRoundedOutline(ms, px, py, pw, ph, r, CreamTheme.withAlpha(accent, (int) ((ModuleManager.BORDER_ALPHA != null ? ModuleManager.BORDER_ALPHA.getFloat() : 0.7f) * 200)));

        // Sidebar Header
        if (minecraft != null) {
            minecraft.getTextureManager().bind(CreamTheme.LOGO_TEX);
            RenderSystem.enableBlend();
            AbstractGui.blit(ms, px + 12, py + 12, 0, 0, 22, 22, 22, 22);
        }
        CreamTheme.drawText(ms, "CreamClient", px + 40, py + 14, 0xFFF2F7EE);
        CreamTheme.drawText(ms, "v1.0 Control Panel", px + 40, py + 25, accent);

        // Search box
        int sx = px + 10, sy = py + 44, sw = 124, sh = 18;
        CreamTheme.drawRoundedRect(ms, sx, sy, sw, sh, 4f, typingSearch ? 0xFF1A241A : 0xFF121812);
        CreamTheme.drawRoundedOutline(ms, sx, sy, sw, sh, 4f, typingSearch ? accent : 0xFF283628);
        String sLabel = searchQuery.isEmpty() ? (typingSearch ? "_" : "Search...") : searchQuery + (typingSearch ? "_" : "");
        CreamTheme.drawText(ms, sLabel, sx + 6, sy + 5, searchQuery.isEmpty() ? 0xFF7D9278 : 0xFFF2F7EE);

        // Sidebar Tabs
        for (int i = 0; i < TABS.length; i++) {
            int tx = px + 10, ty = py + 70 + i * 26, tw = 124, th = 22;
            boolean sel = (i == activeTab);
            boolean hov = mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th;
            if (sel) {
                CreamTheme.drawRoundedRect(ms, tx, ty, tw, th, 5f, CreamTheme.withAlpha(accent, 45));
                AbstractGui.fill(ms, tx, ty + 4, tx + 2, ty + th - 4, accent);
            } else if (hov) {
                CreamTheme.drawRoundedRect(ms, tx, ty, tw, th, 5f, 0x22FFFFFF);
            }
            CreamTheme.drawText(ms, TABS[i], tx + 10, ty + 7, sel ? accent : (hov ? 0xFFF2F7EE : 0xFF93A88D));
        }

        // Content Header
        int cx = px + 156, cy = py + 12, cw = pw - 168, ch = ph - 24;
        CreamTheme.drawText(ms, TABS[activeTab], cx + 4, cy + 2, accent);
        CreamTheme.drawText(ms, "Linux-grade modular settings", cx + cw - 140, cy + 2, 0xFF7D9278);
        AbstractGui.fill(ms, cx, cy + 16, cx + cw, cy + 17, 0x22FFFFFF);

        // Render active tab body
        int bodyY = cy + 24;
        int bodyH = ch - 24;
        renderTabContent(ms, cx, bodyY, cw, bodyH, mouseX, mouseY, accent);

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
            if (!searchQuery.isEmpty() && !n.toLowerCase().contains(searchQuery.toLowerCase())) continue;
            if (tab == 0) {
                if (m.getCategory() == Category.VISUALS && !n.equals("CustomSky") && !n.equals("CustomFog") && !n.equals("TimeChanger") && !n.equals("NoWeather") && !n.equals("Fullbright") && !n.equals("ViewModel") && !n.equals("Zoom") && !n.equals("NoHurtCam") && !n.equals("Waypoints")) {
                    list.add(m);
                }
            } else if (tab == 1) {
                if (n.equals("CustomSky") || n.equals("CustomFog") || n.equals("TimeChanger") || n.equals("NoWeather") || n.equals("Fullbright") || n.equals("BlockOverlay") || n.equals("Background")) {
                    list.add(m);
                }
            } else if (tab == 2) {
                if (n.equals("ViewModel") || n.equals("Zoom") || n.equals("NoHurtCam") || n.equals("NoFire")) {
                    list.add(m);
                }
            } else if (tab == 3) {
                if (n.equals("Waypoints") || n.equals("NearbyPlayers") || n.equals("RadarHUD") || n.equals("CompassHUD")) {
                    list.add(m);
                }
            } else if (tab == 5) {
                if ((m.getCategory() == Category.HUD && !n.equals("NearbyPlayers")) || n.equals("CustomChat")) {
                    list.add(m);
                }
            } else if (tab == 6) {
                if (m.getCategory() == Category.INTERFACE || m.getCategory() == Category.MOVEMENT) {
                    list.add(m);
                }
            }
        }
        return list;
    }

    private void renderTabContent(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        if (activeTab == 4) {
            renderMusicTab(ms, x, y, w, h, mx, my, accent);
            return;
        }
        if (activeTab == 7) {
            renderProfilesTab(ms, x, y, w, h, mx, my, accent);
            return;
        }

        float curY = y - scrollY;
        if (activeTab == 3) {
            if (curY >= y - 30 && curY <= y + h) {
                drawButton(ms, x, (int) curY, 150, 22, "+ Add Waypoint Here [M]", mx, my, accent, true);
                drawButton(ms, x + 158, (int) curY, 110, 22, "Clear All Points", mx, my, 0xFFFF6B6B, false);
            }
            curY += 28;
            List<WaypointManager.Waypoint> wps = WaypointManager.getWaypoints();
            for (int i = 0; i < wps.size(); i++) {
                WaypointManager.Waypoint wp = wps.get(i);
                if (curY >= y - 24 && curY <= y + h) {
                    CreamTheme.drawRoundedRect(ms, x, curY, w, 22, 4f, 0xAA141A14);
                    CreamTheme.drawText(ms, wp.name + "  (" + (int) wp.x + ", " + (int) wp.y + ", " + (int) wp.z + ")", x + 8, curY + 7, wp.enabled ? wp.color : 0xFF666666);
                    drawButton(ms, x + w - 94, (int) curY + 2, 44, 18, wp.enabled ? "ON" : "OFF", mx, my, wp.enabled ? accent : 0xFF777777, false);
                    drawButton(ms, x + w - 46, (int) curY + 2, 42, 18, "Del", mx, my, 0xFFFF6B6B, false);
                }
                curY += 26;
            }
        }

        if (activeTab == 5) {
            if (curY >= y - 30 && curY <= y + h) {
                drawButton(ms, x, (int) curY, 180, 22, "Open Interactive HUD Editor", mx, my, accent, true);
                drawButton(ms, x + 188, (int) curY, 120, 22, "Reset HUD Layout", mx, my, 0xFF93A88D, false);
            }
            curY += 28;
        }

        List<Module> mods = getTabModules(activeTab);
        for (Module m : mods) {
            int cardH = 26 + m.getSettings().size() * 20 + 6;
            if (curY + cardH >= y && curY <= y + h) {
                CreamTheme.drawRoundedRect(ms, x, curY, w, cardH, 6f, 0xCC131913);
                CreamTheme.drawRoundedOutline(ms, x, curY, w, cardH, 6f, m.isEnabled() ? CreamTheme.withAlpha(accent, 110) : 0x33FFFFFF);
                CreamTheme.drawText(ms, m.getName(), x + 10, curY + 7, m.isEnabled() ? 0xFFF2F7EE : 0xFF93A88D);
                CreamTheme.drawText(ms, m.getDescription(), x + 10, curY + 17, 0xFF6F826A);

                // Toggle pill
                int tx = x + w - 38, ty = (int) curY + 7;
                CreamTheme.drawRoundedRect(ms, tx, ty, 28, 13, 6.5f, m.isEnabled() ? CreamTheme.withAlpha(accent, 200) : 0xFF252E25);
                int knobX = m.isEnabled() ? tx + 16 : tx + 2;
                CreamTheme.drawRoundedRect(ms, knobX, ty + 2, 9, 9, 4.5f, 0xFFF2F7EE);

                float sy = curY + 30;
                for (Setting s : m.getSettings()) {
                    if (s instanceof Setting.BoolSetting) {
                        Setting.BoolSetting bs = (Setting.BoolSetting) s;
                        CreamTheme.drawText(ms, s.getName(), x + 14, sy + 3, 0xFFD0DDD0);
                        drawButton(ms, x + w - 48, (int) sy, 38, 15, bs.isEnabled() ? "ON" : "OFF", mx, my, bs.isEnabled() ? accent : 0xFF777777, false);
                    } else if (s instanceof Setting.ModeSetting) {
                        Setting.ModeSetting msSet = (Setting.ModeSetting) s;
                        CreamTheme.drawText(ms, s.getName(), x + 14, sy + 3, 0xFFD0DDD0);
                        drawButton(ms, x + w - 120, (int) sy, 110, 15, msSet.getMode(), mx, my, accent, false);
                    } else if (s instanceof Setting.SliderSetting) {
                        Setting.SliderSetting sl = (Setting.SliderSetting) s;
                        CreamTheme.drawText(ms, s.getName() + ": " + String.format("%.2f", sl.getValue()), x + 14, sy + 3, 0xFFD0DDD0);
                        int sx = x + w - 140, sw = 130;
                        CreamTheme.drawRoundedRect(ms, sx, sy + 5, sw, 5, 2.5f, 0xFF232C23);
                        float pct = (float) ((sl.getValue() - sl.getMin()) / (sl.getMax() - sl.getMin()));
                        CreamTheme.drawRoundedRect(ms, sx, sy + 5, sw * pct, 5, 2.5f, accent);
                        CreamTheme.drawRoundedRect(ms, sx + sw * pct - 3, sy + 3, 6, 9, 3f, 0xFFF2F7EE);
                    }
                    sy += 20;
                }
            }
            curY += cardH + 6;
        }
    }

    private void renderMusicTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        CreamTheme.drawRoundedRect(ms, x, y, w, 72, 6f, 0xCC131913);
        CreamTheme.drawRoundedOutline(ms, x, y, w, 72, 6f, CreamTheme.withAlpha(accent, 130));
        CreamTheme.drawText(ms, "Now Playing: " + MediaManager.getCurrentTrackName(), x + 12, y + 10, 0xFFF2F7EE);
        CreamTheme.drawText(ms, MediaManager.getPositionFormatted() + " / " + MediaManager.getDurationFormatted(), x + 12, y + 22, accent);

        float prog = MediaManager.getProgress();
        CreamTheme.drawRoundedRect(ms, x + 12, y + 36, w - 24, 4, 2f, 0x33FFFFFF);
        if (prog > 0.01f) CreamTheme.drawRoundedRect(ms, x + 12, y + 36, (w - 24) * prog, 4, 2f, accent);

        drawButton(ms, x + 12, y + 46, 56, 18, "Prev", mx, my, accent, false);
        drawButton(ms, x + 74, y + 46, 76, 18, MediaManager.isPlaying() ? "Pause" : "Play", mx, my, accent, true);
        drawButton(ms, x + 156, y + 46, 56, 18, "Next", mx, my, accent, false);
        drawButton(ms, x + 218, y + 46, 56, 18, "Stop", mx, my, 0xFFFF6B6B, false);
        drawButton(ms, x + w - 110, y + 46, 100, 18, "Open Folder", mx, my, 0xFF93A88D, false);

        List<File> tracks = MediaManager.getTracks();
        CreamTheme.drawText(ms, "Tracks in creamclient/music (.wav): " + tracks.size(), x + 4, y + 82, 0xFF93A88D);
        float ty = y + 96 - scrollY;
        for (int i = 0; i < tracks.size(); i++) {
            if (ty >= y + 90 && ty <= y + h - 20) {
                boolean cur = (i == MediaManager.getCurrentIndex() && MediaManager.isPlaying());
                CreamTheme.drawRoundedRect(ms, x, ty, w, 20, 4f, cur ? CreamTheme.withAlpha(accent, 40) : 0x99141A14);
                CreamTheme.drawText(ms, (i + 1) + ". " + tracks.get(i).getName().replace(".wav", ""), x + 10, ty + 6, cur ? accent : 0xFFF2F7EE);
            }
            ty += 24;
        }
    }

    private void renderProfilesTab(MatrixStack ms, int x, int y, int w, int h, int mx, int my, int accent) {
        CreamTheme.drawRoundedRect(ms, x, y, w, 58, 6f, 0xCC131913);
        CreamTheme.drawRoundedOutline(ms, x, y, w, 58, 6f, CreamTheme.withAlpha(accent, 120));
        CreamTheme.drawText(ms, "Config Profile Name:", x + 10, y + 8, 0xFF93A88D);
        CreamTheme.drawRoundedRect(ms, x + 10, y + 20, 180, 18, 4f, typingProfile ? 0xFF1A241A : 0xFF0E130E);
        CreamTheme.drawRoundedOutline(ms, x + 10, y + 20, 180, 18, 4f, typingProfile ? accent : 0xFF283628);
        CreamTheme.drawText(ms, profileInput + (typingProfile ? "_" : ""), x + 16, y + 25, 0xFFF2F7EE);

        drawButton(ms, x + 198, y + 20, 86, 18, "Save Profile", mx, my, accent, true);
        drawButton(ms, x + 290, y + 20, 96, 18, "Open Folder", mx, my, 0xFF93A88D, false);

        List<String> profiles = CreamConfig.listProfiles();
        CreamTheme.drawText(ms, "Saved Profiles (" + profiles.size() + "):", x + 4, y + 66, accent);
        float py = y + 80 - scrollY;
        for (String name : profiles) {
            if (py >= y + 76 && py <= y + h - 20) {
                CreamTheme.drawRoundedRect(ms, x, py, w, 22, 4f, 0xAA141A14);
                CreamTheme.drawText(ms, name + ".json", x + 10, py + 7, 0xFFF2F7EE);
                drawButton(ms, x + w - 100, (int) py + 2, 46, 18, "Load", mx, my, accent, false);
                drawButton(ms, x + w - 50, (int) py + 2, 44, 18, "Del", mx, my, 0xFFFF6B6B, false);
            }
            py += 26;
        }
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
        int pw = Math.min(580, width - 30), ph = Math.min(350, height - 30);
        int px = (width - pw) / 2, py = (height - ph) / 2;

        // Search box click
        int sx = px + 10, sy = py + 44, sw = 124, sh = 18;
        typingSearch = (mouseX >= sx && mouseX <= sx + sw && mouseY >= sy && mouseY <= sy + sh);

        // Sidebar tabs
        for (int i = 0; i < TABS.length; i++) {
            int tx = px + 10, ty = py + 70 + i * 26, tw = 124, th = 22;
            if (mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th) {
                activeTab = i;
                targetScrollY = 0;
                scrollY = 0;
                typingProfile = false;
                return true;
            }
        }

        int cx = px + 156, cy = py + 36, cw = pw - 168;
        if (activeTab == 4) {
            if (hit(mouseX, mouseY, cx + 12, cy + 46, 56, 18)) { MediaManager.prevTrack(); return true; }
            if (hit(mouseX, mouseY, cx + 74, cy + 46, 76, 18)) { MediaManager.togglePause(); return true; }
            if (hit(mouseX, mouseY, cx + 156, cy + 46, 56, 18)) { MediaManager.nextTrack(); return true; }
            if (hit(mouseX, mouseY, cx + 218, cy + 46, 56, 18)) { MediaManager.stopMusic(); return true; }
            if (hit(mouseX, mouseY, cx + cw - 110, cy + 46, 100, 18)) { MediaManager.openMusicFolder(); return true; }
            List<File> tracks = MediaManager.getTracks();
            float ty = cy + 96 - scrollY;
            for (int i = 0; i < tracks.size(); i++) {
                if (hit(mouseX, mouseY, cx, ty, cw, 20)) { MediaManager.playTrack(i); return true; }
                ty += 24;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (activeTab == 7) {
            typingProfile = hit(mouseX, mouseY, cx + 10, cy + 20, 180, 18);
            if (hit(mouseX, mouseY, cx + 198, cy + 20, 86, 18)) {
                CreamConfig.saveProfile(profileInput);
                NotificationManager.push("Config", "Saved profile: " + profileInput, CreamTheme.getAccent());
                return true;
            }
            if (hit(mouseX, mouseY, cx + 290, cy + 20, 96, 18)) { CreamConfig.openConfigsFolder(); return true; }
            List<String> profiles = CreamConfig.listProfiles();
            float pyList = cy + 80 - scrollY;
            for (String name : profiles) {
                if (hit(mouseX, mouseY, cx + cw - 100, pyList + 2, 46, 18)) {
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

        float curY = cy - scrollY;
        if (activeTab == 3) {
            if (hit(mouseX, mouseY, cx, curY, 150, 22)) { WaypointManager.addQuickWaypoint(); return true; }
            if (hit(mouseX, mouseY, cx + 158, curY, 110, 22)) { WaypointManager.clearAll(); return true; }
            curY += 28;
            List<WaypointManager.Waypoint> wps = WaypointManager.getWaypoints();
            for (int i = 0; i < wps.size(); i++) {
                if (hit(mouseX, mouseY, cx + cw - 94, curY + 2, 44, 18)) { WaypointManager.toggleWaypoint(i); return true; }
                if (hit(mouseX, mouseY, cx + cw - 46, curY + 2, 42, 18)) { WaypointManager.removeWaypoint(i); return true; }
                curY += 26;
            }
        }
        if (activeTab == 5) {
            if (hit(mouseX, mouseY, cx, curY, 180, 22)) { minecraft.setScreen(new HudEditorScreen(this)); return true; }
            if (hit(mouseX, mouseY, cx + 188, curY, 120, 22)) { HudLayoutManager.resetDefaults(); CreamConfig.save(); return true; }
            curY += 28;
        }

        for (Module m : getTabModules(activeTab)) {
            int cardH = 26 + m.getSettings().size() * 20 + 6;
            if (hit(mouseX, mouseY, cx, curY, cw, 26)) {
                m.toggle();
                CreamConfig.save();
                return true;
            }
            float setY = curY + 30;
            for (Setting s : m.getSettings()) {
                if (s instanceof Setting.BoolSetting && hit(mouseX, mouseY, cx + cw - 48, setY, 38, 15)) {
                    ((Setting.BoolSetting) s).toggle();
                    CreamConfig.save();
                    return true;
                } else if (s instanceof Setting.ModeSetting && hit(mouseX, mouseY, cx + cw - 120, setY, 110, 15)) {
                    ((Setting.ModeSetting) s).cycle();
                    CreamConfig.save();
                    return true;
                } else if (s instanceof Setting.SliderSetting && hit(mouseX, mouseY, cx + cw - 140, setY, 130, 15)) {
                    draggingSlider = (Setting.SliderSetting) s;
                    sliderX = cx + cw - 140;
                    sliderW = 130;
                    return true;
                }
                setY += 20;
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
        if (draggingSlider != null) {
            draggingSlider = null;
            CreamConfig.save();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        targetScrollY = Math.max(0f, targetScrollY - (float) delta * 24f);
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (typingProfile && Character.isLetterOrDigit(codePoint) || (typingProfile && (codePoint == '_' || codePoint == '-'))) {
            if (profileInput.length() < 20) profileInput += codePoint;
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
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (typingProfile && !profileInput.isEmpty()) { profileInput = profileInput.substring(0, profileInput.length() - 1); return true; }
            if (typingSearch && !searchQuery.isEmpty()) { searchQuery = searchQuery.substring(0, searchQuery.length() - 1); return true; }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
