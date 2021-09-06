package gg.tgb.shardedmc.packets.events;

import gg.tgb.shardedmc.packets.AbstractPacket;
import gg.tgb.shardedmc.util.UUIDUtil;
import io.netty.buffer.ByteBuf;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JoinPacket extends AbstractPacket {

    private UUID uuid;
    private String name;
    private Location location;

    private int PACKET_ID = 0;
    public int LENGTH = 88;

    public JoinPacket(@NotNull UUID sender, @NotNull ByteBuf data) {
        super(sender);
        read(data);
    }

    public JoinPacket() {
        super(UUIDUtil.EMPTY_UUID);
    }

    public JoinPacket(@NotNull UUID uuid, String name, Location location) {
        super(UUIDUtil.EMPTY_UUID);
        this.uuid = uuid;
        this.name = name;
        this.location = location;
    }

    @Override
    public void read(@NotNull ByteBuf buffer) {
        this.uuid = readUUID(buffer);
        this.name = readString(buffer);
        this.location = readLocation(buffer);
    }

    @Override
    public void write(@NotNull ByteBuf buffer) {
        buffer.writeByte(PACKET_ID);
        writeUUID(this.uuid, buffer);
        writeString(name, buffer);
        writeLocation(location, buffer);
    }

    public @NotNull UUID getUuid() { return uuid; }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() { return location; }

    public void setLocation(Location location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "JoinPacket{" +
                "sender=" + sender +
                ", uuid=" + uuid +
                ", name=" + name +
                ", location=" + location.toString() +
                '}';
    }
}
