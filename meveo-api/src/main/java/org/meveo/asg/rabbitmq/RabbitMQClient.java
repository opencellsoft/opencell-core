package org.meveo.asg.rabbitmq;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@Startup
@Singleton
public class RabbitMQClient {

	private static Logger log = LoggerFactory.getLogger(RabbitMQClient.class);

	@PostConstruct
	public void init() {
		log.debug("Starting MEVEO-Rabbit Client application");
		log.debug("Developed by Manaty");
		log.debug("2014");
		log.debug("=========================================");
		start();
	}

	public void start() {
		try {
			// country
			Thread countryCreatedThread = new Thread(new QueueConsumer("",
					"ServMan.Messages.Events:CountryCreated",
					"Meveo.CountryCreated"));
			countryCreatedThread.start();

			Thread coutryUpdated = new Thread(new QueueConsumer("",
					"ServMan.Messages.Events:CountryUpdated",
					"Meveo.CountryUpdated"));
			coutryUpdated.start();

			Thread countryDeleted = new Thread(new QueueConsumer("",
					"ServMan.Messages.Events:CountryDeleted",
					"Meveo.CountryDeleted"));
			countryDeleted.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}