package org.meveo.apiv2.billing.impl;

import static java.util.UUID.randomUUID;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import org.meveo.apiv2.billing.ExceptionalBillingRun;
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
        billingRun.setComputeDatesAtValidation(resource.isComputeDatesAtValidation());
        return billingRun;
    }

    @Override
    protected ExceptionalBillingRun toResource(BillingRun entity) {
        return null;
    }
}