package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ejb.Stateful;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@Stateful
public class InvoiceAgregateHandler {
    private Logger log = LoggerFactory.getLogger(InvoiceAgregateHandler.class);

    private Map<String, CategoryInvoiceAgregate> catInvAgregateMap = new HashMap<String, CategoryInvoiceAgregate>();
    private Map<String, SubCategoryInvoiceAgregate> subCatInvAgregateMap = new HashMap<String, SubCategoryInvoiceAgregate>();
    private Map<String, TaxInvoiceAgregate> taxInvAgregateMap = new HashMap<String, TaxInvoiceAgregate>();

    private BigDecimal invoiceAmountWithoutTax = BigDecimal.ZERO;
    private BigDecimal invoiceAmountTax = BigDecimal.ZERO;
    private BigDecimal invoiceAmountWithTax = BigDecimal.ZERO;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;
	
	@Inject 
    private BillingAccountService billingAccountService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /**
     * reset default values.
     */
    public void reset() {
        catInvAgregateMap = new HashMap<String, CategoryInvoiceAgregate>();
        subCatInvAgregateMap = new HashMap<String, SubCategoryInvoiceAgregate>();
        taxInvAgregateMap = new HashMap<String, TaxInvoiceAgregate>();

        invoiceAmountWithoutTax = BigDecimal.ZERO;
        invoiceAmountTax = BigDecimal.ZERO;
        invoiceAmountWithTax = BigDecimal.ZERO;

    }

    /**
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account.
     * @param description description.
     * @param amountWithoutTax amount without tax
     * @throws BusinessException business exception
     */
    public void addInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, String description,
            BigDecimal amountWithoutTax) throws BusinessException {
        addLine(invoiceSubCategory, billingAccount, userAccount, description, amountWithoutTax, null);

    }

    /**
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account.
     * @param description description
     * @param amountWithoutTax amount without tax
     * @throws BusinessException business exception
     */
    public void removeInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, String description,
            BigDecimal amountWithoutTax) throws BusinessException {
        removeLine(invoiceSubCategory, billingAccount, userAccount, description, amountWithoutTax, null);

    }

    /**
     * Remove rated transaction.
     * 
     * @param description description
     * @param userAccount user account
     * @param ratedTRansaction rated transaction
     * @throws BusinessException business exception
     */
    public void removeRT(RatedTransaction ratedTRansaction, String description, UserAccount userAccount) throws BusinessException {
        InvoiceSubCategory invoiceSubCategory = ratedTRansaction.getInvoiceSubCategory();
        BillingAccount billingAccount = ratedTRansaction.getBillingAccount();
        removeLine(invoiceSubCategory, billingAccount, userAccount, description, ratedTRansaction.getAmountWithoutTax(), ratedTRansaction);
    }

    /**
     * 
     * @param ratedTRansaction rated transaction
     * @param description description
     * @param userAccount user account.
     * @throws BusinessException business exception
     */
    public void addRT(RatedTransaction ratedTRansaction, String description, UserAccount userAccount) throws BusinessException {
        InvoiceSubCategory invoiceSubCategory = ratedTRansaction.getInvoiceSubCategory();
        BillingAccount billingAccount = ratedTRansaction.getBillingAccount();
        if (ratedTRansaction.getAmountWithoutTax() == null) {
            if (ratedTRansaction.getUnitAmountWithoutTax() == null || ratedTRansaction.getQuantity() == null) {
                throw new BusinessException("RT.unitAmountWithoutTax or RT.quantity are null");
            }
            ratedTRansaction.setAmountWithoutTax(ratedTRansaction.getUnitAmountWithoutTax().multiply(ratedTRansaction.getQuantity()));
        }

        addLine(invoiceSubCategory, billingAccount, userAccount, description, ratedTRansaction.getAmountWithoutTax(), ratedTRansaction);
    }

    /**
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account.
     * @param description description.
     * @param amountWithoutTax amount without tax
     * @param ratedTransaction rated transaction.
     * @throws BusinessException business exception
     */
    private void addLine(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, String description, BigDecimal amountWithoutTax,
            RatedTransaction ratedTransaction) throws BusinessException {
        addOrRemoveLine(invoiceSubCategory, billingAccount, userAccount, description, amountWithoutTax, ratedTransaction, true);
    }

    /**
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account
     * @param description description.
     * @param amountWithoutTax amount without tax
     * @param ratedTransaction rated transaction
     * @throws BusinessException business exception
     */
    private void removeLine(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, String description, BigDecimal amountWithoutTax,
            RatedTransaction ratedTransaction) throws BusinessException {
        addOrRemoveLine(invoiceSubCategory, billingAccount, userAccount, description, amountWithoutTax, ratedTransaction, false);
    }

    /**
     * v5.0: Fix tax added to invoice net amount when a customer has "no tax applied"
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param billingAccount billing account
     * @param userAccount user account.
     * @param description description.
     * @param amountWithoutTax amount without tax
     * @param ratedTransaction rated transaction.
     * @param isToAdd true if it is to be added.
     * @throws BusinessException business exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private void addOrRemoveLine(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, String description, BigDecimal amountWithoutTax,
            RatedTransaction ratedTransaction, boolean isToAdd) throws BusinessException {
        log.debug("addOrRemoveLine amountWithoutTax {} ...", amountWithoutTax);

        Auditable auditable = new Auditable(currentUser);

        BigDecimal amountTax = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        Tax currentTax = getCurrentTax(invoiceSubCategory, userAccount, billingAccount);

        if (currentTax == null) {
            throw new BusinessException("Cant find tax for InvoiceSubCategory:" + invoiceSubCategory.getCode());
        }

        if (amountWithoutTax == null) {
            throw new BusinessException("AmountWithoutTax is null");
        }

        if (billingAccountService.isExonerated(billingAccount)) {
            amountWithTax = amountWithoutTax;
        } else {
            amountWithTax = getAmountWithTax(currentTax, amountWithoutTax);
            amountTax = getAmountTax(amountWithTax, amountWithoutTax);
        }
        log.trace("addOrRemoveLine amountWithTax {}", amountWithTax);
        log.trace("addOrRemoveLine amountTax {}", amountTax);

        invoiceAmountWithoutTax = addOrSubtract(invoiceAmountWithoutTax, amountWithoutTax, isToAdd);
        log.trace("addOrRemoveLine invoiceAmountWithoutTax {}", invoiceAmountWithoutTax);
        invoiceAmountTax = addOrSubtract(invoiceAmountTax, amountTax, isToAdd);
        log.trace("addOrRemoveLine invoiceAmountTax {}", invoiceAmountTax);
        invoiceAmountWithTax = addOrSubtract(invoiceAmountWithTax, amountWithTax, isToAdd);
        log.trace("addOrRemoveLine invoiceAmountWithTax {}", invoiceAmountWithTax);

        CategoryInvoiceAgregate categoryInvoiceAgregate = catInvAgregateMap.get(invoiceSubCategory.getInvoiceCategory().getCode());
        if (categoryInvoiceAgregate == null) {
            categoryInvoiceAgregate = new CategoryInvoiceAgregate();
            categoryInvoiceAgregate.setAuditable(auditable);
            categoryInvoiceAgregate.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
            categoryInvoiceAgregate.setDescription(invoiceSubCategory.getInvoiceCategory().getDescription());
            categoryInvoiceAgregate.setAmountWithoutTax(BigDecimal.ZERO);
            categoryInvoiceAgregate.setAmountWithTax(BigDecimal.ZERO);
            categoryInvoiceAgregate.setAmountTax(BigDecimal.ZERO);
            categoryInvoiceAgregate.setBillingAccount(billingAccount);
            categoryInvoiceAgregate.setUserAccount(userAccount);
            categoryInvoiceAgregate.setItemNumber(0);
        }
        categoryInvoiceAgregate.setAmountWithoutTax(addOrSubtract(categoryInvoiceAgregate.getAmountWithoutTax(), amountWithoutTax, isToAdd));
        categoryInvoiceAgregate.setAmountWithTax(addOrSubtract(categoryInvoiceAgregate.getAmountWithTax(), amountWithTax, isToAdd));
        categoryInvoiceAgregate.setAmountTax(addOrSubtract(categoryInvoiceAgregate.getAmountTax(), amountTax, isToAdd));

        categoryInvoiceAgregate.setItemNumber(categoryInvoiceAgregate.getItemNumber() + (isToAdd ? 1 : -1));

        if (categoryInvoiceAgregate.getItemNumber() > 0) {
            catInvAgregateMap.put(invoiceSubCategory.getInvoiceCategory().getCode(), categoryInvoiceAgregate);
        } else {
            catInvAgregateMap.remove(invoiceSubCategory.getInvoiceCategory().getCode());
        }

        SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = subCatInvAgregateMap.get(invoiceSubCategory.getCode());
        if (subCategoryInvoiceAgregate == null) {
            subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
            subCategoryInvoiceAgregate.setAuditable(auditable);
            subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
            subCategoryInvoiceAgregate.setInvoiceSubCategory(invoiceSubCategory);
            subCategoryInvoiceAgregate.setDescription(description);
            subCategoryInvoiceAgregate.setBillingRun(null);
            subCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
            subCategoryInvoiceAgregate.setUserAccount(userAccount);
            subCategoryInvoiceAgregate.setBillingAccount(billingAccount);
            subCategoryInvoiceAgregate.setAccountingCode(invoiceSubCategory.getAccountingCode());
            subCategoryInvoiceAgregate.setQuantity(BigDecimal.ONE);
            subCategoryInvoiceAgregate.setTaxPercent(currentTax.getPercent());
            subCategoryInvoiceAgregate.setTax(currentTax);
            subCategoryInvoiceAgregate.setAmountWithoutTax(BigDecimal.ZERO);
            subCategoryInvoiceAgregate.setAmountWithTax(BigDecimal.ZERO);
            subCategoryInvoiceAgregate.setAmountTax(BigDecimal.ZERO);
            subCategoryInvoiceAgregate.setItemNumber(0);
        }
        if (isToAdd) {
            if (ratedTransaction != null) {
                ratedTransaction.setAmountTax(amountTax);
                ratedTransaction.setAmountWithTax(amountWithTax);
                subCategoryInvoiceAgregate.getRatedtransactions().add(ratedTransaction);
            }

        } else {
            if (ratedTransaction != null) {
                subCategoryInvoiceAgregate.getRatedtransactions().remove(ratedTransaction);
            }
        }
        subCategoryInvoiceAgregate.setAmountWithoutTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountWithoutTax(), amountWithoutTax, isToAdd));
        subCategoryInvoiceAgregate.setAmountWithTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountWithTax(), amountWithTax, isToAdd));
        subCategoryInvoiceAgregate.setAmountTax(addOrSubtract(subCategoryInvoiceAgregate.getAmountTax(), amountTax, isToAdd));

        subCategoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregate.getItemNumber() + (isToAdd ? 1 : -1));

        if (subCategoryInvoiceAgregate.getItemNumber() > 0) {
            subCatInvAgregateMap.put(invoiceSubCategory.getCode(), subCategoryInvoiceAgregate);
        } else {
            subCatInvAgregateMap.remove(invoiceSubCategory.getCode());
        }

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

    /**
     * 
     * @param invoiceSubCategory invoice sub-category
     * @param userAccount user account.
     * @param billingAccount billing account
     * @return current tax.
     * @throws BusinessException business exception
     */
    private Tax getCurrentTax(InvoiceSubCategory invoiceSubCategory, UserAccount userAccount, BillingAccount billingAccount) throws BusinessException {
        for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
            if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode())
                    && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, null)) {
                Tax tax = invoiceSubCategoryCountryService.isInvoiceSubCategoryTaxValid(invoicesubcatCountry, userAccount, billingAccount, null, new Date());
                return tax;
            }
                }
        return null;
    }

    /**
     * 
     * @param expression EL expression
     * @param billingAccount billing account
     * @param invoice current invoice.
     * @return true if invoice match sub category country.
     * @throws BusinessException business exception
     */
    public boolean matchInvoicesubcatCountryExpression(String expression, BillingAccount billingAccount, Invoice invoice) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("iv") >= 0) {
            userMap.put("iv", invoice);
            userMap.put("invoice", invoice);

        }
        boolean result = ValueExpressionWrapper.evaluateToBoolean(expression, userMap);
        return result;
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
     * 
     * @param tax tax
     * @param amountWithoutTax amount without tax
     * @return amount with tax
     */
    private BigDecimal getAmountWithTax(Tax tax, BigDecimal amountWithoutTax) {
        return amountWithoutTax
            .add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100), appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode().getRoundingMode()));
    }

    /**
     * 
     * @param amountWithTax amount with tax
     * @param amountWithoutTax amount without tax
     * @return tax's amount.
     */
    private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax) {
        return amountWithTax.subtract(amountWithoutTax);
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
