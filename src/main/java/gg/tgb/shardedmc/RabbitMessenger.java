package gg.tgb.shardedmc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class RabbitMessenger {

    private Channel channel;
    private String queueName;

    public RabbitMessenger(DeliverCallback callback) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare("SHARDMC", "fanout");

            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "SHARDMC", "");

            channel.basicConsume(queueName, true, callback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            channel.basicPublish("SHARDMC", "", null, message.getBytes());
            Bukkit.getLogger().log(Level.INFO, " [x] Sent '" + message + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
