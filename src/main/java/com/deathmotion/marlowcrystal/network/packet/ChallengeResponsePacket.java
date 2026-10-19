package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.network.packet.Channels;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;

public record ChallengeResponsePacket(int challengeId) implements ServerboundPacket
{
    public static final
    CustomPayload.Id<ChallengeResponsePacket> TYPE = new CustomPayload.Id(Channels.of("challenge_response"));
    public static final PacketCodec<PacketByteBuf, ChallengeResponsePacket> STREAM_CODEC = new PacketCodec<PacketByteBuf, ChallengeResponsePacket>(){

        public void encode(PacketByteBuf buf, ChallengeResponsePacket value) {
            buf.writeInt(value.challengeId());
        }

        
        public ChallengeResponsePacket decode(PacketByteBuf buf) {
            return new ChallengeResponsePacket(buf.readInt());
        }
    };

    
    public
     CustomPayload.Id<ChallengeResponsePacket> getId() {
        return TYPE;
    }
}

