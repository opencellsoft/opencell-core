package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.response.payment.ListAccountOperationsResponseDto;
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
			log.error("error occurred while creating account operation ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while creating account operation ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ListAccountOperationsResponseDto list(String customerAccountCode) {
		ListAccountOperationsResponseDto result = new ListAccountOperationsResponseDto();

		try {
			result = accountOperationApi.list(customerAccountCode, getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error occurred while getting list account operation ",e);
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error generated while getting list account operation ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
