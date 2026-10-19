package com.deathmotion.marlowcrystal.network;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;

final class OptOutNotice {
    private static final Text PREFIX = Text.literal((String)"[").formatted(Formatting.GRAY).append((Text)Text.literal((String)"Marlow's Crystal Optimizer").formatted(Formatting.AQUA)).append((Text)Text.literal((String)"] ").formatted(Formatting.GRAY));

    private OptOutNotice() {
    }

    static void showLater(MinecraftClient client) {
        CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS).execute(() -> client.execute(() -> OptOutNotice.show(client)));
    }

    private static void show(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        client.player.sendMessage(OptOutNotice.message(), false);
    }

    private static Text message() {
        MutableText hover = Text.empty().append((Text)Text.literal((String)"Why is this disabled?\n").formatted(Formatting.AQUA)).append((Text)Text.literal((String)"\u2022 This server has requested Marlow's Crystal Optimizer to be disabled.\n").formatted(Formatting.GRAY)).append((Text)Text.literal((String)"\u2022 This may be to enforce server rules or avoid compatibility issues.\n").formatted(Formatting.GRAY)).append((Text)Text.literal((String)"\nThis only applies while you are connected to this server.").formatted(Formatting.ITALIC));
        Style hoverStyle = Style.EMPTY.withHoverEvent((HoverEvent)new HoverEvent.ShowText((Text)hover));
        MutableText text = Text.literal((String)"Optimizer disabled on this server.").fillStyle(hoverStyle.withColor(Formatting.RED));
        return PREFIX.copy().fillStyle(hoverStyle).append((Text)text);
    }
}

