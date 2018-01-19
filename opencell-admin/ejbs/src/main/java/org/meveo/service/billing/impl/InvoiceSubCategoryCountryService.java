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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

@Stateless
public class InvoiceSubCategoryCountryService extends PersistenceService<InvoiceSubcategoryCountry> {

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private ResourceBundle resourceBundle;

    @Override
    public void create(InvoiceSubcategoryCountry invoiceSubcategoryCountry) throws BusinessException {
        invoiceSubcategoryCountry = validateValidityDates(invoiceSubcategoryCountry);
        super.create(invoiceSubcategoryCountry);
        // Needed to refresh InvoiceSubCategory as InvoiceSubCategory.invoiceSubcategoryCountries field as it is cached
        refresh(invoiceSubcategoryCountry.getInvoiceSubCategory());
    }

    @Override
    public InvoiceSubcategoryCountry update(InvoiceSubcategoryCountry invoiceSubcategoryCountry) throws BusinessException {
        invoiceSubcategoryCountry = validateValidityDates(invoiceSubcategoryCountry);
        invoiceSubcategoryCountry = super.update(invoiceSubcategoryCountry);
        // Needed to refresh InvoiceSubCategory as InvoiceSubCategory.invoiceSubcategoryCountries field as it is cached
        refresh(invoiceSubcategoryCountry.getInvoiceSubCategory());
        return invoiceSubcategoryCountry;
    }

    @Override
    public void remove(InvoiceSubcategoryCountry invoiceSubcategoryCountry) throws BusinessException {
        super.remove(invoiceSubcategoryCountry);
        // Needed to remove from InvoiceSubCategory.invoiceSubcategoryCountries field as it is cached
        invoiceSubcategoryCountry.getInvoiceSubCategory().getInvoiceSubcategoryCountries().remove(invoiceSubcategoryCountry);
    }

    public InvoiceSubcategoryCountry validateValidityDates(InvoiceSubcategoryCountry invoiceSubcategoryCountry) throws BusinessException {
        // validate date
        // Check that two dates are one after another
        if (invoiceSubcategoryCountry.getStartValidityDate() != null && invoiceSubcategoryCountry.getEndValidityDate() != null
                && invoiceSubcategoryCountry.getStartValidityDate().compareTo(invoiceSubcategoryCountry.getEndValidityDate()) >= 0) {
            throw new BusinessException(resourceBundle.getString("invoiceSubCategoryCountry.validityDates.intervalIncorrect"));
        }

        // compute priority
        // get all the taxes of an invoice sub category
        List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = listByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubcategoryCountry.getInvoiceSubCategory(),
            invoiceSubcategoryCountry.getTradingCountry(), null, null, null);
        if (invoiceSubcategoryCountries != null) {
            InvoiceSubcategoryCountry invoiceSubcategoryCountryFound = null;
            // check for overlap
            for (InvoiceSubcategoryCountry invoiceSubcategoryCountryTemp : invoiceSubcategoryCountries) {
                if (!invoiceSubcategoryCountry.isTransient() && invoiceSubcategoryCountry.getId().equals(invoiceSubcategoryCountryTemp.getId())) {
                    continue;
                }
                if (invoiceSubcategoryCountryTemp.isCorrespondsToValidityDate(invoiceSubcategoryCountry.getStartValidityDate(), invoiceSubcategoryCountry.getEndValidityDate(),
                    false)) {
                    if (invoiceSubcategoryCountryFound == null || invoiceSubcategoryCountryFound.getPriority() < invoiceSubcategoryCountryTemp.getPriority()) {
                        invoiceSubcategoryCountryFound = invoiceSubcategoryCountryTemp;
                    }
                }
            }

            if (invoiceSubcategoryCountryFound != null) {
                // check if strict
                if (invoiceSubcategoryCountryFound.isCorrespondsToValidityDate(invoiceSubcategoryCountry.getStartValidityDate(), invoiceSubcategoryCountry.getEndValidityDate(),
                    true)) {
                    invoiceSubcategoryCountry.setStrictMatch(true);
                    invoiceSubcategoryCountry.setStartValidityDateMatch(invoiceSubcategoryCountryFound.getStartValidityDate());
                    invoiceSubcategoryCountry.setEndValidityDateMatch(invoiceSubcategoryCountryFound.getEndValidityDate());

                    throw new BusinessException(resourceBundle.getString("invoiceSubCategoryCountry.validityDates.matchingFound"));
                } else {
                    invoiceSubcategoryCountry.setStrictMatch(false);
                    invoiceSubcategoryCountry.setStartValidityDateMatch(invoiceSubcategoryCountryFound.getStartValidityDate());
                    invoiceSubcategoryCountry.setEndValidityDateMatch(invoiceSubcategoryCountryFound.getEndValidityDate());
                }
            }

            invoiceSubcategoryCountry.setPriority(getNextPriority(invoiceSubcategoryCountries));
        }

        return invoiceSubcategoryCountry;
    }

    private int getNextPriority(List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
        int maxPriority = 0;
        for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubcategoryCountries) {
            maxPriority = (invoiceSubcategoryCountry.getPriority() > maxPriority ? invoiceSubcategoryCountry.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    @SuppressWarnings("unchecked")
    public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(String invoiceSubCategoryCode, Long countryId, Date applicationDate) {

        try {
            QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "i");
            qb.addCriterion("invoiceSubCategory.code", "=", invoiceSubCategoryCode, true);
            qb.addCriterion("tradingCountry.id", "=", countryId, true);
            qb.addCriterionDateInRange("startValidityDate", "endValidityDate", applicationDate);
            qb.addOrderCriterion("priority", false);

            List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = qb.getQuery(getEntityManager()).getResultList();
            return invoiceSubcategoryCountries.size() > 0 ? invoiceSubcategoryCountries.get(0) : null;
        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country", ex);
        }

        return null;
    }

    /**
     * Find InvoiceSubCategoryCountry without fetching join entities.
     * 
     * @param invoiceSubCategory invoice sub category
     * @param tradingCountry trading country
     * @param applicationDate application date
     * @return invoice sub category country.
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(InvoiceSubCategory invoiceSubCategory, TradingCountry tradingCountry, Date applicationDate) {

        try {
            return getEntityManager().createNamedQuery("InvoiceSubcategoryCountry.findByInvoiceSubCategoryAndCountry", InvoiceSubcategoryCountry.class)
                .setParameter("invoiceSubCategory", invoiceSubCategory).setParameter("tradingCountry", tradingCountry).setParameter("applicationDate", applicationDate)
                .setMaxResults(1).getSingleResult();

        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country with parameters {}/{}/{}", invoiceSubCategory.getId(), tradingCountry.getId(), applicationDate);
        }

        return null;
    }

    /**
     * Find InvoiceSubCategoryCountry with fetching join entities.
     * 
     * @param invoiceSubCategory invoice sub category
     * @param tradingCountry trading country
     * @param fetchFields list of fields to be fetched
     * @param applicationDate application date.
     * @return invoice sub category.
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(InvoiceSubCategory invoiceSubCategory, TradingCountry tradingCountry, List<String> fetchFields,
            Date applicationDate) {
        QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "ic", fetchFields);
        qb.addCriterionEntity("ic.tradingCountry", tradingCountry);
        qb.addCriterionEntity("ic.invoiceSubCategory", invoiceSubCategory);
        qb.addCriterionDateInRange("startValidityDate", "endValidityDate", applicationDate);
        qb.addOrderCriterionAsIs("priority", false);

        try {
            return (InvoiceSubcategoryCountry) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get the tax with the most highest priority='1'.
     * 
     * @param invoiceSubCategoryId invoice sub categoryId
     * @param countryId country id
     * @return invoice sub category country.
     */
    @SuppressWarnings("unchecked")
    public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(Long invoiceSubCategoryId, Long countryId) {

        try {
            QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "i");
            qb.addCriterion("invoiceSubCategory.id", "=", invoiceSubCategoryId, true);
            qb.addCriterion("tradingCountry.id", "=", countryId, true);
            qb.addOrderCriterion("priority", false);

            List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = qb.getQuery(getEntityManager()).getResultList();
            return invoiceSubcategoryCountries.size() > 0 ? invoiceSubcategoryCountries.get(0) : null;
        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country", ex);
        }

        return null;
    }

    public Tax isInvoiceSubCategoryTaxValid(InvoiceSubcategoryCountry isc, UserAccount userAccount, BillingAccount billingAccount, Invoice invoice, Date operationDate)
            throws BusinessException {
        // check if invoiceSubCategory date is valid
        if (!DateUtils.isDateWithinPeriod(operationDate, isc.getStartValidityDate(), isc.getEndValidityDate())) {
            return null;
        }

        if (StringUtils.isBlank(isc.getTaxCodeEL())) {
            return isc.getTax();
        } else {
            return invoiceSubCategoryService.evaluateTaxCodeEL(isc.getTaxCodeEL(), userAccount, billingAccount, invoice);
        }
    }

    /**
     * Find InvoiceSubCategoryCountry with a given range of validity dates. No fetching join entities.
     * 
     * @param invoiceSubCategory invoice sub category
     * @param tradingCountry trading country
     * @param startValidityDate start validity date
     * @param endValidityDate and validity date
     * @return invoice sub category.
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry tradingCountry,
            Date startValidityDate, Date endValidityDate) {
        return findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory, tradingCountry, null, startValidityDate, endValidityDate);
    }

    /**
     * Find InvoiceSubCategoryCountry with a given range of validity dates. With fetching join entities.
     * 
     * @param invoiceSubCategory invoice sub category
     * @param tradingCountry trading country
     * @param fetchFields list of fields to be fetched
     * @param startValidityDate start validity date
     * @param endValidityDate and validity date
     * @return invoice sub category
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry tradingCountry,
            List<String> fetchFields, Date startValidityDate, Date endValidityDate) {
        List<InvoiceSubcategoryCountry> result = listByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory, tradingCountry, fetchFields, startValidityDate,
            endValidityDate);

        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public List<InvoiceSubcategoryCountry> listByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry tradingCountry,
            List<String> fetchFields, Date startValidityDate, Date endValidityDate) {
        QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "ic", fetchFields);
        qb.addCriterionEntity("ic.tradingCountry", tradingCountry);
        qb.addCriterionEntity("ic.invoiceSubCategory", invoiceSubCategory);
        if (startValidityDate != null) {
            qb.addCriterionDate("startValidityDate", startValidityDate);
        }
        if (endValidityDate != null) {
            qb.addCriterionDate("endValidityDate", endValidityDate);
        }
        qb.addOrderCriterion("priority", false);

        try {
            return (List<InvoiceSubcategoryCountry>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}