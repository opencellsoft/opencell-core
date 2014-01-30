package org.meveo.asg.rabbitmq;

import java.io.IOException;

import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author Edward P. Legaspi
 **/
public class QueueConsumer extends EndPoint implements Runnable, Consumer {

	private static Logger log = LoggerFactory.getLogger(QueueConsumer.class);

	private ParamBean paramBean = ParamBean.getInstance();

	public QueueConsumer(String server, String exchangeName, String queueName)
			throws IOException {
		super(server, exchangeName, queueName);
	}

	@Override
	public void run() {
		try {
			// start consuming messages. Don't auto-acknowledge the messages.
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		log.debug("handleConsumeOk " + consumerTag);
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		log.debug("handleCancelOk " + consumerTag);
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		log.debug("handleCancel " + consumerTag);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		log.debug("handleDelivery " + consumerTag);

		long deliveryTag = envelope.getDeliveryTag();

		// (process the message components here ...)
		channel.basicAck(deliveryTag, false);
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		log.debug("handleShutdownSignal " + consumerTag);

		try {
			Thread.sleep(Long.valueOf(paramBean.getProperty(
					"rabbitmq.handleShutdownSignalDelay", "300000")));
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}

		try {
			open();
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		log.debug("handleRecoverOk " + consumerTag);
	}

}
