package org.meveo.apiv2.billing.impl;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.billing.ExceptionalBillingRun;
import org.meveo.apiv2.billing.ImmutableExceptionalBillingRun;
import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.billing.service.BillingRunApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

public class InvoicingResourceImpl implements InvoicingResource {

    @Inject
    private BillingRunApiService invoicingApiService;

    private final InvoicingMapper mapper = new InvoicingMapper();

    @Override
    public Response createExceptionalBillingRuns(ExceptionalBillingRun billingRun) {
        if (billingRun.getFilters() == null || billingRun.getFilters().isEmpty()) {
            throw new BadRequestException("Filters are required");
        }

        if (billingRun.getBillingRunTypeEnum() == null) {
            throw new BadRequestException("Billing run type is missing");
        }
        org.meveo.model.billing.BillingRun billingRunEntity = invoicingApiService.create(mapper.toEntity(billingRun));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(InvoicingResource.class, billingRunEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(billingRunEntity)))
                .build();
    }

    private ImmutableExceptionalBillingRun toResourceOrderWithLink(ExceptionalBillingRun exceptionBillingRun) {
        return ImmutableExceptionalBillingRun.copyOf(exceptionBillingRun)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(InvoicingResource.class)
                                .withId(exceptionBillingRun.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }

}