package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.billing.InvoiceValidationRuleDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;

public class InvoiceValidationRuleMapper extends ResourceMapper<InvoiceValidationRuleDto, InvoiceValidationRule> {

    @Override
    protected InvoiceValidationRuleDto toResource(InvoiceValidationRule entity) {
        return null;
    }

    @Override
    protected InvoiceValidationRule toEntity(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        InvoiceValidationRule invoiceValidationRule = new InvoiceValidationRule();
        invoiceValidationRule.setType(invoiceValidationRuleDto.getType());
        invoiceValidationRule.setValidationEL(invoiceValidationRuleDto.getValidationEL());
        invoiceValidationRule.setValidationScript(invoiceValidationRuleDto.getValidationScript());
        invoiceValidationRule.setCode(invoiceValidationRuleDto.getCode());
        invoiceValidationRule.setDescription(invoiceValidationRuleDto.getDescription());
        invoiceValidationRule.setValidFrom(invoiceValidationRuleDto.getValidFrom());
        invoiceValidationRule.setValidTo(invoiceValidationRuleDto.getValidTo());
        invoiceValidationRule.setFailStatus(invoiceValidationRuleDto.getFailStatus());

        return invoiceValidationRule;
    }

    protected InvoiceValidationRule toEntity(InvoiceValidationRuleDto invoiceValidationRuleDto, InvoiceValidationRule invoiceValidationRule, InvoiceType invoiceType) {

        if (invoiceValidationRuleDto.getType() != null) {
            invoiceValidationRule.setType(invoiceValidationRuleDto.getType());
        }

        if (invoiceType != null) {
            invoiceValidationRule.setInvoiceType(invoiceType);
        }

        if (invoiceValidationRuleDto.getFailStatus() != null) {
            invoiceValidationRule.setFailStatus(invoiceValidationRuleDto.getFailStatus());
        }

        if (invoiceValidationRuleDto.getValidationEL() != null) {
            invoiceValidationRule.setValidationEL(invoiceValidationRuleDto.getValidationEL());
        }

        if (invoiceValidationRuleDto.getValidationScript() != null) {
            invoiceValidationRule.setValidationScript(invoiceValidationRuleDto.getValidationScript());
        }

        if (invoiceValidationRuleDto.getCode() != null) {
            invoiceValidationRule.setCode(invoiceValidationRuleDto.getCode());
        }

        if (invoiceValidationRuleDto.getDescription() != null) {
            invoiceValidationRule.setDescription(invoiceValidationRuleDto.getDescription());
        }
        if (invoiceValidationRuleDto.getValidFrom() != null) {
            invoiceValidationRule.setValidFrom(invoiceValidationRuleDto.getValidFrom());
        }

        if (invoiceValidationRuleDto.getValidTo() != null) {
            invoiceValidationRule.setValidTo(invoiceValidationRuleDto.getValidTo());
        }


        return invoiceValidationRule;
    }
}
