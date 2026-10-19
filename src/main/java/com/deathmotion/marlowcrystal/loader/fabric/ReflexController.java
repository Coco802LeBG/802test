package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public final class ReflexController {
    private static final int TOGGLE_KEY = 297;
    private static final int RESYNC_KEY = 296;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(net.minecraft.util.Identifier.of("marlowcrystal", "main"));
    private static KeyBinding toggleKey;
    private static KeyBinding resyncKey;
    private static boolean active;

    public static boolean isActive() { return active; }

    public static void setActive(boolean value) {
        active = value;
        MarlowCrystal.get().settings().setReflexMode(value);
        MarlowCrystal.get().saveSettings();
    }
    private static int lastOffhandCount;
    private static boolean lastOffhandWasTotem;

    private ReflexController() {
    }

    public static void initialize() {
        active = MarlowCrystal.get().settings().isReflexMode();
        toggleKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.marlowcrystal.toggle_reflex", InputUtil.Type.KEYSYM, 297, CATEGORY));
        resyncKey = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.marlowcrystal.resync_held_item", InputUtil.Type.KEYSYM, 296, CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(ReflexController::tick);
    }

    private static void tick(MinecraftClient client) {
        if (toggleKey != null) {
            while (toggleKey.wasPressed()) {
                active = !active;
                MarlowCrystal.get().settings().setReflexMode(active);
                MarlowCrystal.get().saveSettings();
                ReflexController.notify(client, active ? "Crystal Reflex: ON" : "Crystal Reflex: OFF");
                if (!active) continue;
                ReflexController.applyRawMouseInput(client);
            }
        }
        if (resyncKey != null) {
            while (resyncKey.wasPressed()) {
                ReflexController.forceHeldItemResync(client);
            }
        }
        if (active && MarlowCrystal.get().settings().isRawMouseInput()) {
            ReflexController.applyRawMouseInput(client);
        }
        if (MarlowCrystal.get().settings().isTotemGuard()) {
            ReflexController.observeTotemState(client);
        }
    }

    private static void applyRawMouseInput(MinecraftClient client) {
        if (client.getWindow() == null || !GLFW.glfwRawMouseMotionSupported()) {
            return;
        }
        GLFW.glfwSetInputMode((long)client.getWindow().getHandle(), (int)208901, (int)1);
    }

    private static void forceHeldItemResync(MinecraftClient client) {
        ItemStack offhand;
        if (client.interactionManager == null || client.player == null) {
            return;
        }
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(client.player.getInventory().getSelectedSlot()));
        }
        String state = (offhand = client.player.getOffHandStack()).isOf(Items.TOTEM_OF_UNDYING) ? "Totem confirm\u00e9 dans la main secondaire" : "Slot tenu resynchronis\u00e9";
        ReflexController.notify(client, "Crystal Reflex: " + state);
    }

    private static void observeTotemState(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        ItemStack offhand = client.player.getOffHandStack();
        boolean isTotem = offhand.isOf(Items.TOTEM_OF_UNDYING);
        int count = offhand.getCount();
        if (count != lastOffhandCount || isTotem != lastOffhandWasTotem) {
            lastOffhandCount = count;
            lastOffhandWasTotem = isTotem;
        }
    }

    private static void notify(MinecraftClient client, String message) {
        if (client.player != null && MarlowCrystal.get().settings().isShowStatusMessages()) {
            client.player.sendMessage((Text)Text.literal((String)message), true);
        }
    }

    static {
        lastOffhandCount = -1;
    }
}

