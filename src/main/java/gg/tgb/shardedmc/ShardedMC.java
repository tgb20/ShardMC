package gg.tgb.shardedmc;

import com.rabbitmq.client.DeliverCallback;
import gg.tgb.shardedmc.packets.events.*;
import gg.tgb.shardedmc.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;


public final class ShardedMC extends JavaPlugin implements Listener {

    private List<RemotePlayer> remotePlayers;

    private RabbitMessenger messenger;

    protected static final ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        remotePlayers = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            ByteBuf nd = alloc.buffer(delivery.getBody().length, delivery.getBody().length);
            nd.writeBytes(delivery.getBody());

            int packetId = nd.readByte();
            switch (packetId) {
                case 0:
                    JoinPacket joinPacket = new JoinPacket();
                    joinPacket.read(nd);

                    Player tempP = Bukkit.getPlayer(joinPacket.getUuid());
                    if(tempP == null || !Objects.requireNonNull(Bukkit.getPlayer(joinPacket.getUuid())).isOnline()) {
                        RemotePlayer rp = new RemotePlayer(joinPacket.getName(), joinPacket.getUuid(), joinPacket.getLocation());
                        rp.setSkin(joinPacket.getUuid());
                        rp.spawn();
                        remotePlayers.add(rp);

                        Bukkit.getLogger().log(Level.INFO, "Remote player joined: " + joinPacket.getUuid());
                    }
                    break;
                case 1:
                    LeavePacket leavePacket = new LeavePacket();
                    leavePacket.read(nd);
                    for(int i = 0; i < remotePlayers.size(); i++) {
                        RemotePlayer rp = remotePlayers.get(i);
                        if(rp.id.equalsIgnoreCase(leavePacket.getUuid().toString())) {
                            rp.disconnect();
                            remotePlayers.remove(rp);
                            Bukkit.getLogger().log(Level.INFO, "Remote player left: " + leavePacket.getUuid());
                        }
                    }
                    break;
                case 2:
                    MovePacket movePacket = new MovePacket();
                    movePacket.read(nd);
                    for (RemotePlayer rp : remotePlayers) {
                        if (rp.id.equalsIgnoreCase(movePacket.getUuid().toString())) {
                            rp.move(movePacket.getLocation());
                        }
                    }
                    break;
                case 3:
                    BlockPlacePacket blockPlacePacket = new BlockPlacePacket();
                    blockPlacePacket.read(nd);
                    Bukkit.getScheduler().runTask(this, new Runnable() {
                        @Override
                        public void run() {
                            blockPlacePacket.getLocation().getBlock().setType(Material.valueOf(blockPlacePacket.getType()));
                        }
                    });
                    break;
                case 4:
                    BlockBreakPacket blockBreakPacket = new BlockBreakPacket();
                    blockBreakPacket.read(nd);
                    Bukkit.getScheduler().runTask(this, new Runnable() {
                        @Override
                        public void run() {
                            blockBreakPacket.getLocation().getBlock().setType(Material.AIR);
                        }
                    });
                    break;
                default:
                    Bukkit.getLogger().log(Level.INFO, "Unknown packet with ID: " + packetId);
                    break;
            }
        };

        messenger = new RabbitMessenger(deliverCallback);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        messenger.close();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for(RemotePlayer rp: remotePlayers) {
            rp.spawnForNewPlayer(p);
        }

        JoinPacket packet = new JoinPacket();
        packet.setUuid(p.getUniqueId());
        packet.setName(p.getName());
        packet.setLocation(p.getLocation());

        messenger.sendMessage(PacketUtil.packetToByteArray(packet, packet.LENGTH));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        LeavePacket packet = new LeavePacket();
        packet.setUuid(p.getUniqueId());

        messenger.sendMessage(PacketUtil.packetToByteArray(packet, packet.LENGTH));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        MovePacket packet = new MovePacket();
        packet.setUuid(p.getUniqueId());
        packet.setLocation(p.getLocation());

        messenger.sendMessage(PacketUtil.packetToByteArray(packet, packet.LENGTH));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockPlacePacket packet = new BlockPlacePacket();
        packet.setType(event.getBlock().getType().name());
        packet.setLocation(event.getBlock().getLocation());

        messenger.sendMessage(PacketUtil.packetToByteArray(packet, packet.LENGTH));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        BlockBreakPacket packet = new BlockBreakPacket();
        packet.setLocation(event.getBlock().getLocation());

        messenger.sendMessage(PacketUtil.packetToByteArray(packet, packet.LENGTH));
    }
}
