package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.network.packet.Channels;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModLoader;
import com.deathmotion.marlowcrystal.version.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;

public record VersionPacket(int major, int minor, int patch, boolean snapshot, ModLoader loader, String commit, boolean dirty, long buildTimestamp) implements ServerboundPacket
{
    private static final int COMMIT_MAX_LENGTH = 40;
    public static final CustomPayload.Id<VersionPacket> TYPE = new CustomPayload.Id(Channels.of("version"));
    public static final PacketCodec<PacketByteBuf, VersionPacket> STREAM_CODEC = new PacketCodec<PacketByteBuf, VersionPacket>(){

        public void encode(PacketByteBuf buf, VersionPacket packet) {
            VersionPacket.encode(buf, packet);
        }

        
        public VersionPacket decode(PacketByteBuf buf) {
            int major = buf.readVarInt();
            int minor = buf.readVarInt();
            int patch = buf.readVarInt();
            boolean snapshot = buf.readBoolean();
            if (!buf.isReadable()) {
                return new VersionPacket(major, minor, patch, snapshot, null, "", false, 0L);
            }
            VersionPacket packet = new VersionPacket(major, minor, patch, snapshot, ModLoader.byNetworkId(buf.readVarInt()), buf.readString(40), buf.readBoolean(), buf.readLong());
            buf.skipBytes(buf.readableBytes());
            return packet;
        }
    };

    public static VersionPacket current() {
        ModVersion version = CurrentBuild.VERSION;
        String commit = "d66f53423636a276d9c8b8da3dc9b180586d022d";
        return new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot(), CurrentBuild.LOADER, commit != null ? commit : "", true, CurrentBuild.TIMESTAMP.toEpochMilli());
    }

    private static void encode(PacketByteBuf buf, VersionPacket packet) {
        buf.writeVarInt(packet.major());
        buf.writeVarInt(packet.minor());
        buf.writeVarInt(packet.patch());
        buf.writeBoolean(packet.snapshot());
        buf.writeVarInt(packet.loader().networkId());
        buf.writeString(packet.commit(), 40);
        buf.writeBoolean(packet.dirty());
        buf.writeLong(packet.buildTimestamp());
    }

    
    public CustomPayload.Id<VersionPacket> getId() {
        return TYPE;
    }
}

