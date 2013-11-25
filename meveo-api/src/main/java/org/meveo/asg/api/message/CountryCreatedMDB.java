package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.CountryServiceApi;
import org.meveo.api.dto.CountryDto;
import org.meveo.asg.api.CountryCreated;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Nov 4, 2013
 **/
@MessageDriven(name = "CountryCreatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/createCountry"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class CountryCreatedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(CountryCreatedMDB.class);

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private CountryServiceApi countryServiceApi;

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

			CountryCreated data = mapper.readValue(message,
					CountryCreated.class);

			log.debug("Creating country with code={}", data.getCountry()
					.getCountryId());
			
			CountryDto countryDto = new CountryDto();
			countryDto.setCountryCode(data.getCountry().getCountryId());
			countryDto.setName(data.getCountry().getName());
			countryDto.setCurrencyCode(data.getCountry().getCurrencyCode());
			countryDto.setCurrentUserId(Long
					.valueOf(paramBean.getProperty("asp.api.userId", "1")));
			countryDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));
			
			countryServiceApi.create(countryDto);
		} catch (Exception e) {
			log.error("Error processing ASG message: {}", e.getMessage());
		}
	}
}
