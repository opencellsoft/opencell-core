package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatusEnum;
import org.meveo.api.CustomerInvoiceApi;
import org.meveo.api.rest.response.CustomerInvoicesResponse;

@Path("/customerInvoice")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CustomerInvoiceWS extends BaseWS {

	@Inject
	private CustomerInvoiceApi customerInvoiceApi;

	@GET
	@Path("/")
	public CustomerInvoicesResponse getInvoiceList(
			@QueryParam("customerAccountCode") String customerAccountCode,
			@QueryParam("providerCode") String providerCode) throws Exception {

		CustomerInvoicesResponse result = new CustomerInvoicesResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerInvoiceDtoList(customerInvoiceApi.getInvoiceList(
					customerAccountCode, providerCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
