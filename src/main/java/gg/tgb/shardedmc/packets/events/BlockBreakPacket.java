package gg.tgb.shardedmc.packets.events;

import gg.tgb.shardedmc.packets.AbstractPacket;
import gg.tgb.shardedmc.util.UUIDUtil;
import io.netty.buffer.ByteBuf;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlockBreakPacket extends AbstractPacket {

    private Location location;
    private int PACKET_ID = 4;
    public int LENGTH = 40;

    public BlockBreakPacket(@NotNull UUID sender, @NotNull ByteBuf data) {
        super(sender);
        read(data);
    }

    public BlockBreakPacket() {
        super(UUIDUtil.EMPTY_UUID);
    }

    public BlockBreakPacket(@NotNull String type, @NotNull Location location) {
        super(UUIDUtil.EMPTY_UUID);
        this.location = location;
    }

    @Override
    public void read(@NotNull ByteBuf buffer) {
        this.location = readLocation(buffer);
    }

    @Override
    public void write(@NotNull ByteBuf buffer) {
        buffer.writeByte(PACKET_ID);
        writeLocation(this.location, buffer);
    }


    public @NotNull Location getLocation() { return location; }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BlockPlacePacket{" +
                "sender=" + sender +
                ", location=" + location.toString() +
                '}';
    }
}
