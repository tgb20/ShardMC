package gg.tgb.shardedmc;

import com.rabbitmq.client.DeliverCallback;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public final class ShardedMC extends JavaPlugin implements CommandExecutor, Listener {

    private List<FakePlayer> fakePlayers;

    private RabbitMessenger messenger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("createnpc").setExecutor(this);
        this.getCommand("sendmessage").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        fakePlayers = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String recvMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Bukkit.getLogger().log(Level.INFO, " [x] Received '" + recvMessage + "'");
        };

        messenger = new RabbitMessenger(deliverCallback);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown login
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("sendmessage")) {
            messenger.sendMessage(args[0]);
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            FakePlayer fp = new FakePlayer(player.getName(), UUID.randomUUID(), player.getLocation());
            fp.setSkin(player.getUniqueId());
            fp.spawn();
            fakePlayers.add(fp);
            return true;
        }
        return false;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(FakePlayer p: fakePlayers) {
            p.spawnForNewPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        for(FakePlayer p: fakePlayers) {
            p.move(event.getPlayer().getLocation());
        }
    }
}
