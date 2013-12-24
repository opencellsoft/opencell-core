package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;

import org.meveo.api.OfferPricePlanServiceApi;
import org.meveo.asg.api.service.AsgIdMappingService;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Dec 10, 2013
 **/
@MessageDriven(name = "OfferPricePlanDeletedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/deleteOfferPriceplan"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OfferPricePlanDeletedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(OfferPricePlanDeletedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	@Inject
	private AsgIdMappingService asgIdMappingService;

	@Override
	public void onMessage(Message msg) {
		log.debug("onMessage: {}", msg.toString());

		if (msg instanceof TextMessage) {
			processMessage((TextMessage) msg);
		}

	}

	private void processMessage(TextMessage msg) {
		try {

		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}

}
