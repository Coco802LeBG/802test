package com.deathmotion.marlowcrystal.network;

import com.deathmotion.marlowcrystal.network.OptOutNotice;
import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.network.packet.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutAckPacket;
import com.deathmotion.marlowcrystal.network.packet.VersionPacket;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;

public final class ServerSession {
    private volatile boolean optedOut;
    private volatile boolean notified;

    public boolean isOptedOut() {
        return this.optedOut;
    }

    public void joined(MinecraftClient client, Consumer<ServerboundPacket> send) {
        if (client.isInSingleplayer()) {
            return;
        }
        send.accept(VersionPacket.current());
    }

    public void optOutReceived(MinecraftClient client, Consumer<ServerboundPacket> send) {
        this.optedOut = true;
        if (this.claimNotice()) {
            OptOutNotice.showLater(client);
        }
        send.accept(OptOutAckPacket.INSTANCE);
    }

    public void challengeReceived(int challengeId, Consumer<ServerboundPacket> send) {
        send.accept(new ChallengeResponsePacket(challengeId));
    }

    public synchronized void disconnected() {
        this.optedOut = false;
        this.notified = false;
    }

    private synchronized boolean claimNotice() {
        if (this.notified) {
            return false;
        }
        this.notified = true;
        return true;
    }
}

