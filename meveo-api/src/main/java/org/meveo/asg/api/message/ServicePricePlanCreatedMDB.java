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
import org.meveo.api.ServicePricePlanServiceApi;
import org.meveo.api.dto.RecurringChargeDto;
import org.meveo.api.dto.ServicePricePlanDto;
import org.meveo.asg.api.RecurringChargeData;
import org.meveo.asg.api.ServicePricePlanCreated;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Dec 10, 2013
 **/
@MessageDriven(name = "ServicePricePlanCreatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/createServicePriceplan"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ServicePricePlanCreatedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(ServicePricePlanCreatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private ServicePricePlanServiceApi servicePricePlanServiceApi;

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

			ServicePricePlanCreated servicePricePlanCreated = mapper.readValue(
					message, ServicePricePlanCreated.class);

			ServicePricePlanDto servicePricePlanDto = new ServicePricePlanDto();
			servicePricePlanDto.setCurrentUserId(Long.valueOf(paramBean
					.getProperty("asp.api.userId", "1")));
			servicePricePlanDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));

			if (servicePricePlanCreated.getPricePlan() != null) {
				servicePricePlanDto.setServiceId(servicePricePlanCreated
						.getServiceId());
				servicePricePlanDto.setOrganizationId(servicePricePlanCreated
						.getPricePlan().getOrganizationId());
				if (servicePricePlanCreated.getPricePlan().getRecurringCharge() != null) {
					List<RecurringChargeDto> recurringCharges = new ArrayList<RecurringChargeDto>();
					RecurringChargeData source = servicePricePlanCreated
							.getPricePlan().getRecurringCharge();
					RecurringChargeDto recurringChargeDto = new RecurringChargeDto();
				}
				if (servicePricePlanCreated.getPricePlan().getUsageCharge() != null) {

				}
				if (servicePricePlanCreated.getPricePlan().getSubscriptionFee() != null) {

				}
				if (servicePricePlanCreated.getPricePlan().getTerminationFee() != null) {

				}
			}

			servicePricePlanServiceApi.create(servicePricePlanDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}
}
