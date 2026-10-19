package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public final class ArmorHudModule extends Module802 {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHudModule() {
        super("ArmorHud", "Affiche ton armure à l'écran");
        addPosition(4, 130);
    }

    @Override public String category() { return "HUD"; }
    @Override public int boxWidth() { return 18; }
    @Override public int boxHeight() { return 72; }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        int py = posY.getInt();
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, posX.getInt(), py);
                py += 18;
            }
        }
    }
}
