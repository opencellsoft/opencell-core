package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.InvoiceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author R.AITYAAZZA
 * 
 */
@Path("/invoice")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@WSSecured
public class InvoiceWs extends BaseWs {

	@Inject
	private InvoiceApi invoiceApi;

	@POST
	@Path("/create")
	public ActionStatus create(InvoiceDto invoiceDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceApi.createInvoice(invoiceDto, getCurrentUser());
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	@GET
	@Path("/customerInvoice")
	public CustomerInvoicesResponse getInvoiceList(
			@QueryParam("customerAccountCode") String customerAccountCode)
			throws Exception {

		CustomerInvoicesResponse result = new CustomerInvoicesResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerInvoiceDtoList(invoiceApi.getInvoiceList(
					customerAccountCode, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}
}
