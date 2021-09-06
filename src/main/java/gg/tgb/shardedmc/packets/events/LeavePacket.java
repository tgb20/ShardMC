package gg.tgb.shardedmc.packets.events;

import gg.tgb.shardedmc.packets.AbstractPacket;
import gg.tgb.shardedmc.util.UUIDUtil;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LeavePacket extends AbstractPacket {

    private UUID uuid;
    private int PACKET_ID = 1;
    public int LENGTH = 24;

    public LeavePacket(@NotNull UUID sender, @NotNull ByteBuf data) {
        super(sender);
        read(data);
    }

    public LeavePacket() {
        super(UUIDUtil.EMPTY_UUID);
    }

    public LeavePacket(@NotNull UUID uuid) {
        super(UUIDUtil.EMPTY_UUID);
        this.uuid = uuid;
    }

    @Override
    public void read(@NotNull ByteBuf buffer) {
        this.uuid = readUUID(buffer);
    }

    @Override
    public void write(@NotNull ByteBuf buffer) {
        buffer.writeByte(PACKET_ID);
        writeUUID(this.uuid, buffer);
    }

    public @NotNull UUID getUuid() { return uuid; }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "LeavePacket{" +
                "sender=" + sender +
                ", uuid=" + uuid +
                '}';
    }
}
