package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.ServicePricePlanServiceApi;
import org.meveo.asg.api.ServicePricePlanDeleted;
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
@MessageDriven(name = "ServicePricePlanDeletedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/deleteServicePriceplan"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ServicePricePlanDeletedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(ServicePricePlanDeletedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	@Inject
	private AsgIdMappingService asgIdMappingService;

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

			ServicePricePlanDeleted data = mapper.readValue(message,
					ServicePricePlanDeleted.class);

			servicePricePlanServiceApi.remove(asgIdMappingService.getMeveoCode(
					em, data.getServiceId(), EntityCodeEnum.OPF),
					asgIdMappingService.getMeveoCode(em,
							data.getOrganizationId(),
							EntityCodeEnum.ORG), Long
							.parseLong(paramBean.getProperty(
									"asp.api.providerId", "1")), Long
							.parseLong(paramBean.getProperty("asp.api.userId",
									"1")));
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}

}
