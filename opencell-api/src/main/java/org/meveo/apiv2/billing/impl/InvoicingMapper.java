package org.meveo.apiv2.billing.impl;

import static java.util.UUID.randomUUID;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import org.meveo.apiv2.billing.ExceptionalBillingRun;
import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.billing.ImmutableExceptionalBillingRun;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.BillingRun;

public class InvoicingMapper extends ResourceMapper<ExceptionalBillingRun, BillingRun> {

    @Override
    protected BillingRun toEntity(ExceptionalBillingRun resource) {
        BillingRun billingRun = new BillingRun();
        billingRun.setInvoiceDate(resource.getInvoiceDate());
        billingRun.setCollectionDate(resource.getCollectionDate());
        billingRun.setSuspectAutoAction(resource.getSuspectAutoAction());
        billingRun.setRejectAutoAction(resource.getRejectAutoAction());
        billingRun.setSkipValidationScript(resource.isSkipValidationScript());
        billingRun.setUuid(randomUUID().toString());
        billingRun.setStatus(NEW);
        billingRun.setProcessType(resource.getBillingRunTypeEnum());
        billingRun.setFilters(resource.getFilters());
        return billingRun;
    }

    public ExceptionalBillingRun toResourceInvoiceWithLink(ExceptionalBillingRun billingRun) {
        return ImmutableExceptionalBillingRun.copyOf(billingRun)
                .withLinks(new LinkGenerator.SelfLinkGenerator(InvoicingResource.class)
                        .withId(billingRun.getId())
                        .withGetAction()
                        .withPostAction()
                        .withPutAction()
                        .withPatchAction()
                        .withDeleteAction()
                        .build());
    }

    @Override
    protected ExceptionalBillingRun toResource(BillingRun entity) {
        return null;
    }
}
