package org.meveo.apiv2.billing.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.billing.InvoiceLinesToMark;
import org.meveo.apiv2.billing.resource.InvoiceLinesResource;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.billing.service.InvoiceLinesApiService;
import org.meveo.model.billing.Invoice;

public class InvoiceLinesResourceImpl implements InvoiceLinesResource {

    @Inject
    private InvoiceLinesApiService invoiceLinesApiService;
    
    @Inject
	private InvoiceApiService invoiceApiService;

    @Override
    public Response getTaxDetails(Long invoiceLineId, Request request) {
        return Response
                .ok(invoiceLinesApiService.getTaxDetails(invoiceLineId))
                .build();
    }
    
    @Override
    public Response markForAdjustment(@NotNull InvoiceLinesToMark invoiceLinesToMark) {
        Invoice invoice = invoiceApiService.findById(id).orElseThrow(NotFoundException::new);
        Invoice adjInvoice = invoiceApiService.createAdjustment(invoice, invoiceLinesToReplicate);
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("invoice", invoice);
        return Response.ok(response).build();
    }
}