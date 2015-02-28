package org.meveo.api.rest.invoice.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceRs;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class InvoiceRsImpl extends BaseRs implements InvoiceRs {

	@Inject
	private Logger log;

	@Inject
	private InvoiceApi invoiceApi;

	@Override
	public InvoiceCreationResponse create(InvoiceDto invoiceDto) {
		InvoiceCreationResponse result = new InvoiceCreationResponse();

		try {
			String invoiceNumber = invoiceApi.create(invoiceDto, getCurrentUser());
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
			result.setInvoiceNumber(invoiceNumber);
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerInvoicesResponse result = new CustomerInvoicesResponse();

		try {
			result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
