package com.creamclient.module;

public enum Category {
    VISUALS("Visuals"),
    HUD("HUD"),
    MOVEMENT("Movement"),
    INTERFACE("Interface"),
    MEDIA("Media");

    private final String title;

    Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
