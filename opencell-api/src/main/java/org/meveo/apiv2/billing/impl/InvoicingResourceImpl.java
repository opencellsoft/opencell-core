package org.meveo.apiv2.billing.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.billing.service.BillingRunApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.resource.invoicing.BillingRun;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.billing.impl.InvoiceTypeService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

public class InvoicingResourceImpl implements InvoicingResource {

    @Inject
    private BillingRunApiService invoicingApiService;
    
    @Inject
    InvoiceTypeService invoiceTypeService;

    private final InvoicingMapper mapper = new InvoicingMapper();

    @Override
    public Response createExceptionalBillingRuns(BillingRun billingRun) {
        if(billingRun.getFilters() == null || billingRun.getFilters().isEmpty()) {
            throw new BadRequestException("Filters are required");
        }
        if(billingRun.getBillingRunTypeEnum() == null) {
            throw new BadRequestException("Billing run type is missing");
        }
        org.meveo.model.billing.BillingRun entity = mapper.toEntity(billingRun);
        if(billingRun.getInvoiceType() != null) {
        	InvoiceType invoiceType=invoiceTypeService.findByCode(billingRun.getInvoiceType());
        	if(invoiceType==null) {
        		throw new EntityDoesNotExistsException("InvoiceType", billingRun.getInvoiceType());
        	}
        	entity.setInvoiceType(invoiceType);
        }
		org.meveo.model.billing.BillingRun billingRunEntity = invoicingApiService.create(entity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoicingResource.class, billingRunEntity.getId()).build())
                .entity(billingRunEntity.getId())
                .build();
    }
}
