package com.coco802.client.gui;

import com.coco802.client.Client802;
import com.coco802.client.module.Module802;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Éditeur de HUD : glisse chaque élément à l'endroit voulu. */
public final class HudEditorScreen extends Screen {
    private static final int ACCENT = 0xFF2E9BFF;
    private final Screen parent;
    private Module802 dragged;
    private int offX, offY;
    private boolean wasDown;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("Éditeur de HUD"));
        this.parent = parent;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x55000000);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        String help = "Éditeur de HUD : glisse les éléments - Échap pour revenir";
        ctx.drawTextWithShadow(this.textRenderer, help, (this.width - this.textRenderer.getWidth(help)) / 2, 8, 0xFFFFFFFF);

        boolean any = false;
        for (Module802 m : Client802.MODULES) {
            if (!m.isEnabled() || m.posX == null) continue;
            any = true;
            int x = m.posX.getInt();
            int y = m.posY.getInt();
            int w = m.boxWidth();
            int h = m.boxHeight();
            ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, m == dragged ? 0x442E9BFF : 0x222E9BFF);
            ctx.fill(x - 2, y - 2, x + w + 2, y - 1, ACCENT);
            ctx.fill(x - 2, y + h + 1, x + w + 2, y + h + 2, ACCENT);
            ctx.fill(x - 2, y - 2, x - 1, y + h + 2, ACCENT);
            ctx.fill(x + w + 1, y - 2, x + w + 2, y + h + 2, ACCENT);
            ctx.drawTextWithShadow(this.textRenderer, m.name, x, y - 12, ACCENT);
        }
        if (!any) {
            String none = "Aucun élément actif : active HUD, Keystrokes ou ArmorHud dans le menu.";
            ctx.drawTextWithShadow(this.textRenderer, none, (this.width - this.textRenderer.getWidth(none)) / 2, this.height / 2, 0xFFB0BAC8);
        }
        handleMouse(mx, my);
    }

    private void handleMouse(int mx, int my) {
        long handle = this.client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (down && !wasDown) {
            for (int i = Client802.MODULES.size() - 1; i >= 0; i--) {
                Module802 m = Client802.MODULES.get(i);
                if (!m.isEnabled() || m.posX == null) continue;
                int x = m.posX.getInt();
                int y = m.posY.getInt();
                if (mx >= x - 2 && mx <= x + m.boxWidth() + 2 && my >= y - 2 && my <= y + m.boxHeight() + 2) {
                    dragged = m;
                    offX = mx - x;
                    offY = my - y;
                    break;
                }
            }
        } else if (down && dragged != null) {
            int nx = Math.max(0, Math.min(this.width - dragged.boxWidth(), mx - offX));
            int ny = Math.max(0, Math.min(this.height - dragged.boxHeight(), my - offY));
            dragged.posX.set(nx);
            dragged.posY.set(ny);
        }
        if (!down && wasDown) {
            dragged = null;
            Client802.CONFIG.save();
        }
        wasDown = down;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
