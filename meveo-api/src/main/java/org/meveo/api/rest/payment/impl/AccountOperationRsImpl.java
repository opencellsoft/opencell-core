package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.AccountOperationRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccountOperationRsImpl extends BaseRs implements AccountOperationRs {

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
			log.error("error occurred while creating account operation ", e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while creating account operation ", e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public AccountOperationsResponseDto list(String customerAccountCode) {
		AccountOperationsResponseDto result = new AccountOperationsResponseDto();

		try {
			result = accountOperationApi.list(customerAccountCode, getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error occurred while getting list account operation ", e);
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error generated while getting list account operation ", e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus matchOperations(MatchOperationRequestDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountOperationApi.matchOperations(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while matching account operation ", e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while matching account operation ", e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus unMatchingOperations(UnMatchingOperationRequestDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountOperationApi.unMatchingOperations(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while unMatching account operation ", e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while unMatching account operation ", e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}
	
	@Override
	public ActionStatus addLitigation(LitigationRequestDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountOperationApi.addLitigation(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while adding Litigation account operation ", e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while adding Litigation account operation ", e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}
	
	@Override
	public ActionStatus cancelLitigation(LitigationRequestDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountOperationApi.cancelLitigation(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while cancelling litigation account operation ", e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while cancelling Litigation account operation ", e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

}
