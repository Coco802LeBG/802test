package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.packet.Channels;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;

public record ChallengePacket(int challengeId) implements CustomPayload
{
    public static final
    CustomPayload.Id<ChallengePacket> TYPE = new CustomPayload.Id(Channels.of("challenge"));
    public static final PacketCodec<PacketByteBuf, ChallengePacket> STREAM_CODEC = new PacketCodec<PacketByteBuf, ChallengePacket>(){

        public void encode(PacketByteBuf buf, ChallengePacket value) {
            buf.writeInt(value.challengeId());
        }

        
        public ChallengePacket decode(PacketByteBuf buf) {
            return new ChallengePacket(buf.readInt());
        }
    };

    
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}

