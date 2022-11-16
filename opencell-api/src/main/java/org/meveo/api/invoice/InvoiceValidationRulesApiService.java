package org.meveo.api.invoice;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

public class InvoiceValidationRulesApiService implements ApiService<InvoiceValidationRule> {

    @Inject
    InvoiceValidationRulesService invoiceValidationRulesService;

    @Inject
    InvoiceTypeService invoiceTypeService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public InvoiceValidationRule create(InvoiceValidationRule invoiceValidationRule) {

        updateInvoiceTypePriority(invoiceValidationRule);

        invoiceValidationRulesService.create(invoiceValidationRule);

        return invoiceValidationRule;
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public Optional<InvoiceValidationRule> update(Long id, InvoiceValidationRule invoiceValidationRule) {
        updateInvoiceTypePriority(invoiceValidationRule);
        return Optional.ofNullable(invoiceValidationRulesService.update(invoiceValidationRule));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateInvoiceTypePriority(InvoiceValidationRule invoiceValidationRule) {

        InvoiceType invoiceType = invoiceValidationRule.getInvoiceType();

        if (invoiceValidationRule.getPriority() == null) {
            invoiceValidationRule.setPriority(invoiceType.getInvoiceValidationRules() != null ? invoiceType.getInvoiceValidationRules().size() + 1 : null);
        } else {
            InvoiceType updatedInvoiceType = invoiceValidationRulesService.reorderInvoiceValidationRules(invoiceType, invoiceValidationRule, false);
            invoiceTypeService.update(updatedInvoiceType);
        }
    }


    @Override
    public List<InvoiceValidationRule> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<InvoiceValidationRule> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceValidationRule> patch(Long id, InvoiceValidationRule baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceValidationRule> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceValidationRule> findByCode(String code) {
        return Optional.empty();
    }
}
