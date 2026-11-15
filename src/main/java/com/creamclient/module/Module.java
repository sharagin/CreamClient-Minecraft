package com.creamclient.module;

public class Module {
    private final String name;
    private final String description;
    private boolean enabled;

    public Module(String name, String description, boolean defaultEnabled) {
        this.name = name;
        this.description = description;
        this.enabled = defaultEnabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    protected void onEnable() {}

    protected void onDisable() {}
}
