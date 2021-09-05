package gg.tgb.shardedmc.packets.events;

import gg.tgb.shardedmc.packets.AbstractPacket;
import gg.tgb.shardedmc.util.UUIDUtil;
import io.netty.buffer.ByteBuf;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MovePacket extends AbstractPacket {

    private UUID uuid;
    private Location location;

    private int PACKET_ID = 2;

    public MovePacket(@NotNull UUID sender, @NotNull ByteBuf data) {
        super(sender);
        read(data);
    }

    public MovePacket() {
        super(UUIDUtil.EMPTY_UUID);
    }

    public MovePacket(@NotNull UUID uuid, Location location) {
        super(UUIDUtil.EMPTY_UUID);
        this.uuid = uuid;
        this.location = location;
    }

    @Override
    public void read(@NotNull ByteBuf buffer) {
        this.uuid = readUUID(buffer);
        this.location = readLocation(buffer);
    }

    @Override
    public void write(@NotNull ByteBuf buffer) {
        buffer.writeInt(PACKET_ID);
        writeUUID(this.uuid, buffer);
        writeLocation(location, buffer);
    }

    public @NotNull UUID getUuid() { return uuid; }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public Location getLocation() { return location; }

    public void setLocation(Location location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "MovePacket{" +
                "sender=" + sender +
                ", uuid=" + uuid +
                ", location=" + location.toString() +
                '}';
    }
}
