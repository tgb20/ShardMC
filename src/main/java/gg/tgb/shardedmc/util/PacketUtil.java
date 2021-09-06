package gg.tgb.shardedmc.util;

import gg.tgb.shardedmc.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public class PacketUtil {

    protected static final ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

    public static byte[] packetToByteArray(Packet packet, int length) {
        ByteBuf bb = alloc.buffer(length, length);
        packet.write(bb);
        byte[] b = new byte[bb.readableBytes()];
        bb.duplicate().readBytes(b);
        return b;
    }
}
