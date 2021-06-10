package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.billing.service.BillingRunApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.resource.invoicing.BillingRun;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

public class InvoicingResourceImpl implements InvoicingResource {

    @Inject
    private BillingRunApiService invoicingApiService;

    private final InvoicingMapper mapper = new InvoicingMapper();

    @Override
    public Response createExceptionalBillingRuns(BillingRun billingRun) {
        if(billingRun.getFilters() == null || billingRun.getFilters().isEmpty()) {
            throw new BadRequestException("Filters are required");
        }
        if(billingRun.getBillingRunTypeEnum() == null) {
            throw new BadRequestException("Billing run type is missing");
        }
        org.meveo.model.billing.BillingRun billingRunEntity = invoicingApiService.create(mapper.toEntity(billingRun));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoicingResource.class, billingRunEntity.getId()).build())
                .entity(billingRunEntity.getId())
                .build();
    }
}
