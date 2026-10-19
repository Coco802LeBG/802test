package com.deathmotion.marlowcrystal;

import com.deathmotion.marlowcrystal.Log;
import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.config.SettingsFile;
import com.deathmotion.marlowcrystal.crystal.CrystalBreaker;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.loader.fabric.FabricLoaderAccess;
import com.deathmotion.marlowcrystal.network.ServerSession;
import com.deathmotion.marlowcrystal.update.UpdateCheck;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModVersion;

public final class MarlowCrystal {
    public static final String MOD_ID = "marlowcrystal";
    private static final MarlowCrystal INSTANCE = new MarlowCrystal(new FabricLoaderAccess());
    private final SettingsFile settingsFile;
    private final Settings settings;
    private final UpdateCheck updateCheck;
    private final ServerSession serverSession = new ServerSession();
    private final KeptCrystals keptCrystals = new KeptCrystals();
    private final CrystalBreaker crystalBreaker = new CrystalBreaker(this.keptCrystals);

    private MarlowCrystal(LoaderAccess loader) {
        this.settingsFile = new SettingsFile(loader.configDirectory().resolve("marlowcrystal.json"));
        this.settings = this.settingsFile.load();
        this.updateCheck = new UpdateCheck(this.settings, loader);
    }

    public static MarlowCrystal get() {
        return INSTANCE;
    }

    public static void initialize() {
        ModVersion version = CurrentBuild.VERSION;
        String commit = version.commit();
        Log.info("Mod initialized, version " + version.toDisplayString() + " for Minecraft 1.21.11 built " + String.valueOf(CurrentBuild.TIMESTAMP) + (String)(commit != null ? " (" + commit + ", dirty)" : ""));
    }

    public Settings settings() {
        return this.settings;
    }

    public UpdateCheck updateCheck() {
        return this.updateCheck;
    }

    public ServerSession serverSession() {
        return this.serverSession;
    }

    public KeptCrystals keptCrystals() {
        return this.keptCrystals;
    }

    public CrystalBreaker crystalBreaker() {
        return this.crystalBreaker;
    }

    public void saveSettings() {
        this.settingsFile.save(this.settings);
        this.updateCheck.settingsSaved();
    }
}

