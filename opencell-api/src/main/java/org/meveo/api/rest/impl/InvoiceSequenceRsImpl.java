package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.InvoiceSequenceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import org.meveo.api.dto.response.GetInvoiceSequencesResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.InvoiceSequenceRs;

/**
 * @author akadid abdelmounaim
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceSequenceRsImpl extends BaseRs implements InvoiceSequenceRs {

    @Inject
    private InvoiceSequenceApi invoiceSequenceApi;
    
	@Override
	public ActionStatus create(InvoiceSequenceDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.create(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public ActionStatus update(InvoiceSequenceDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.update(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceSequenceResponse find(String invoiceSequenceCode) {
	    GetInvoiceSequenceResponse result = new GetInvoiceSequenceResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceSequenceDto(invoiceSequenceApi.find(invoiceSequenceCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
	    return result;
	}

	@Override
	public ActionStatus createOrUpdate(InvoiceSequenceDto invoiceSequenceDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.createOrUpdate(invoiceSequenceDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceSequencesResponse list() {
	    GetInvoiceSequencesResponse result = new GetInvoiceSequencesResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceSequencesDto(invoiceSequenceApi.list());
		} catch (Exception e) {
            processException(e, result.getActionStatus());
		}
	    return result;
	}
}