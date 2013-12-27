package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.OfferTemplateServiceApi;
import org.meveo.api.dto.OfferDto;
import org.meveo.asg.api.OfferUpdated;
import org.meveo.asg.api.model.EntityCodeEnum;
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
@MessageDriven(name = "OfferUpdatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/updateOffer"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OfferUpdatedMDB implements MessageListener {

	private static Logger log = LoggerFactory.getLogger(OfferUpdatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OfferTemplateServiceApi offerTemplateServiceApi;

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
			String message = msg.getText();
			ObjectMapper mapper = new ObjectMapper();

			OfferUpdated data = mapper.readValue(message, OfferUpdated.class);

			OfferDto offerDto = new OfferDto();
			offerDto.setOfferId(asgIdMappingService.getMeveoCode(em, data
					.getOffer().getOfferId(), EntityCodeEnum.O));
			offerDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			offerDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			offerTemplateServiceApi.update(offerDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}

}
