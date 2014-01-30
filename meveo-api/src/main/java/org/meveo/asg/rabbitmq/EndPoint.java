package org.meveo.asg.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Edward P. Legaspi
 **/
public abstract class EndPoint {

	protected Channel channel;
	protected Connection connection;
	protected String queueName;
	private String exchangeName;
	private String server;

	public EndPoint(String server, String exchangeName, String queueName)
			throws IOException {
		this.server = server;
		this.exchangeName = exchangeName;
		this.queueName = queueName;
		open();
	}

	public void open() throws IOException {
		// Create a connection factory
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(server);

		// getting a connection
		connection = factory.newConnection();

		// creating a channel
		channel = connection.createChannel();

		// declaring a queue for this channel. If queue does not exist,
		// it will be created on the server.
		channel.queueDeclare(queueName, true, false, false, null);
		channel.exchangeDeclare(exchangeName, "fanout", true);
		channel.queueBind(queueName, exchangeName, "");
	}

	/**
	 * Close channel and connection. Not necessary as it happens implicitly any
	 * way.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.channel.close();
		this.connection.close();
	}

}
