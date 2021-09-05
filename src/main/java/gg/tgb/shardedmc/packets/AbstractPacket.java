package gg.tgb.shardedmc.packets;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractPacket implements Packet {

    protected final UUID sender;

    protected AbstractPacket(@NotNull UUID sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull UUID getSender() { return sender; }

    protected final int readVarInt(@NotNull ByteBuf input) { return readVarInt(input, 5); }

    protected final int readVarInt(@NotNull ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        do {
            in = input.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    protected final void writeVarInt(int value, @NotNull ByteBuf output) {
        int part;
        do {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            output.writeByte((byte) part);
        } while (value != 0);
    }

    protected final int readVarShort(@NotNull ByteBuf buf) {
        int low = buf.readShort() & 0xFFFF; // convert to unsigned
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readByte() & 0xFF; // convert to unsigned
        }
        return ((high & 0xFF) << 15) | low;
    }

    protected final void writeVarShort(int toWrite, @NotNull ByteBuf buf) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort((short) low);
        if (high != 0) {
            buf.writeByte((byte) high);
        }
    }

    protected final @NotNull UUID readUUID(@NotNull ByteBuf input) { return new UUID(input.readLong(), input.readLong()); }

    protected final void writeUUID(@NotNull UUID value, @NotNull ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

    protected final @NotNull Location readLocation(@NotNull ByteBuf input) {
        return new Location(Bukkit.getWorld("world"), input.readDouble(), input.readDouble(), input.readDouble(), input.readFloat(), input.readFloat());
    }

    protected final void writeLocation(@NotNull Location location, @NotNull ByteBuf output) {
        output.writeDouble(location.getX());
        output.writeDouble(location.getY());
        output.writeDouble(location.getZ());
        output.writeFloat(location.getYaw());
        output.writeFloat(location.getPitch());
    }

    protected final @NotNull String readString(@NotNull ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            throw new RuntimeException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, StandardCharsets.UTF_8);
    }

    protected final void writeString(@NotNull String s, @NotNull ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new RuntimeException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        }

        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    @Override
    public boolean verifyFullRead(@NotNull ByteBuf buffer) {
        if (buffer.readableBytes() > 0) {
            Bukkit.getLogger().warning(buffer.readableBytes() + " bytes remain in the packet ByteBuf after being parsed.");
            return false;
        }
        return true;
    }
}