package com.creamclient.module;

import com.creamclient.media.SoundFX;
import com.creamclient.ui.NotificationManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private final String name, description;
    private final Category category;
    private boolean enabled;
    private boolean expanded = false;
    private float animProgress = 0.0f;
    private int keyCode = -1;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, Category category, boolean defaultEnabled) {
        this.name = name; this.description = description; this.category = category;
        this.enabled = defaultEnabled; this.animProgress = defaultEnabled ? 1.0f : 0.0f;
    }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) onEnable(); else onDisable();
        }
    }
    public void toggle() {
        setEnabled(!this.enabled);
        NotificationManager.push(this.name, this.enabled);
        SoundFX.toggle(this.enabled);
    }
    public int getKey() { return keyCode; }
    public void setKey(int keyCode) { this.keyCode = keyCode; }
    public String getKeyName() {
        if (keyCode <= 0) return "-";
        String kn = GLFW.glfwGetKeyName(keyCode, 0);
        if (kn != null && !kn.isEmpty()) return kn.toUpperCase();
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) return "SHIFT";
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) return "CTRL";
        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) return "ALT";
        if (keyCode == GLFW.GLFW_KEY_SPACE) return "SPACE";
        if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F12) return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
        return "K" + keyCode;
    }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
    public float getAnimProgress() { return animProgress; }
    public void setAnimProgress(float animProgress) { this.animProgress = animProgress; }
    public <T extends Setting> T addSetting(T setting) { settings.add(setting); return setting; }
    public List<Setting> getSettings() { return settings; }
    protected void onEnable() {}
    protected void onDisable() {}
}
