/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * InvoiceSubCategory service implementation.
 * 
 */
@Stateless
public class InvoiceSubCategoryService extends BusinessService<InvoiceSubCategory> {

    @Inject
    private TaxService taxService;

    public int getNbInvSubCatNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("invoiceSubCategory.getNbrInvoiceSubCatNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<InvoiceSubCategory> getInvoiceSubCatNotAssociated() {
        return (List<InvoiceSubCategory>) getEntityManager().createNamedQuery("invoiceSubCategory.getInvoiceSubCatNotAssociated", InvoiceSubCategory.class).getResultList();
    }

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

        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("iv") >= 0 || expression.indexOf("invoice") >= 0) {
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

    public Tax evaluateTaxCodeEL(String expression, UserAccount userAccount, BillingAccount billingAccount, Invoice invoice) throws BusinessException {
        Tax result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("seller") >= 0) {
            userMap.put("seller", billingAccount.getCustomerAccount().getCustomer().getSeller());
        }
        if (expression.indexOf("cust") >= 0) {
            userMap.put("cust", billingAccount.getCustomerAccount().getCustomer());
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", userAccount);
        }
        if (expression.indexOf("iv") >= 0 || expression.indexOf("invoice") >= 0) {
            userMap.put("iv", invoice);
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("date") >= 0) {
            userMap.put("date", invoice == null ? new Date() : invoice.getInvoiceDate());
        }
        String taxCode = null;
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            taxCode = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        if (taxCode == null) {
            throw new BusinessException("Expression " + expression + " evaluates to null  ");
        } else {
            result = taxService.findByCode(taxCode);
        }

        return result;
    }

    @Override
    public void create(InvoiceSubCategory subCat) throws BusinessException {
        super.create(subCat);
        // Needed to refresh InvoiceCategory as InvoiceCategory.invoiceSubCategories field as it is cached
        refresh(subCat.getInvoiceCategory());
    }

    @Override
    public InvoiceSubCategory update(InvoiceSubCategory subCat) throws BusinessException {
        subCat = super.update(subCat);
        // Needed to refresh InvoiceCategory as InvoiceCategory.invoiceSubCategories field as it is cached
        refresh(subCat.getInvoiceCategory());
        return subCat;
    }

    @Override
    public void remove(InvoiceSubCategory subCat) throws BusinessException {
        super.remove(subCat);
        // Needed to remove from InvoiceCategory.invoiceSubCategories field as it is cached
        subCat.getInvoiceCategory().getInvoiceSubCategories().remove(subCat);
    }
}