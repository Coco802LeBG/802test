package com.coco802.client;

import com.coco802.client.config.Config802;
import com.coco802.client.gui.GuiScaleManager;
import com.coco802.client.gui.MenuScreen;
import com.coco802.client.module.ArmorHudModule;
import com.coco802.client.module.AutoSprintModule;
import com.coco802.client.module.CrystalReflexModule;
import com.coco802.client.module.KeystrokesModule;
import com.coco802.client.module.HudModule;
import com.coco802.client.module.Module802;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class Client802 implements ClientModInitializer {
    public static Config802 CONFIG;
    public static final List<Module802> MODULES = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        CONFIG = Config802.load();

        // === Enregistre tes modules ici ===
        MODULES.add(new CrystalReflexModule());
        MODULES.add(new HudModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new ArmorHudModule());
        MODULES.add(new AutoSprintModule());

        applyModules();

        GuiScaleManager.init();

        // Bouton "802 Client" en haut à gauche du menu principal et du menu pause
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
                Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal("802 Client"),
                    b -> client.setScreen(new MenuScreen(screen))).dimensions(6, 6, 80, 20).build());
            }
        });

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("client802", "main"));
        KeyBinding menuKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.client802.menu", InputUtil.Type.KEYSYM, CONFIG.menuKey, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new MenuScreen(null));
            }
            for (Module802 m : MODULES) if (m.isEnabled()) m.onTick(mc);
        });

        HudElementRegistry.addLast(Identifier.of("client802", "hud"), (context, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            for (Module802 m : MODULES) if (m.isEnabled()) m.onHud(context, mc);
        });
    }

    public static void applyModules() {
        for (Module802 m : MODULES) {
            if (!m.externalState()) m.setEnabled(CONFIG.moduleEnabled(m.name, m instanceof HudModule));
        }
    }

    public static void saveModules() {
        for (Module802 m : MODULES) CONFIG.modules.put(m.name, m.isEnabled());
        CONFIG.save();
    }
}
