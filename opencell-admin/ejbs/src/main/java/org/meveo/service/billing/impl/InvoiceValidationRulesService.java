package org.meveo.service.billing.impl;


import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Objects;

@Stateless
public class InvoiceValidationRulesService extends BusinessService<InvoiceValidationRule> {

    public void updateInvoiceTypePriority(InvoiceValidationRule invoiceValidationRule) {

        InvoiceType invoiceType = invoiceValidationRule.getInvoiceType();

        if (invoiceValidationRule.getPriority() == null) {
            invoiceValidationRule.setPriority(invoiceType.getInvoiceValidationRules() != null ? invoiceType.getInvoiceValidationRules().size() + 1 : null);
        } else {
            reorderInvoiceValidationRules(invoiceValidationRule, false);
        }
    }

    public void reorderInvoiceValidationRules(InvoiceValidationRule invoiceValidationRule, boolean remove) {

        InvoiceType invoiceType = invoiceValidationRule.getInvoiceType();
        List<InvoiceValidationRule> invoiceValidationRules = invoiceType.getInvoiceValidationRules();

        int rulePriority = invoiceValidationRule.getPriority();

        if (CollectionUtils.isEmpty(invoiceValidationRules)) {
            invoiceValidationRule.setPriority(1);
        } else {
            if (rulePriority <= 0 || rulePriority > invoiceValidationRules.size() + 1) {
                invoiceValidationRule.setPriority(invoiceValidationRules.size() + 1);
            }
        }

        if (remove) {
            invoiceValidationRules.stream().filter(currentRule -> currentRule.getPriority() > rulePriority)
                    .forEach(currentRule -> currentRule.setPriority(currentRule.getPriority() - 1));
        } else {
            invoiceValidationRules.stream().filter(currentRule -> currentRule.getPriority() >= rulePriority).forEach(currentRule ->
                    currentRule.setPriority(currentRule.getPriority() + 1));
        }

        invoiceValidationRules.stream().filter(validationRule -> !Objects.equals(validationRule.getId(), invoiceValidationRule.getId())).forEach(this::update);

    }

    public Long returnLastId() {
        Object result = this.getEntityManager().createNamedQuery("InvoiceValidationRule.lastInsertedId").getSingleResult();
        return result != null ? (Long) result : 0;
    }


}
