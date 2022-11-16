package org.meveo.service.billing.impl;


import org.apache.commons.collections.CollectionUtils;
import org.meveo.apiv2.billing.InvoiceValidationRuleDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class InvoiceValidationRulesService extends BusinessService<InvoiceValidationRule> {

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


}
