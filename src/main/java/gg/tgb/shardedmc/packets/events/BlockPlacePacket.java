package gg.tgb.shardedmc.packets.events;

import gg.tgb.shardedmc.packets.AbstractPacket;
import gg.tgb.shardedmc.util.UUIDUtil;
import io.netty.buffer.ByteBuf;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlockPlacePacket extends AbstractPacket {

    private String type;
    private Location location;
    private int PACKET_ID = 3;
    public int LENGTH = 88;

    public BlockPlacePacket(@NotNull UUID sender, @NotNull ByteBuf data) {
        super(sender);
        read(data);
    }

    public BlockPlacePacket() {
        super(UUIDUtil.EMPTY_UUID);
    }

    public BlockPlacePacket(@NotNull String type, @NotNull Location location) {
        super(UUIDUtil.EMPTY_UUID);
        this.type = type;
        this.location = location;
    }

    @Override
    public void read(@NotNull ByteBuf buffer) {
        this.type = readString(buffer);
        this.location = readLocation(buffer);
    }

    @Override
    public void write(@NotNull ByteBuf buffer) {
        buffer.writeByte(PACKET_ID);
        writeString(this.type, buffer);
        writeLocation(this.location, buffer);
    }

    public @NotNull String getType() { return type; }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    public @NotNull Location getLocation() { return location; }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BlockPlacePacket{" +
                "sender=" + sender +
                ", type=" + type +
                ", location=" + location.toString() +
                '}';
    }
}
