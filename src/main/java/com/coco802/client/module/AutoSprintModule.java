package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

public final class AutoSprintModule extends Module802 {
    public AutoSprintModule() { super("AutoSprint", "Sprint automatique en avançant"); }

    @Override public String category() { return "Player"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player != null && mc.options.forwardKey.isPressed()) {
            mc.options.sprintKey.setPressed(true);
        }
    }
}
