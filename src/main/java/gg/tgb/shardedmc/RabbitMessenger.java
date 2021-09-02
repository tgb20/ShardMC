package gg.tgb.shardedmc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitMessenger {

    private static final String EXCHANGE_NAME = "SHARDMC";

    private Channel channel;
    private Connection connection;

    public RabbitMessenger(DeliverCallback callback) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            channel.basicConsume(queueName, true, callback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
