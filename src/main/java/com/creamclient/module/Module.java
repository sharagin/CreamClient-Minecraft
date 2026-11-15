package com.creamclient.module;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private boolean expanded = false;
    private float animProgress = 0.0f;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, Category category, boolean defaultEnabled) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = defaultEnabled;
        this.animProgress = defaultEnabled ? 1.0f : 0.0f;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public void toggle() {
        setEnabled(!this.enabled);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public float getAnimProgress() {
        return animProgress;
    }

    public void setAnimProgress(float animProgress) {
        this.animProgress = animProgress;
    }

    public <T extends Setting> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    protected void onEnable() {}

    protected void onDisable() {}
}
