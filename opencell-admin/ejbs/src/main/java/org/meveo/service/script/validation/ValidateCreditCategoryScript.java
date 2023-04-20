package org.meveo.service.script.validation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CreditCategory;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.script.Script;

public class ValidateCreditCategoryScript extends Script {

	private static final long serialVersionUID = 1L;

	private BillingAccountService billingAccountService = (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);

        if (invoice == null) {
            log.warn("No Invoice passed as CONTEXT_ENTITY");
            throw new BusinessException("No Invoice passed as CONTEXT_ENTITY");
        }

        List<CreditCategory> creditCategories = (List<CreditCategory>) context.get("CheckCreditCategory");

		if (creditCategories != null && !creditCategories.isEmpty()) {
			List<Long> creditCategorytIds = creditCategories.stream().map(CreditCategory::getId).collect(Collectors.toList());
			long counter = billingAccountService.getCountByCreditCategory(invoice.getBillingAccount().getId(), creditCategorytIds);
			context.put(Script.INVOICE_VALIDATION_STATUS, counter == 0 ? true : false);
		} else {
			context.put(Script.INVOICE_VALIDATION_STATUS, null);
		}
    }

}
