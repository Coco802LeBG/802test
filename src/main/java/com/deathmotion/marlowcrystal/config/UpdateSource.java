package com.deathmotion.marlowcrystal.config;

public enum UpdateSource {
    MODRINTH("Modrinth"),
    GITHUB("GitHub");

    private final String displayName;

    private UpdateSource(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }
}

