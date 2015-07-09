package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.InvoicingApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.InvoicingWs;

/**
 * @author anasseh
 **/
@WebService(serviceName = "InvoicingWs", endpointInterface = "org.meveo.api.ws.InvoicingWs")
@Interceptors({ LoggingInterceptor.class })
public class InvoicingWsImpl extends BaseWs implements InvoicingWs {

	@Inject
	InvoicingApi invoicingApi;

	@Override
	public ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		log.info("createBillingRun request={}",createBillingRunDto);
		try {
			
			invoicingApi.createBillingRun(createBillingRunDto, getCurrentUser());
			
		}catch(MissingParameterException mpe){
			result.setErrorCode(mpe.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(mpe.getMessage());
		}catch (EntityDoesNotExistsException ednep) {
			result.setErrorCode(ednep.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(ednep.getMessage());
		}catch (BusinessApiException bae) {
			result.setErrorCode(bae.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(bae.getMessage());			
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		log.info("createBillingRun Response={}", result);
		return result;
	}

	@Override
	public GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId) {
		GetBillingRunInfoResponseDto result = new GetBillingRunInfoResponseDto();
		log.info("getBillingRunInfo request={}",billingRunId);
		try {
			
			result.setBillingRunDto(invoicingApi.getBillingRunInfo(billingRunId));
			
		}catch(MissingParameterException mpe){
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		}catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		}catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());			
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getBillingRunInfo Response={}", result);
		return result;
	}

	@Override
	public GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId) {
		GetBillingAccountListInRunResponseDto result = new GetBillingAccountListInRunResponseDto();
		log.info("getBillingAccountListInRun request={}",billingRunId);
		try {
			
			result.setBillingAccountsDto(invoicingApi.getBillingAccountListInRun(billingRunId));
			
		}catch(MissingParameterException mpe){
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		}catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		}catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());			
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getBillingAccountListInRun Response={}", result);
		return result;
	}
}
