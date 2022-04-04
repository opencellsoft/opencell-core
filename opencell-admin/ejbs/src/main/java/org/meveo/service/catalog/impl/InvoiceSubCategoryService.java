/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.catalog.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * InvoiceSubCategory service implementation.
 * 
 */
@Stateless
public class InvoiceSubCategoryService extends BusinessService<InvoiceSubCategory> {
	
	private Map<Long, InvoiceSubCategory> invoiceSubCategoryMap = new TreeMap<Long, InvoiceSubCategory>();

	
    @SuppressWarnings("unchecked")
    public List<InvoiceSubCategory> findByInvoiceCategory(InvoiceCategory invoiceCategory) {
        QueryBuilder qb = new QueryBuilder(InvoiceSubCategory.class, "sc");
        qb.addCriterionEntity("sc.invoiceCategory", invoiceCategory);
        try {
            return qb.getQuery(getEntityManager()).getResultList();

        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean matchInvoicesubcatCountryExpression(String expression, BillingAccount billingAccount, Invoice invoice) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf("iv") >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put("iv", invoice);
            userMap.put("invoice", invoice);
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    @Override
    public void create(InvoiceSubCategory subCat) throws BusinessException {
        super.create(subCat);
        // Needed to refresh InvoiceCategory as InvoiceCategory.invoiceSubCategories field as it is cached
        refresh(subCat.getInvoiceCategory());
        invoiceSubCategoryMap.put(subCat.getId(),subCat);
    }

    @Override
    public InvoiceSubCategory update(InvoiceSubCategory subCat) throws BusinessException {
        subCat = super.update(subCat);
        // Needed to refresh InvoiceCategory as InvoiceCategory.invoiceSubCategories field as it is cached
        refresh(subCat.getInvoiceCategory());
        invoiceSubCategoryMap.put(subCat.getId(),subCat);
        return subCat;
    }

    @Override
    public void remove(InvoiceSubCategory subCat) throws BusinessException {
        super.remove(subCat);
        // Needed to remove from InvoiceCategory.invoiceSubCategories field as it is cached
        subCat.getInvoiceCategory().getInvoiceSubCategories().remove(subCat);
        if(invoiceSubCategoryMap.containsKey(subCat.getId())) {
        	invoiceSubCategoryMap.remove(subCat.getId());
        }
    }

	/**
	 * @param invoiceSubCategoryId
	 * @return
	 */
	public InvoiceSubCategory findFromMap(long invoiceSubCategoryId) {
		if(invoiceSubCategoryMap.isEmpty()) {
	        List<InvoiceSubCategory> invoiceSubCategories = getEntityManager().createNamedQuery("InvoiceSubCategory.listWithCategory").getResultList();
			invoiceSubCategoryMap = invoiceSubCategories.stream().collect(Collectors.toMap(InvoiceSubCategory::getId, Function.identity()));
		}
		return invoiceSubCategoryMap.get(invoiceSubCategoryId);
	}
}