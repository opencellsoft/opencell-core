package org.meveo.apiv2.billing.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.InvoiceLinesToMarkAdjustment;
import org.meveo.apiv2.billing.resource.InvoiceLinesResource;
import org.meveo.apiv2.billing.service.InvoiceLinesApiService;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.generic.services.SearchResult;

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
    	int nbInvoiceLinesMarked = 0;
    	invoiceLinesToMark.getFilters().get("inList id");
    	/*if(!CollectionUtils.isEmpty(invoiceLinesToMark.getInvoiceLinesIds()))
    		nbInvoiceLinesMarked = invoiceLinesApiService.markInvoiceLinesForAdjustment(invoiceLinesToMark);*/
    	
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("message", nbInvoiceLinesMarked+" new invoiceLine(s) marked TO_ADJUST");
        return Response.ok(response).build();
    }
    
    @Override
    public Response unmarkForAdjustment(@NotNull InvoiceLinesToMarkAdjustment invoiceLinesToUnmark) {
    	
    	invoiceLinesApiService.getInvoiceLineIds(invoiceLinesToUnmark);
    	
    	
    	int nbInvoiceLinesUnmarked = 0;
    	/*if(!CollectionUtils.isEmpty(invoiceLinesToUnmark.getInvoiceLinesIds()))
    		nbInvoiceLinesUnmarked = invoiceLinesApiService.unmarkInvoiceLinesForAdjustment(invoiceLinesToUnmark);*/
    	
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("message", nbInvoiceLinesUnmarked+" new invoiceLine(s) marked NOT_ADJUSTED");
        return Response.ok(response).build();
    }


}