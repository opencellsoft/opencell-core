package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.OrganizationServiceApi;
import org.meveo.api.dto.OrganizationDto;
import org.meveo.asg.api.OrganizationCreated;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Nov 4, 2013
 **/
@MessageDriven(name = "OrganizationCreatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/createOrganization"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OrganizationCreatedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(OrganizationCreatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OrganizationServiceApi organizationServiceApi;

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

			OrganizationCreated data = mapper.readValue(message,
					OrganizationCreated.class);

			log.debug("Creating organization with code={}", data
					.getOrganization().getCountryId());

			OrganizationDto organizationDto = new OrganizationDto();
			organizationDto.setOrganizationId(data.getOrganization()
					.getOrganizationId());
			organizationDto.setName(data.getOrganization().getNames()
					.getItemNameData().get(0).toString());
			organizationDto.setParentId(data.getOrganization().getParentId());
			organizationDto.setCountryCode(data.getOrganization()
					.getCountryId());
			organizationDto.setDefaultCurrencyCode(data.getOrganization()
					.getDefaultCurrencyCode());
			organizationDto.setCurrentUserId(Long.valueOf(paramBean
					.getProperty("asp.api.userId", "1")));
			organizationDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			organizationServiceApi.create(organizationDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}
}
