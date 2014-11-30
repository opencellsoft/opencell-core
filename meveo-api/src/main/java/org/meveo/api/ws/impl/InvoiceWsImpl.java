package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;

import org.meveo.api.InvoiceApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.InvoiceWs;

@WebService(serviceName = "InvoiceWs", endpointInterface = "org.meveo.api.ws.InvoiceWs")
public class InvoiceWsImpl extends BaseWs implements InvoiceWs {

	@Inject
	private InvoiceApi invoiceApi;

	@Override
	public ActionStatus create(InvoiceDto invoiceDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceApi.create(invoiceDto, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public CustomerInvoicesResponse find(
			@QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerInvoicesResponse result = new CustomerInvoicesResponse();

		try {
			result.setCustomerInvoiceDtoList(invoiceApi.list(
					customerAccountCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(
					MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
