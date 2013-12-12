package org.meveo.asg.api.message;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.OfferTemplateServiceApi;
import org.meveo.api.dto.OfferDto;
import org.meveo.asg.api.OfferCreated;
import org.meveo.asg.api.ServiceName;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Dec 10, 2013
 **/
@MessageDriven(name = "OfferCreatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/createOffer"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OfferCreatedMDB implements MessageListener {

	private static Logger log = LoggerFactory.getLogger(OfferCreatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OfferTemplateServiceApi offerTemplateServiceApi;

	@Override
	public void onMessage(Message msg) {
		log.debug("onMessage: {}", msg.toString());

		if (msg instanceof TextMessage) {
			processMessage((TextMessage) msg);
		}

	}

	private void processMessage(TextMessage msg) {
		try {
			String message = msg.getText();
			ObjectMapper mapper = new ObjectMapper();

			OfferCreated data = mapper.readValue(message, OfferCreated.class);

			OfferDto offerDto = new OfferDto();
			offerDto.setOfferId(data.getOffer().getOfferId());

			if (data.getOffer().getServices() != null
					&& data.getOffer().getServices().getServices() != null
					&& data.getOffer().getServices().getServices().size() > 0) {
				List<String> services = new ArrayList<String>();
				for (ServiceName serviceName : data.getOffer().getServices()
						.getServices()) {
					services.add(serviceName.getName());
				}
				offerDto.setServices(services);
			}

			offerDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			offerDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			offerTemplateServiceApi.create(offerDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}

}
