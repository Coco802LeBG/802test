package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module802 {
    public final String name;
    public final String description;
    public final List<Setting> settings = new ArrayList<>();
    /** Position à l'écran (null si le module n'a pas d'élément HUD déplaçable). */
    public NumberSetting posX, posY;
    private boolean enabled;

    protected Module802(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** true si l'état on/off est géré ailleurs (ex: Crystal Reflex). */
    public boolean externalState() { return false; }

    /** Catégorie du menu : PvP, HUD, Visuals, Player, Misc. */
    public String category() { return "Misc"; }
    /** Taille de la boîte dans l'éditeur de HUD. */
    public int boxWidth() { return 80; }
    public int boxHeight() { return 24; }

    protected void addPosition(int defX, int defY) {
        posX = addNumber("x", "Position X", 0, 960, 1, defX);
        posY = addNumber("y", "Position Y", 0, 540, 1, defY);
    }

    protected BoolSetting addBool(String key, String label, boolean def) {
        BoolSetting s = new BoolSetting(this, key, label, def);
        settings.add(s);
        return s;
    }

    protected NumberSetting addNumber(String key, String label, double min, double max, double step, double def) {
        NumberSetting s = new NumberSetting(this, key, label, min, max, step, def);
        settings.add(s);
        return s;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onHud(DrawContext ctx, MinecraftClient mc) {}
}
