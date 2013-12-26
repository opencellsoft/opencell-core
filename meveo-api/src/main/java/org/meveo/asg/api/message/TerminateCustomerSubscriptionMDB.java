package org.meveo.asg.api.message;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.meveo.api.CustomerSubscriptionWithCreditLimitServiceApi;
import org.meveo.api.SubscriptionApiStatusEnum;
import org.meveo.api.dto.TerminateCustomerSubscriptionDto;
import org.meveo.api.util.RabbitUtil;
import org.meveo.asg.api.TerminateUserSubscriptionRequest;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.api.response.TerminateCustomerSubscriptionResponse;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@MessageDriven(name = "TerminateCustomerSubscriptionMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/terminateCustomerSubscription"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TerminateCustomerSubscriptionMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(TerminateCustomerSubscriptionMDB.class);

	private static final String RESPONSE_EXCHANGE_NAME = "ServMan.Messages.Commands.Billing:TerminateUserSubscriptionRequest";

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	@Inject
	private CustomerSubscriptionWithCreditLimitServiceApi customerSubscriptionWithCreditLimitServiceApi;

	@Override
	public void onMessage(Message msg) {
		log.debug("onMessage: {}", msg.toString());
		try {
			processMessage((TextMessage) msg);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processMessage(TextMessage msg)
			throws JsonGenerationException, JsonMappingException, IOException {
		TerminateCustomerSubscriptionResponse asgResponse = new TerminateCustomerSubscriptionResponse();
		ObjectMapper mapper = new ObjectMapper();

		try {
			String message = msg.getText();

			TerminateUserSubscriptionRequest data = mapper.readValue(message,
					TerminateUserSubscriptionRequest.class);

			TerminateCustomerSubscriptionDto terminateCustomerSubscriptionDto = new TerminateCustomerSubscriptionDto();
			terminateCustomerSubscriptionDto.setCurrentUserId(Long
					.valueOf(paramBean.getProperty("asp.api.userId", "1")));
			terminateCustomerSubscriptionDto.setProviderId(Long
					.valueOf(paramBean.getProperty("asp.api.providerId", "1")));
			terminateCustomerSubscriptionDto.setRequestId(data.getRequestId());
			terminateCustomerSubscriptionDto.setSubscriptionId(data
					.getSubscriptionId());

			TerminateCustomerSubscriptionResponse response = customerSubscriptionWithCreditLimitServiceApi
					.terminateSubscription(terminateCustomerSubscriptionDto);

			asgResponse.setAccepted(response.getAccepted());
			asgResponse.setStatus(response.getStatus());
			asgResponse.setSubscriptionId(response.getSubscriptionId());
		} catch (Exception e) {
			asgResponse.setAccepted(false);
			asgResponse.setStatus(SubscriptionApiStatusEnum.FAIL.name());
			log.error("Error processing ASG message: {}", e.getMessage());
		}

		RabbitUtil
				.sendMessage(paramBean.getProperty("asg.api.response.host",
						"192.168.0.116"), RESPONSE_EXCHANGE_NAME, paramBean
						.getProperty("asg.api.response.queue", "Meveo"), mapper
						.writeValueAsString(asgResponse));
	}

}
