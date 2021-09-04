package gg.tgb.shardedmc.messages;

import org.bukkit.entity.Player;

public class LeaveMessage implements Message {
    private Player player;

    public LeaveMessage(Player player) {
        this.player = player;
    }

    public String content() {
        return "l," + player.getUniqueId();
    }
}
