package com.deathmotion.marlowcrystal.config;

import com.deathmotion.marlowcrystal.config.UpdateSource;

public final class Settings {
    private volatile UpdateSource updateSource = UpdateSource.MODRINTH;
    private volatile boolean experimentalBuilds;
    private volatile boolean keepRender;
    private volatile boolean reflexMode = true;
    private volatile boolean rawMouseInput = true;
    private volatile boolean totemGuard = true;
    private volatile boolean showStatusMessages = true;
    private volatile int keepReleaseMs = 1500;
    private volatile int maxKeptCrystals = 4;
    private volatile java.util.List<String> disabledServers = new java.util.ArrayList<>();
    public int getKeepReleaseMs() { return Math.max(250, Math.min(3000, this.keepReleaseMs == 0 ? 1500 : this.keepReleaseMs)); }
    public void setKeepReleaseMs(int v) { this.keepReleaseMs = v; }
    public int getMaxKeptCrystals() { return Math.max(1, Math.min(16, this.maxKeptCrystals == 0 ? 4 : this.maxKeptCrystals)); }
    public void setMaxKeptCrystals(int v) { this.maxKeptCrystals = v; }
    /** Adresses de serveurs (ex: "mc.exemple.net") sur lesquelles l'optimizer est désactivé. */
    public boolean isDisabledOn(String address) {
        if (address == null || this.disabledServers == null) return false;
        String a = address.toLowerCase(java.util.Locale.ROOT);
        for (String s : this.disabledServers) if (s != null && !s.isBlank() && a.contains(s.toLowerCase(java.util.Locale.ROOT).trim())) return true;
        return false;
    }

    public UpdateSource getUpdateSource() {
        UpdateSource source = this.updateSource;
        return source != null ? source : UpdateSource.MODRINTH;
    }

    public void setUpdateSource(UpdateSource updateSource) {
        this.updateSource = updateSource;
    }

    public boolean isExperimentalBuilds() {
        return this.experimentalBuilds;
    }

    public void setExperimentalBuilds(boolean experimentalBuilds) {
        this.experimentalBuilds = experimentalBuilds;
    }

    public boolean isKeepRender() {
        return this.keepRender;
    }

    public void setKeepRender(boolean keepRender) {
        this.keepRender = keepRender;
    }

    public boolean isReflexMode() {
        return this.reflexMode;
    }

    public void setReflexMode(boolean reflexMode) {
        this.reflexMode = reflexMode;
    }

    public boolean isRawMouseInput() {
        return this.rawMouseInput;
    }

    public void setRawMouseInput(boolean rawMouseInput) {
        this.rawMouseInput = rawMouseInput;
    }

    public boolean isTotemGuard() {
        return this.totemGuard;
    }

    public void setTotemGuard(boolean totemGuard) {
        this.totemGuard = totemGuard;
    }

    public boolean isShowStatusMessages() {
        return this.showStatusMessages;
    }

    public void setShowStatusMessages(boolean showStatusMessages) {
        this.showStatusMessages = showStatusMessages;
    }
}

