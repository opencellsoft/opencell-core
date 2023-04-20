package org.meveo.api.invoice;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceValidationRulesApiService implements ApiService<InvoiceValidationRule> {

    @Inject
    InvoiceValidationRulesService invoiceValidationRulesService;

    @Override
    public InvoiceValidationRule create(InvoiceValidationRule invoiceValidationRule) {

        invoiceValidationRulesService.updateInvoiceTypePriority(invoiceValidationRule);
        invoiceValidationRulesService.create(invoiceValidationRule);

        return invoiceValidationRule;
    }

    @Override
    public Optional<InvoiceValidationRule> update(Long id, InvoiceValidationRule invoiceValidationRule) {
    	invoiceValidationRulesService.updateInvoiceTypePriority(invoiceValidationRule);
        return Optional.ofNullable(invoiceValidationRulesService.update(invoiceValidationRule));
    }

    @Override
    public List<InvoiceValidationRule> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return new ArrayList<>();
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

        InvoiceValidationRule invoiceValidationRule = invoiceValidationRulesService.findById(id);
        if (invoiceValidationRule == null) {
            throw new BusinessException("The invoice validation rule does not exist");
        }
        invoiceValidationRulesService.remove(invoiceValidationRule);
        invoiceValidationRulesService.reorderInvoiceValidationRules(invoiceValidationRule, true);
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceValidationRule> findByCode(String code) {
        return Optional.empty();
    }
}
