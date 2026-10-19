package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

public final class KeystrokesModule extends Module802 {

    public KeystrokesModule() {
        super("Keystrokes", "Affiche les touches WASD et espace");
        addPosition(4, 60);
    }

    @Override public String category() { return "HUD"; }
    @Override public int boxWidth() { return 58; }
    @Override public int boxHeight() { return 58; }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        int px = posX.getInt();
        int py = posY.getInt();
        key(ctx, mc, "W", mc.options.forwardKey, px + 20, py, 18);
        key(ctx, mc, "A", mc.options.leftKey, px, py + 20, 18);
        key(ctx, mc, "S", mc.options.backKey, px + 20, py + 20, 18);
        key(ctx, mc, "D", mc.options.rightKey, px + 40, py + 20, 18);
        key(ctx, mc, "_", mc.options.jumpKey, px, py + 40, 58);
    }

    private void key(DrawContext ctx, MinecraftClient mc, String label, KeyBinding k, int x, int y, int w) {
        boolean down = k.isPressed();
        ctx.fill(x, y, x + w, y + 18, down ? 0xCC2E9BFF : 0x88000000);
        int tx = x + (w - mc.textRenderer.getWidth(label)) / 2;
        ctx.drawTextWithShadow(mc.textRenderer, label, tx, y + 5, 0xFFFFFFFF);
    }
}
