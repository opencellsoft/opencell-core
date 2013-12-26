package org.meveo.api.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * @author Edward P. Legaspi
 **/
public class RabbitUtil {

	private static Logger log = LoggerFactory.getLogger(RabbitUtil.class);

	public static void sendMessage(String host, String exchangeName,
			String queueName, String message) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(exchangeName, "fanout");
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, "");

		channel.basicPublish(exchangeName, "",
				MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		log.debug(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

}
