package gg.tgb.shardedmc;

import com.rabbitmq.client.DeliverCallback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;


public final class ShardedMC extends JavaPlugin implements Listener {

    private List<FakePlayer> fakePlayers;

    private RabbitMessenger messenger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        fakePlayers = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String recvMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);

            String[] messageData = recvMessage.split(",");

            if(messageData.length > 0) {
                String command = messageData[0];

                switch(command) {
                    case "j":
                        UUID newID = UUID.fromString(messageData[1]);
                        Player tempP = Bukkit.getPlayer(newID);
                        // Only create a Fake Player if they are not actually in this server
                        if(tempP == null || !Objects.requireNonNull(Bukkit.getPlayer(newID)).isOnline()) {

                            String name = messageData[2];
                            double x = Double.parseDouble(messageData[3]);
                            double y = Double.parseDouble(messageData[4]);
                            double z = Double.parseDouble(messageData[5]);
                            float yaw = Float.parseFloat(messageData[6]);
                            float pitch = Float.parseFloat(messageData[7]);
                            Location loc = new Location(getServer().getWorld("world"), x, y, z, yaw, pitch);

                            FakePlayer fp = new FakePlayer(name, newID, loc);
                            fp.setSkin(newID);
                            fp.spawn();
                            fakePlayers.add(fp);

                            Bukkit.getLogger().log(Level.INFO, "Remote player joined: " + newID);
                        }
                        break;
                    case "l":
                        for(int i = 0; i < fakePlayers.size(); i++) {
                            FakePlayer fp = fakePlayers.get(i);
                            if(fp.id.equalsIgnoreCase(messageData[1])) {
                                fp.disconnect();
                                fakePlayers.remove(fp);
                                Bukkit.getLogger().log(Level.INFO, "Remote player left: " + messageData[1]);
                            }
                        }
                        break;
                    case "m":
                        for (FakePlayer fp : fakePlayers) {
                            if (fp.id.equalsIgnoreCase(messageData[1])) {
                                double x = Double.parseDouble(messageData[2]);
                                double y = Double.parseDouble(messageData[3]);
                                double z = Double.parseDouble(messageData[4]);
                                float yaw = Float.parseFloat(messageData[5]);
                                float pitch = Float.parseFloat(messageData[6]);
                                Location loc = new Location(getServer().getWorld("world"), x, y, z, yaw, pitch);
                                fp.move(loc);
                            }
                        }
                    default:
                        break;
                }
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
        Player eP = event.getPlayer();
        for(FakePlayer p: fakePlayers) {
            p.spawnForNewPlayer(eP);
        }
        messenger.sendMessage("j," + eP.getUniqueId()+ "," + eP.getName() + "," + eP.getLocation().getX() + "," + eP.getLocation().getY() + "," + eP.getLocation().getZ() + "," + eP.getLocation().getYaw() + "," + eP.getLocation().getPitch());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player eP = event.getPlayer();
        messenger.sendMessage("l," + eP.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player eP = event.getPlayer();
        messenger.sendMessage("m," + eP.getUniqueId()+ "," + eP.getLocation().getX() + "," + eP.getLocation().getY() + "," + eP.getLocation().getZ() + "," + eP.getLocation().getYaw() + "," + eP.getLocation().getPitch());
    }
}
