package org.meveo.api.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.InvoiceApi;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
@Path("/invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class InvoiceWS {

	@Inject
	private Logger log;

	@Inject
	private ParamBean paramBean;

	@Inject
	private InvoiceApi invoiceApi;

	@POST
	@Path("/")
	public ActionStatus create(InvoiceDto invoiceDto) {
		log.debug("invoice.create={}", invoiceDto);

		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			invoiceDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			invoiceApi.createInvoice(invoiceDto);
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

}
