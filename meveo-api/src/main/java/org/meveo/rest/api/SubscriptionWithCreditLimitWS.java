package org.meveo.rest.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.SubscriptionWithCreditLimitServiceApi;
import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.api.exception.CreditLimitExceededException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.ParentSellerDoesNotExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
import org.meveo.api.exception.ServiceTemplateDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.api.response.SubscriptionWithCreditLimitResponse;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@Stateless
@Path("/subscriptionWithCreditLimit")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class SubscriptionWithCreditLimitWS {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private Logger log;

	@Inject
	private SubscriptionWithCreditLimitServiceApi subscriptionWithCreditLimitServiceApi;

	@POST
	@Path("/")
	public SubscriptionWithCreditLimitResponse create(
			SubscriptionWithCreditLimitDto subscriptionDto) {
		SubscriptionWithCreditLimitResponse result = new SubscriptionWithCreditLimitResponse();

		subscriptionDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
				"asp.api.userId", "1")));
		subscriptionDto.setProviderId(Long.valueOf(paramBean.getProperty(
				"asp.api.providerId", "1")));

		try {
			subscriptionWithCreditLimitServiceApi.create(subscriptionDto);
		} catch (CreditLimitExceededException e) {
		} catch (SellerDoesNotExistsException e) {
		} catch (ParentSellerDoesNotExistsException e) {
		} catch (ServiceTemplateDoesNotExistsException e) {

		} catch (MeveoApiException e) {
			log.error(e.getMessage());
		} catch (BusinessException e) {
			e.printStackTrace();
		}

		return result;
	}
}
