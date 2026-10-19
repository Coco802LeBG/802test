package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.packet.Channels;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;

public final class OptOutPacket
implements CustomPayload {
    public static final
    CustomPayload.Id<OptOutPacket> TYPE = new CustomPayload.Id(Channels.of("opt_out"));
    public static final OptOutPacket INSTANCE = new OptOutPacket();
    public static final PacketCodec<PacketByteBuf, OptOutPacket> STREAM_CODEC = new PacketCodec<PacketByteBuf, OptOutPacket>(){

        public void encode(PacketByteBuf buffer, OptOutPacket value) {
        }

        
        public OptOutPacket decode(PacketByteBuf buffer) {
            return INSTANCE;
        }
    };

    private OptOutPacket() {
    }

    
    public
    CustomPayload.Id<OptOutPacket> getId() {
        return TYPE;
    }
}

