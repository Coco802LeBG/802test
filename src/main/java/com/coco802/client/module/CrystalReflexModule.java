package com.coco802.client.module;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.loader.fabric.ReflexController;

/** Carte « Crystal Reflex » : on/off (F8) + ses réglages, dans le menu 802. */
public final class CrystalReflexModule extends Module802 {
    public CrystalReflexModule() {
        super("Crystal Reflex", "Prédiction crystal côté client (F8)");
        settings.add(new BoolSetting(this, "keepRender", "Keep Render",
            () -> s().isKeepRender(), v -> s().setKeepRender(v)));
        settings.add(new BoolSetting(this, "rawMouse", "Raw mouse input",
            () -> s().isRawMouseInput(), v -> s().setRawMouseInput(v)));
        settings.add(new BoolSetting(this, "totemGuard", "Totem guard",
            () -> s().isTotemGuard(), v -> s().setTotemGuard(v)));
        settings.add(new BoolSetting(this, "messages", "Messages d'état",
            () -> s().isShowStatusMessages(), v -> s().setShowStatusMessages(v)));
        settings.add(new NumberSetting(this, "release", "Délai libération (ms)", 250, 3000, 50,
            () -> s().getKeepReleaseMs(), v -> s().setKeepReleaseMs((int) v)));
        settings.add(new NumberSetting(this, "maxKept", "Crystals gardés max", 1, 16, 1,
            () -> s().getMaxKeptCrystals(), v -> s().setMaxKeptCrystals((int) v)));
    }

    private static Settings s() { return MarlowCrystal.get().settings(); }

    @Override public String category() { return "PvP"; }
    @Override public boolean externalState() { return true; }
    @Override public boolean isEnabled() { return ReflexController.isActive(); }
    @Override public void setEnabled(boolean value) { ReflexController.setActive(value); }
    @Override public void toggle() { ReflexController.setActive(!ReflexController.isActive()); }
}
