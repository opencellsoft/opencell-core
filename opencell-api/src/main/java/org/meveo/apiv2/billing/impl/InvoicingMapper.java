package org.meveo.apiv2.billing.impl;

import static java.util.UUID.randomUUID;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import org.meveo.apiv2.billing.resource.InvoicingResource;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.ordering.resource.invoicing.ImmutableBillingRun;
import org.meveo.model.billing.BillingRun;

public class InvoicingMapper extends ResourceMapper<org.meveo.apiv2.ordering.resource.invoicing.BillingRun, BillingRun> {

    @Override
    protected BillingRun toEntity(org.meveo.apiv2.ordering.resource.invoicing.BillingRun resource) {
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

    public org.meveo.apiv2.ordering.resource.invoicing.BillingRun toResourceInvoiceWithLink(
            org.meveo.apiv2.ordering.resource.invoicing.BillingRun billingRun) {
        return ImmutableBillingRun.copyOf(billingRun)
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
    protected org.meveo.apiv2.ordering.resource.invoicing.BillingRun toResource(BillingRun entity) {
        return null;
    }
}
