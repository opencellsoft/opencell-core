package org.meveo.apiv2.billing.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.InvoiceLinesToMarkAdjustment;
import org.meveo.apiv2.billing.resource.InvoiceLinesResource;
import org.meveo.apiv2.billing.service.InvoiceLinesApiService;

@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceLinesResourceImpl implements InvoiceLinesResource {

    @Inject
    private InvoiceLinesApiService invoiceLinesApiService;
    

    @Override
    public Response getTaxDetails(Long invoiceLineId, Request request) {
        return Response
                .ok(invoiceLinesApiService.getTaxDetails(invoiceLineId))
                .build();
    }
    
    @Override
    public Response markForAdjustment(@NotNull InvoiceLinesToMarkAdjustment invoiceLinesToMark) {
    	List<Long> invoiceLineIds = invoiceLinesApiService.getInvoiceLineIds(invoiceLinesToMark);
    	int nbInvoiceLinesUnmarked = 0;
    	if(!CollectionUtils.isEmpty(invoiceLineIds))
    		nbInvoiceLinesUnmarked = invoiceLinesApiService.markInvoiceLinesForAdjustment(invoiceLinesToMark.getIgnoreInvalidStatuses(), invoiceLineIds);
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("message", nbInvoiceLinesUnmarked+" new invoiceLine(s) marked TO_ADJUST");
        return Response.ok(response).build();
    }
    
    @Override
    public Response unmarkForAdjustment(@NotNull InvoiceLinesToMarkAdjustment invoiceLinesToUnmark) {   	
    	List<Long> invoiceLineIds = invoiceLinesApiService.getInvoiceLineIds(invoiceLinesToUnmark);   	
    	int nbInvoiceLinesUnmarked = 0;
    	if(!CollectionUtils.isEmpty(invoiceLineIds))
    		nbInvoiceLinesUnmarked = invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(invoiceLinesToUnmark.getIgnoreInvalidStatuses(), invoiceLineIds);    	
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("message", nbInvoiceLinesUnmarked+" new invoiceLine(s) marked NOT_ADJUSTED");
        return Response.ok(response).build();
    }


}