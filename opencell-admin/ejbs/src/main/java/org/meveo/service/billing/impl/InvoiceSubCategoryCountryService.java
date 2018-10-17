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
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * Service for Invoice subcategory country entity management and applicable tax determination
 * 
 * @author Andrius Karpavicius
 */
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

    /**
     * Check that validity dates of Invoice subcategory country entity do not overlapp existing records
     * 
     * @param invoiceSubcategoryCountry Invoice subcategory country entity
     * @return Updated validity dates and priority value in Invoice subcategory country entity
     * @throws BusinessException General business exception
     */
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
            invoiceSubcategoryCountry.getSellingCountry(), invoiceSubcategoryCountry.getTradingCountry(), null, null, null);
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

    /**
     * Find InvoiceSubCategoryCountry with the highest priority (lowest number). Does not fetch join entities.
     * 
     * @param invoiceSubCategory invoice sub category
     * @param sellersCountry Seller's trading country
     * @param buyersCountry Buyer's trading country
     * @param applicationDate application date
     * @return Invoice sub category country.
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(InvoiceSubCategory invoiceSubCategory, TradingCountry sellersCountry, TradingCountry buyersCountry,
            Date applicationDate) {

        try {
            return getEntityManager().createNamedQuery("InvoiceSubcategoryCountry.findByInvoiceSubCategoryAndCountry", InvoiceSubcategoryCountry.class)
                .setParameter("invoiceSubCategory", invoiceSubCategory).setParameter("sellingCountry", sellersCountry).setParameter("tradingCountry", buyersCountry)
                .setParameter("applicationDate", applicationDate).setMaxResults(1).getSingleResult();

        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country with parameters {}/{}/{}/{}", invoiceSubCategory.getId(), (sellersCountry != null ? sellersCountry.getId() : null),
                buyersCountry.getId(), applicationDate);
        }

        return null;
    }

    /**
     * Check if Invoice subcategory country entity is applicable with given criteria
     * 
     * @param isc Invoice subcategory country entity
     * @param userAccount User account
     * @param billingAccount Billing account
     * @param invoice Invoice
     * @param operationDate Operation date
     * @return True if Invoice subcategory country entity is applicable with given criteria
     * @throws BusinessException General business exception
     */
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
     * @param invoiceSubCategory Invoice sub category
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param startValidityDate Tax validity period - from
     * @param endValidityDate Tax validity period - to
     * @return Invoice sub category country
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry sellersCountry,
            TradingCountry buyersCountry, Date startValidityDate, Date endValidityDate) {
        return findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory, sellersCountry, buyersCountry, null, startValidityDate, endValidityDate);
    }

    /**
     * Find InvoiceSubCategoryCountry with a given range of validity dates. With fetching of indicated join entities.
     * 
     * @param invoiceSubCategory Invoice sub category
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param fetchFields List of fields to be fetched
     * @param startValidityDate Tax validity period - from
     * @param endValidityDate Tax validity period - to
     * @return Invoice sub category country
     */
    public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry sellersCountry,
            TradingCountry buyersCountry, List<String> fetchFields, Date startValidityDate, Date endValidityDate) {

        List<InvoiceSubcategoryCountry> result = listByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory, sellersCountry, buyersCountry, fetchFields,
            startValidityDate, endValidityDate);

        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    /**
     * Find a list of matching InvoiceSubCategoryCountry with a given range of validity dates. With fetching of indicated join entities.
     * 
     * @param invoiceSubCategory Invoice sub category
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param fetchFields List of fields to be fetched
     * @param startValidityDate Tax validity period - from
     * @param endValidityDate Tax validity period - to
     * @return Invoice sub category country
     */
    @SuppressWarnings("unchecked")
    public List<InvoiceSubcategoryCountry> listByInvoiceSubCategoryAndCountryWithValidityDates(InvoiceSubCategory invoiceSubCategory, TradingCountry sellersCountry,
            TradingCountry buyersCountry, List<String> fetchFields, Date startValidityDate, Date endValidityDate) {

        QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "ic", fetchFields);

        if (sellersCountry == null) {
            qb.addSql("ic.sellingCountry is null");
        } else {
            qb.addCriterionEntity("ic.sellingCountry", sellersCountry);
        }

        if (buyersCountry == null) {
            qb.addSql("ic.tradingCountry is null");
        } else {
            qb.addCriterionEntity("ic.tradingCountry", buyersCountry);
        }
        qb.addCriterionEntity("ic.invoiceSubCategory", invoiceSubCategory);
        if (startValidityDate != null) {
            qb.addCriterionDate("startValidityDate", startValidityDate);
        }
        if (endValidityDate != null) {
            qb.addCriterionDate("endValidityDate", endValidityDate);
        }
        qb.addOrderMultiCriterion("ic.sellingCountry", false, "ic.tradingCountry", false, "ic.priority", false);

        try {
            return (List<InvoiceSubcategoryCountry>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Determine applicable tax for a given charge instance
     * 
     * @param chargeInstance Charge instance
     * @param date Date to determine tax validity
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public Tax determineTax(ChargeInstance chargeInstance, Date date) throws BusinessException {

        InvoiceSubCategory invoiceSubCategory = chargeInstance.getChargeTemplate().getInvoiceSubCategory();

        TradingCountry sellersCountry = chargeInstance.getSeller().getTradingCountry();
        TradingCountry buyersCountry = chargeInstance.getCountry();

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = findByInvoiceSubCategoryAndCountry(invoiceSubCategory, sellersCountry, buyersCountry, date);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory " + invoiceSubCategory.getId() + "/"
                    + (sellersCountry != null ? sellersCountry.getId() : null) + "/" + buyersCountry.getId());
        }

        Tax tax = null;

        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        return tax;
    }

    /**
     * Determine applicable tax for a given seller/buyer and invoice subcategory combination
     * 
     * @param invoiceSubCategory Invoice subcategory
     * @param seller Seller
     * @param billingAccount Billing account
     * @param date Date to determine tax validity
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public Tax determineTax(InvoiceSubCategory invoiceSubCategory, Seller seller, BillingAccount billingAccount, Date date, boolean ignoreNoTax) throws BusinessException {

        TradingCountry sellersCountry = seller.getTradingCountry();
        TradingCountry buyersCountry = billingAccount.getTradingCountry();

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = findByInvoiceSubCategoryAndCountry(invoiceSubCategory, sellersCountry, buyersCountry, date);
        if (invoiceSubcategoryCountry == null) {
            if (ignoreNoTax) {
                return null;
            }
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory " + invoiceSubCategory.getId() + "/"
                    + (sellersCountry != null ? sellersCountry.getId() : null) + "/" + buyersCountry.getId());
        }

        Tax tax = null;

        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, billingAccount, null);
        }

        if (tax == null) {
            if (ignoreNoTax) {
                return null;
            }
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        return tax;
    }

    /**
     * Determine applicable tax for a given seller/buyer and invoice subcategory combination
     * 
     * @param invoiceSubCategory Invoice subcategory
     * @param seller Seller
     * @param buyersCountry Buyer's country
     * @param date Date to determine tax validity
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public Tax determineTax(InvoiceSubCategory invoiceSubCategory, Seller seller, TradingCountry buyersCountry, Date date, boolean ignoreNoTax) throws BusinessException {

        TradingCountry sellersCountry = seller.getTradingCountry();

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = findByInvoiceSubCategoryAndCountry(invoiceSubCategory, sellersCountry, buyersCountry, date);
        if (invoiceSubcategoryCountry == null) {
            if (ignoreNoTax) {
                return null;
            }
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory " + invoiceSubCategory.getId() + "/"
                    + (sellersCountry != null ? sellersCountry.getId() : null) + "/" + buyersCountry.getId());
        }

        Tax tax = null;

        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, null, null);
        }

        if (tax == null) {
            if (ignoreNoTax) {
                return null;
            }
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        return tax;
    }
}