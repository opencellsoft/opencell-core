package org.meveo.apiv2.billing.impl;

import static java.util.UUID.randomUUID;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import org.meveo.apiv2.billing.ExceptionalBillingRun;
import org.meveo.apiv2.billing.ImmutableExceptionalBillingRun;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;

public class InvoicingMapper extends ResourceMapper<ExceptionalBillingRun, BillingRun> {

    @Override
    protected BillingRun toEntity(ExceptionalBillingRun resource) {
        BillingRun billingRun = new BillingRun();
        billingRun.setInvoiceDate(resource.getInvoiceDate());
        billingRun.setCollectionDate(resource.getCollectionDate());
        if(resource.getRejectAutoAction() == null) {
            billingRun.setRejectAutoAction(BillingRunAutomaticActionEnum.MANUAL_ACTION);
        }
        else {
            billingRun.setRejectAutoAction(resource.getRejectAutoAction());
        }        
        if(resource.getSuspectAutoAction() == null) {
            billingRun.setSuspectAutoAction(BillingRunAutomaticActionEnum.AUTOMATIC_VALIDATION);
        }
        else {
            billingRun.setSuspectAutoAction(resource.getSuspectAutoAction());
        }
        billingRun.setSkipValidationScript(resource.isSkipValidationScript());
        billingRun.setUuid(randomUUID().toString());
        billingRun.setStatus(NEW);
        billingRun.setProcessType(resource.getBillingRunTypeEnum());
        billingRun.setFilters(resource.getFilters());
        billingRun.setComputeDatesAtValidation(resource.isComputeDatesAtValidation());
        billingRun.setIncrementalInvoiceLines(resource.isIncrementalInvoiceLines());
        billingRun.setAggregateUnitAmounts(resource.isAggregateUnitAmounts());
        billingRun.setDateAggregation(resource.getDateAggregation());
        billingRun.setDiscountAggregation(resource.getDiscountAggregation());
        billingRun.setIgnoreOrders(resource.isIgnoreOrders());
        billingRun.setIgnoreSubscriptions(resource.isIgnoreSubscriptions());
        billingRun.setDisableAggregation(resource.isDisableAggregation());
        billingRun.setUseAccountingArticleLabel(resource.isUseAccountingArticleLabel());
        billingRun.setPreReportAutoOnCreate(resource.isPreReportAutoOnCreate());
        billingRun.setPreReportAutoOnInvoiceLinesJob(resource.isPreReportAutoOnInvoiceLinesJob());
        return billingRun;
    }

    @Override
    protected ExceptionalBillingRun toResource(BillingRun entity) {
        return ImmutableExceptionalBillingRun.builder()
        		.id(entity.getId())
        		.invoiceDate(entity.getInvoiceDate())
        		.collectionDate(entity.getCollectionDate())
        		.suspectAutoAction(entity.getSuspectAutoAction())
        		.rejectAutoAction(entity.getRejectAutoAction())
        		.isSkipValidationScript(entity.isSkipValidationScript())
        		.filters(entity.getFilters())
        		.isComputeDatesAtValidation(entity.getComputeDatesAtValidation())
                .isIncrementalInvoiceLines(entity.getIncrementalInvoiceLines())
        		.billingRunTypeEnum(entity.getProcessType())
                .isPreReportAutoOnCreate(entity.isPreReportAutoOnCreate())
                .isPreReportAutoOnInvoiceLinesJob(entity.isPreReportAutoOnInvoiceLinesJob())
        		.build();
    }
}