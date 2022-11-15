package org.meveo.service.billing.impl;


import org.apache.commons.collections.CollectionUtils;
import org.meveo.api.dto.invoice.InvoiceValidationRuleDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class InvoiceValidationRulesService extends PersistenceService<InvoiceValidationRule> {

    @Inject
    InvoiceTypeService invoiceTypeService;

    public InvoiceType reorderInvoiceValidationRules(InvoiceType invoiceType, InvoiceValidationRule rule, boolean remove) {

        List<InvoiceValidationRule> invoiceValidationRules = invoiceType.getInvoiceValidationRules();

        int rulePriority = rule.getPriority();

        if (CollectionUtils.isEmpty(invoiceValidationRules)) {
            rule.setPriority(1);
        } else {
            if (rulePriority <= 0 || rulePriority > invoiceValidationRules.size() + 1) {
                rule.setPriority(invoiceValidationRules.size() + 1);
            }
        }

        if (remove) {
            invoiceValidationRules.stream().filter(currentRule -> currentRule.getPriority() > rulePriority).forEach(currentRule -> currentRule.setPriority(currentRule.getPriority() - 1));
        } else {
            invoiceValidationRules.stream().filter(currentRule -> currentRule.getPriority() >= rulePriority).forEach(currentRule -> currentRule.setPriority(currentRule.getPriority() + 1));
        }

        invoiceType.setInvoiceValidationRules(invoiceValidationRules);

        return invoiceType;
    }

    public InvoiceValidationRule createInvoiceValidationOrUpdateFromDto(InvoiceValidationRuleDto invoiceValidationRuleDto, InvoiceValidationRule invoiceValidationRule) {

        if (invoiceValidationRule == null) {
            invoiceValidationRule = new InvoiceValidationRule();
        }

        if (invoiceValidationRuleDto.getType() != null)
            invoiceValidationRule.setType(invoiceValidationRuleDto.getType());

        if (invoiceValidationRuleDto.getValidationEL() != null)
            invoiceValidationRule.setValidationEL(invoiceValidationRuleDto.getValidationEL());

        if (invoiceValidationRuleDto.getValidationScript() != null)
            invoiceValidationRule.setValidationScript(invoiceValidationRuleDto.getValidationScript());

        if (invoiceValidationRuleDto.getCode() != null)
            invoiceValidationRule.setCode(invoiceValidationRuleDto.getCode());

        if (invoiceValidationRuleDto.getDescription() != null)
            invoiceValidationRule.setDescription(invoiceValidationRuleDto.getDescription());

        if (invoiceValidationRuleDto.getValidFrom() != null)
            invoiceValidationRule.setValidTo(invoiceValidationRuleDto.getValidFrom());

        if (invoiceValidationRuleDto.getValidTo() != null)
            invoiceValidationRule.setValidTo(invoiceValidationRuleDto.getValidTo());

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceValidationRuleDto.getInvoiceTypeDto().getCode());
        invoiceValidationRule.setInvoiceType(invoiceType);

        return invoiceValidationRule;
    }

}
