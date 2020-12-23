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

package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @lastModifiedVersion 7.0
 */
@Stateful
public class InvoiceAggregateHandler {
    private Logger log = LoggerFactory.getLogger(InvoiceAggregateHandler.class);

    private Map<String, CategoryInvoiceAgregate> catInvAgregateMap = new HashMap<String, CategoryInvoiceAgregate>();
    private Map<String, SubCategoryInvoiceAgregate> subCatInvAgregateMap = new HashMap<String, SubCategoryInvoiceAgregate>();
    private Map<String, TaxInvoiceAgregate> taxInvAgregateMap = new HashMap<String, TaxInvoiceAgregate>();

    private BigDecimal invoiceAmountWithoutTax = BigDecimal.ZERO;
    private BigDecimal invoiceAmountTax = BigDecimal.ZERO;
    private BigDecimal invoiceAmountWithTax = BigDecimal.ZERO;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private TaxMappingService taxMappingService;

    /**
     * reset default values.
     */
    public void reset() {
        catInvAgregateMap = new HashMap<>();
        subCatInvAgregateMap = new HashMap<>();
        taxInvAgregateMap = new HashMap<>();

        invoiceAmountWithoutTax = BigDecimal.ZERO;
        invoiceAmountTax = BigDecimal.ZERO;
        invoiceAmountWithTax = BigDecimal.ZERO;

    }

    /**
     * Add invoice subcategory line
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param userAccount user account.
     * @param description description.
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @throws BusinessException business exception
     */
    public void addInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory, UserAccount userAccount, String description, BigDecimal amountWithoutTax, BigDecimal amountWithTax) throws BusinessException {
        addRemoveOrUpdateSubCategoryInvoiceAggregate(invoiceSubCategory, userAccount, description, true, amountWithoutTax, amountWithTax, null);
    }

    /**
     * Remove invoice subcategory line
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param userAccount user account.
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @throws BusinessException business exception
     */
    public void removeInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory, UserAccount userAccount, BigDecimal amountWithoutTax, BigDecimal amountWithTax) throws BusinessException {

        addRemoveOrUpdateSubCategoryInvoiceAggregate(invoiceSubCategory, userAccount, null, false, amountWithoutTax, amountWithTax, null);
    }

    /**
     * Remove rated transaction.
     * 
     * @param ratedTransaction rated transaction to remove
     * @throws BusinessException business exception
     */
    public void removeRT(RatedTransaction ratedTransaction) throws BusinessException {
        addOrRemoveRT(null, ratedTransaction, false);
    }

    /**
     * Add Rated transaction
     * 
     * @param invoiceDate Invoice date
     * @param ratedTransaction Rated transaction to add
     * @throws BusinessException business exception
     */
    public void addRT(Date invoiceDate, RatedTransaction ratedTransaction) throws BusinessException {

        addOrRemoveRT(invoiceDate, ratedTransaction, true);
    }

    /**
     * v5.0: Fix tax added to invoice net amount when a customer has "no tax applied"
     * 
     * @param Seller the seller. If null use the one from customer.
     * @param invoiceDate invoice date. If null use now.
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account.
     * @param description description.
     * @param amount amount without tax if enterprise is true else amount with tax
     * @param ratedTransaction rated transaction.
     * @param isToAdd true if it is to be added.
     * @throws BusinessException business exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private void addOrRemoveRT(Date invoiceDate, RatedTransaction ratedTransaction, boolean isToAdd) throws BusinessException {

        boolean isEnterprise = appProvider.isEntreprise();
        int rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        if (isToAdd) {
            if (ratedTransaction.getIsEnterpriseAmount(isEnterprise) == null) {
                if (ratedTransaction.getIsEnterpriseUnitAmount(isEnterprise) == null || ratedTransaction.getQuantity() == null) {
                    throw new BusinessException("RT.unitAmountWithoutTax/unitAmountWithTax or RT.quantity are null");
                }
                ratedTransaction.setIsEnterpriseAmount(isEnterprise, ratedTransaction.getIsEnterpriseUnitAmount(isEnterprise).multiply(ratedTransaction.getQuantity()));
            }

            if (invoiceDate == null) {
                invoiceDate = new Date();
            }

            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(ratedTransaction.getAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getTaxPercent(), isEnterprise, rounding,
                roundingMode.getRoundingMode());

            ratedTransaction.setAmountWithoutTax(amounts[0]);
            ratedTransaction.setAmountWithTax(amounts[1]);
            ratedTransaction.setAmountTax(amounts[2]);
        }

        addRemoveOrUpdateSubCategoryInvoiceAggregate(ratedTransaction.getInvoiceSubCategory(), ratedTransaction.getUserAccount(), ratedTransaction.getInvoiceSubCategory().getDescription(), isToAdd,
            ratedTransaction.getAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction);

        updateInvoiceAgregateTax(ratedTransaction.getBillingAccount(), isToAdd, ratedTransaction.getAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax(), ratedTransaction.getTax());
    }

    private void updateInvoiceAgregateTax(BillingAccount billingAccount, boolean isToAdd, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, Tax currentTax) {
        TaxInvoiceAgregate invoiceAgregateTax = taxInvAgregateMap.get(currentTax.getCode());
        if (invoiceAgregateTax == null) {
            invoiceAgregateTax = new TaxInvoiceAgregate();
            invoiceAgregateTax.setBillingRun(null);
            invoiceAgregateTax.setTax(currentTax);
            invoiceAgregateTax.setAccountingCode(currentTax.getAccountingCode());
            invoiceAgregateTax.setTaxPercent(currentTax.getPercent());
            invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
            invoiceAgregateTax.setAmountWithTax(BigDecimal.ZERO);
            invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
            invoiceAgregateTax.setBillingAccount(billingAccount);
            invoiceAgregateTax.setItemNumber(0);
        }
        invoiceAgregateTax.setAmountWithoutTax(addOrSubtract(invoiceAgregateTax.getAmountWithoutTax(), amountWithoutTax, isToAdd));
        invoiceAgregateTax.setAmountWithTax(addOrSubtract(invoiceAgregateTax.getAmountWithTax(), amountWithTax, isToAdd));
        invoiceAgregateTax.setAmountTax(addOrSubtract(invoiceAgregateTax.getAmountTax(), amountTax, isToAdd));
        invoiceAgregateTax.setItemNumber(invoiceAgregateTax.getItemNumber() + (isToAdd ? 1 : -1));

        if (invoiceAgregateTax.getItemNumber() > 0) {
            taxInvAgregateMap.put(currentTax.getCode(), invoiceAgregateTax);
        } else {
            taxInvAgregateMap.remove(currentTax.getCode());
        }
    }

    private void addRemoveOrUpdateSubCategoryInvoiceAggregate(InvoiceSubCategory invoiceSubCategory, UserAccount userAccount, String description, boolean isToAdd, BigDecimal amountWithoutTax, BigDecimal amountWithTax,
            RatedTransaction ratedTransaction) throws BusinessException {

        BigDecimal amountTax = amountWithTax.subtract(amountWithoutTax);

        SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = subCatInvAgregateMap.get(invoiceSubCategory.getCode());
        CategoryInvoiceAgregate categoryInvoiceAgregate = null;
        if (subCategoryInvoiceAgregate != null && subCategoryInvoiceAgregate.getCategoryInvoiceAgregate()!=null) {
            categoryInvoiceAgregate = subCategoryInvoiceAgregate.getCategoryInvoiceAgregate();

        } else {
            categoryInvoiceAgregate = catInvAgregateMap.get(invoiceSubCategory.getInvoiceCategory().getCode());
            if (categoryInvoiceAgregate == null) {
                categoryInvoiceAgregate = new CategoryInvoiceAgregate();
                categoryInvoiceAgregate.setAuditable(new Auditable(currentUser));
                categoryInvoiceAgregate.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
                categoryInvoiceAgregate.setDescription(invoiceSubCategory.getInvoiceCategory().getDescription());
                categoryInvoiceAgregate.setAmountWithoutTax(BigDecimal.ZERO);
                categoryInvoiceAgregate.setAmountWithTax(BigDecimal.ZERO);
                categoryInvoiceAgregate.setAmountTax(BigDecimal.ZERO);
                categoryInvoiceAgregate.setBillingAccount(userAccount.getBillingAccount());
                categoryInvoiceAgregate.setUserAccount(userAccount);
                categoryInvoiceAgregate.setItemNumber(0);

                catInvAgregateMap.put(invoiceSubCategory.getInvoiceCategory().getCode(), categoryInvoiceAgregate);
            }

            if (subCategoryInvoiceAgregate == null) {
                subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
                subCategoryInvoiceAgregate.setAuditable(new Auditable(currentUser));
                subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
                subCategoryInvoiceAgregate.setInvoiceSubCategory(invoiceSubCategory);
                subCategoryInvoiceAgregate.setDescription(description);
                subCategoryInvoiceAgregate.setBillingRun(null);
                subCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
                subCategoryInvoiceAgregate.setUserAccount(userAccount);
                subCategoryInvoiceAgregate.setBillingAccount(userAccount.getBillingAccount());
                subCategoryInvoiceAgregate.setAccountingCode(invoiceSubCategory.getAccountingCode());
                subCategoryInvoiceAgregate.setAmountWithoutTax(BigDecimal.ZERO);
                subCategoryInvoiceAgregate.setAmountWithTax(BigDecimal.ZERO);
                subCategoryInvoiceAgregate.setAmountTax(BigDecimal.ZERO);
                subCategoryInvoiceAgregate.setItemNumber(0);

                subCatInvAgregateMap.put(invoiceSubCategory.getCode(), subCategoryInvoiceAgregate);
            }
        }

        categoryInvoiceAgregate.setAmountWithoutTax(addOrSubtract(categoryInvoiceAgregate.getAmountWithoutTax(), amountWithoutTax, isToAdd));
        categoryInvoiceAgregate.setAmountWithTax(addOrSubtract(categoryInvoiceAgregate.getAmountWithTax(), amountWithTax, isToAdd));
        categoryInvoiceAgregate.setAmountTax(addOrSubtract(categoryInvoiceAgregate.getAmountTax(), amountTax, isToAdd));

        subCategoryInvoiceAgregate.setAmountWithoutTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountWithoutTax(), amountWithoutTax, isToAdd));
        subCategoryInvoiceAgregate.setAmountWithTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountWithTax(), amountWithTax, isToAdd));
        subCategoryInvoiceAgregate.setAmountTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountTax(), amountTax, isToAdd));

        invoiceAmountWithoutTax = addOrSubtract(invoiceAmountWithoutTax, amountWithoutTax, isToAdd);
        invoiceAmountTax = addOrSubtract(invoiceAmountTax, amountTax, isToAdd);
        invoiceAmountWithTax = addOrSubtract(invoiceAmountWithTax, amountWithTax, isToAdd);

        boolean removeSubcategory = !isToAdd && ratedTransaction == null;

        if (ratedTransaction != null) {
            categoryInvoiceAgregate.setItemNumber(categoryInvoiceAgregate.getItemNumber() + (isToAdd ? 1 : -1));
            subCategoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregate.getItemNumber() + (isToAdd ? 1 : -1));

            if (isToAdd) {
                subCategoryInvoiceAgregate.getRatedtransactionsToAssociate().add(ratedTransaction);
            } else if (subCategoryInvoiceAgregate.getItemNumber().intValue() == 0) {
                removeSubcategory = true;
            } else {
                subCategoryInvoiceAgregate.getRatedtransactionsToAssociate().remove(ratedTransaction);
            }
        }

        if (removeSubcategory) {
            subCatInvAgregateMap.remove(invoiceSubCategory.getCode());

            // A simple remove would do, but for some reason does not work even though equals and hashSet are overridden
            SubCategoryInvoiceAgregate subCategoryInvoiceAgregateFinal = subCategoryInvoiceAgregate;
            Set newSet = categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().stream().filter(subCat -> !subCat.equals(subCategoryInvoiceAgregateFinal)).collect(Collectors.toSet());
            categoryInvoiceAgregate.setSubCategoryInvoiceAgregates(newSet);
            if (categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().isEmpty()) {
                catInvAgregateMap.remove(invoiceSubCategory.getInvoiceCategory().getCode());
            }
        }
    }

    /**
     * 
     * @param mainValue input value
     * @param aValue added value
     * @param isToAdd if true the added value will be added to mainValue
     * @return calculated value.
     */
    private BigDecimal addOrSubtract(BigDecimal mainValue, BigDecimal aValue, boolean isToAdd) {
        if (isToAdd) {
            return mainValue.add(aValue);
        }
        return mainValue.subtract(aValue);
    }

    /**
     * @return the catInvAgregateMap
     */
    public Map<String, CategoryInvoiceAgregate> getCatInvAgregateMap() {
        return catInvAgregateMap;
    }

    /**
     * @param catInvAgregateMap the catInvAgregateMap to set
     */
    public void setCatInvAgregateMap(Map<String, CategoryInvoiceAgregate> catInvAgregateMap) {
        this.catInvAgregateMap = catInvAgregateMap;
    }

    /**
     * @return the subCatInvAgregateMap
     */
    public Map<String, SubCategoryInvoiceAgregate> getSubCatInvAgregateMap() {
        return subCatInvAgregateMap;
    }

    /**
     * @param subCatInvAgregateMap the subCatInvAgregateMap to set
     */
    public void setSubCatInvAgregateMap(Map<String, SubCategoryInvoiceAgregate> subCatInvAgregateMap) {
        this.subCatInvAgregateMap = subCatInvAgregateMap;
    }

    /**
     * @return the taxInvAgregateMap
     */
    public Map<String, TaxInvoiceAgregate> getTaxInvAgregateMap() {
        return taxInvAgregateMap;
    }

    /**
     * @param taxInvAgregateMap the taxInvAgregateMap to set
     */
    public void setTaxInvAgregateMap(Map<String, TaxInvoiceAgregate> taxInvAgregateMap) {
        this.taxInvAgregateMap = taxInvAgregateMap;
    }

    /**
     * @return the invoiceAmountWithoutTax
     */
    public BigDecimal getInvoiceAmountWithoutTax() {
        return invoiceAmountWithoutTax;
    }

    /**
     * @param invoiceAmountWithoutTax the invoiceAmountWithoutTax to set
     */
    public void setInvoiceAmountWithoutTax(BigDecimal invoiceAmountWithoutTax) {
        this.invoiceAmountWithoutTax = invoiceAmountWithoutTax;
    }

    /**
     * @return the invoiceAmountTax
     */
    public BigDecimal getInvoiceAmountTax() {
        return invoiceAmountTax;
    }

    /**
     * @param invoiceAmountTax the invoiceAmountTax to set
     */
    public void setInvoiceAmountTax(BigDecimal invoiceAmountTax) {
        this.invoiceAmountTax = invoiceAmountTax;
    }

    /**
     * @return the invoiceAmountWithTax
     */
    public BigDecimal getInvoiceAmountWithTax() {
        return invoiceAmountWithTax;
    }

    /**
     * @param invoiceAmountWithTax the invoiceAmountWithTax to set
     */
    public void setInvoiceAmountWithTax(BigDecimal invoiceAmountWithTax) {
        this.invoiceAmountWithTax = invoiceAmountWithTax;
    }
}