package gg.tgb.shardedmc.messages;

import org.bukkit.entity.Player;

public class MoveMessage implements Message {
    private Player player;

    public MoveMessage(Player player) {
        this.player = player;
    }

    public String content() {
        return "m," + player.getUniqueId()+ "," + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
    }
}
