package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.ServiceTemplateServiceApi;
import org.meveo.api.dto.ServiceDto;
import org.meveo.asg.api.ServiceCreated;
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
@MessageDriven(name = "ServiceUpdatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/updateService"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ServiceUpdatedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(ServiceUpdatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateServiceApi serviceTemplateServiceApi;

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

			ServiceCreated data = mapper.readValue(message,
					ServiceCreated.class);

			ServiceDto serviceDto = new ServiceDto();
			serviceDto.setServiceId(asgIdMappingService.getMeveoCode(em, data
					.getService().getServiceId(), EntityCodeEnum.S));
			serviceDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			serviceDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			serviceTemplateServiceApi.update(serviceDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}

}
