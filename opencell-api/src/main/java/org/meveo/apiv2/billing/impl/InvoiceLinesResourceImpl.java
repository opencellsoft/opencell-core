package org.meveo.apiv2.billing.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.billing.*;
import org.meveo.apiv2.billing.resource.InvoiceLinesResource;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.billing.service.InvoiceLinesApiService;
import org.meveo.service.billing.impl.InvoiceLineService;

public class InvoiceLinesResourceImpl implements InvoiceLinesResource {

    @Inject
    private InvoiceLinesApiService invoiceLinesApiService;
    
    @Inject
	private InvoiceApiService invoiceApiService;
	
    @Inject
    private InvoiceLineService invoiceLinesService;

    @Override
    public Response getTaxDetails(Long invoiceLineId, Request request) {
        return Response
                .ok(invoiceLinesApiService.getTaxDetails(invoiceLineId))
                .build();
    }
    
    @Override
    public Response markForAdjustment() {
        //Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
       // Invoice adjInvoice = invoiceApiService.createAdjustment(invoice, invoiceLinesToMark);
    	//invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesToMark.);
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
       // response.put("invoice", invoice);
        return Response.ok(response).build();
    }
}