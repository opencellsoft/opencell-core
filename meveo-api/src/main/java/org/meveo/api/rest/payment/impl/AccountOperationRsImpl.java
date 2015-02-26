package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.AccountOperationRs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class AccountOperationRsImpl extends BaseRs implements AccountOperationRs {

	@Inject
	private Logger log;

	@Inject
	private AccountOperationApi accountOperationApi;

	@Override
	public ActionStatus create(AccountOperationDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountOperationApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
