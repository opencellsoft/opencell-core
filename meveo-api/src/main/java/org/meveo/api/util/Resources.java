package org.meveo.api.util;

import javax.annotation.Resource;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * @author Edward P. Legaspi
 * @since Oct 29, 2013
 **/
public class Resources {

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "queue/meveoApi")
	private Queue meveoApiQueue;

	@Produces
	public Connection createConnection() throws JMSException {
		return connectionFactory.createConnection();
	}

	public void closeConnection(@Disposes Connection conn) throws JMSException {
		conn.close();
	}

	@Produces
	public Queue getTestQueue() {
		return meveoApiQueue;
	}

}
