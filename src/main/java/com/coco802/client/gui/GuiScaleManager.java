package com.coco802.client.gui;

import com.coco802.client.Client802;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/** GUI scale différent selon l'écran (inventaire, coffres, chat, pause, autres). */
public final class GuiScaleManager {
    public static final String[] KINDS = {"inventory", "container", "chat", "pause", "other"};
    public static final String[] LABELS = {"Inventaire", "Coffres / tables", "Chat", "Menu pause", "Autres menus"};

    private static boolean applying;
    private static final Set<Screen> REGISTERED = Collections.newSetFromMap(new WeakHashMap<>());

    private GuiScaleManager() {}

    public static String kind(Screen screen) {
        if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) return "inventory";
        if (screen instanceof HandledScreen) return "container";
        if (screen instanceof ChatScreen) return "chat";
        if (screen instanceof GameMenuScreen) return "pause";
        return "other";
    }

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (REGISTERED.add(screen)) {
                ScreenEvents.remove(screen).register(s -> restore(client));
            }
            if (applying) return;
            int wanted = Client802.CONFIG.scaleFor(kind(screen));
            if (wanted <= 0) return;
            Window window = client.getWindow();
            int target = window.calculateScaleFactor(wanted, client.forcesUnicodeFont());
            if (target == (int) window.getScaleFactor()) return;
            window.setScaleFactor(target);
            applying = true;
            try {
                screen.resize(window.getScaledWidth(), window.getScaledHeight());
            } finally {
                applying = false;
            }
        });
    }

    private static void restore(MinecraftClient client) {
        Window window = client.getWindow();
        int normal = window.calculateScaleFactor(client.options.getGuiScale().getValue(), client.forcesUnicodeFont());
        if (normal != (int) window.getScaleFactor()) {
            window.setScaleFactor(normal);
        }
    }
}
