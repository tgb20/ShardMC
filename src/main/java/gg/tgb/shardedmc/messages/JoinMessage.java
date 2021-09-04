package gg.tgb.shardedmc.messages;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JoinMessage implements Message {

    private Player player;

    public JoinMessage(Player player) {
        this.player = player;
    }

    public String content() {
        return "j," + player.getUniqueId()+ "," + player.getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
    }
}
