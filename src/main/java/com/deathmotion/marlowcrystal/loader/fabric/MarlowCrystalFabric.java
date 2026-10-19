package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.loader.fabric.ReflexController;
import com.deathmotion.marlowcrystal.network.ServerSession;
import com.deathmotion.marlowcrystal.network.packet.ChallengePacket;
import com.deathmotion.marlowcrystal.network.packet.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutAckPacket;
import com.deathmotion.marlowcrystal.network.packet.OptOutPacket;
import com.deathmotion.marlowcrystal.network.packet.VersionPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Environment(value=EnvType.CLIENT)
public class MarlowCrystalFabric
implements ClientModInitializer {
    public void onInitializeClient() {
        MarlowCrystal.initialize();
        ReflexController.initialize();
        MarlowCrystalFabric.registerPayloads();
        MarlowCrystalFabric.registerListeners(MarlowCrystal.get().serverSession());
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.configurationS2C().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC);
    }

    private static void registerListeners(ServerSession session) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> session.joined(client, arg_0 -> ((PacketSender)sender).sendPacket(arg_0)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> session.disconnected());
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> session.disconnected());
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) -> session.optOutReceived(context.client(), ClientPlayNetworking::send));
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.TYPE, (payload, context) -> session.challengeReceived(payload.challengeId(), ClientPlayNetworking::send));
    }
}

