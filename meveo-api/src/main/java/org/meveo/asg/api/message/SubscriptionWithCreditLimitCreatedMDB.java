package org.meveo.asg.api.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.meveo.api.SubscriptionApiStatusEnum;
import org.meveo.api.SubscriptionWithCreditLimitServiceApi;
import org.meveo.api.dto.CreditLimitDto;
import org.meveo.api.dto.ServiceToAddDto;
import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.api.util.RabbitUtil;
import org.meveo.asg.api.CreateOrganizationSubscriptionWithCreditLimitRequest;
import org.meveo.asg.api.CreateOrganizationSubscriptionWithCreditLimitResponse;
import org.meveo.asg.api.OrganizationCreditLimit;
import org.meveo.asg.api.ServiceSubscriptionDate;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.asg.api.service.AsgIdMappingService;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.api.response.SubscriptionWithCreditLimitResponse;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@MessageDriven(name = "SubscriptionWithCreditLimitCreatedMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/createSubscriptionWithCreditLimit"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SubscriptionWithCreditLimitCreatedMDB implements MessageListener {

	private static Logger log = LoggerFactory
			.getLogger(SubscriptionWithCreditLimitCreatedMDB.class);

	private static final String RESPONSE_EXCHANGE_NAME = "ServMan.Messages.Commands.Billing:CreateOrganizationSubscriptionWithCreditLimitResponse";

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	@Inject
	private AsgIdMappingService asgIdMappingService;

	@Inject
	private SubscriptionWithCreditLimitServiceApi subscriptionWithCreditLimitServiceApi;

	@Override
	public void onMessage(Message msg) {
		log.debug("onMessage: {}", msg.toString());

		if (msg instanceof TextMessage) {
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

	}

	private void processMessage(TextMessage msg)
			throws JsonGenerationException, JsonMappingException, IOException {
		CreateOrganizationSubscriptionWithCreditLimitResponse asgResponse = new CreateOrganizationSubscriptionWithCreditLimitResponse();
		ObjectMapper mapper = new ObjectMapper();

		try {
			String message = msg.getText();

			CreateOrganizationSubscriptionWithCreditLimitRequest data = mapper
					.readValue(
							message,
							CreateOrganizationSubscriptionWithCreditLimitRequest.class);

			asgResponse.setRequestId(data.getRequestId());

			SubscriptionWithCreditLimitDto subscriptionWithCreditLimitDto = new SubscriptionWithCreditLimitDto();
			subscriptionWithCreditLimitDto.setCurrentUserId(Long
					.valueOf(paramBean.getProperty("asp.api.userId", "1")));
			subscriptionWithCreditLimitDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));
			subscriptionWithCreditLimitDto.setRequestId(data.getRequestId());
			subscriptionWithCreditLimitDto
					.setOrganizationId(asgIdMappingService.getMeveoCode(em,
							data.getOrganizationId(),
							EntityCodeEnum.ORGANIZATION));
			subscriptionWithCreditLimitDto.setOfferId(asgIdMappingService
					.getMeveoCode(em, data.getOfferId(), EntityCodeEnum.OFFER));
			if (data.getSubscriptionDate() != null) {
				subscriptionWithCreditLimitDto.setSubscriptionDate(data
						.getSubscriptionDate().toGregorianCalendar().getTime());
			}
			if (data.getServices() != null
					&& data.getServices().getServiceSubscriptionDate() != null) {
				List<ServiceToAddDto> servicesToAdd = new ArrayList<ServiceToAddDto>();
				for (ServiceSubscriptionDate serviceSubscriptionDate : data
						.getServices().getServiceSubscriptionDate()) {
					ServiceToAddDto serviceToAddDto = new ServiceToAddDto();
					serviceToAddDto.setServiceId(asgIdMappingService
							.getMeveoCode(em,
									serviceSubscriptionDate.getServiceId(),
									EntityCodeEnum.SERVICE));
					if (serviceSubscriptionDate.getSubscriptionDate() != null) {
						serviceToAddDto
								.setSubscriptionDate(serviceSubscriptionDate
										.getSubscriptionDate()
										.toGregorianCalendar().getTime());
					}

					servicesToAdd.add(serviceToAddDto);
				}

				subscriptionWithCreditLimitDto.setServicesToAdd(servicesToAdd);
			}

			if (data.getCreditLimits() != null
					&& data.getCreditLimits().getOrganizationCreditLimit() != null) {
				List<CreditLimitDto> creditLimits = new ArrayList<CreditLimitDto>();
				for (OrganizationCreditLimit organizationCreditLimit : data
						.getCreditLimits().getOrganizationCreditLimit()) {
					CreditLimitDto creditLimitDto = new CreditLimitDto();
					creditLimitDto
							.setOrganizationId(asgIdMappingService
									.getMeveoCode(em, organizationCreditLimit
											.getOrganizationId(),
											EntityCodeEnum.ORGANIZATION));
					creditLimitDto.setCreditLimit(organizationCreditLimit
							.getCreditLimit());

					creditLimits.add(creditLimitDto);
				}

				subscriptionWithCreditLimitDto.setCreditLimits(creditLimits);
			}

			SubscriptionWithCreditLimitResponse response = subscriptionWithCreditLimitServiceApi
					.create(subscriptionWithCreditLimitDto);

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
