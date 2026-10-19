package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.network.packet.Channels;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;

public final class OptOutAckPacket
implements ServerboundPacket {
    public static final
    CustomPayload.Id<OptOutAckPacket> TYPE = new CustomPayload.Id(Channels.of("opt_out_ack"));
    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();
    public static final PacketCodec<PacketByteBuf, OptOutAckPacket> STREAM_CODEC = new PacketCodec<PacketByteBuf, OptOutAckPacket>(){

        public void encode(PacketByteBuf buffer, OptOutAckPacket value) {
        }

        
        public OptOutAckPacket decode(PacketByteBuf buffer) {
            return INSTANCE;
        }
    };

    private OptOutAckPacket() {
    }

    
    public
     CustomPayload.Id<OptOutAckPacket> getId() {
        return TYPE;
    }
}

