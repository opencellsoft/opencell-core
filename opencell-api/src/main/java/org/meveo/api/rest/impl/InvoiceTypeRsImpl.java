package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.InvoiceTypeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;
import org.meveo.api.dto.response.GetInvoiceTypesResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.InvoiceTypeRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceTypeRsImpl extends BaseRs implements InvoiceTypeRs {

    @Inject
    private InvoiceTypeApi invoiceTypeApi;
    
	@Override
	public ActionStatus create(InvoiceTypeDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceTypeApi.create(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public ActionStatus update(InvoiceTypeDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceTypeApi.update(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceTypeResponse find(String invoiceTypeCode) {
		GetInvoiceTypeResponse result = new GetInvoiceTypeResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceTypeDto(invoiceTypeApi.find(invoiceTypeCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
	    return result;
	}

	@Override
	public ActionStatus remove(String invoiceTypeCode) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceTypeApi.remove(invoiceTypeCode);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public ActionStatus createOrUpdate(InvoiceTypeDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceTypeApi.createOrUpdate(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceTypesResponse list() {
		GetInvoiceTypesResponse result = new GetInvoiceTypesResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceTypesDto(invoiceTypeApi.list());
		} catch (Exception e) {
            processException(e, result.getActionStatus());
		}
	    return result;
	}
}