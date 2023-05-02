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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.catalog.DiscountPlanItem;

/**
 * The Class SubCategoryInvoiceAgregate.
 */
@Entity
@DiscriminatorValue("F")
@NamedQueries({ @NamedQuery(name = "SubCategoryInvoiceAgregate.deleteByBR", query = "delete from SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId AND ia.invoice.id in (select ia2.invoice.id from SubCategoryInvoiceAgregate ia2 where ia2.invoice.status <> org.meveo.model.billing.InvoiceStatusEnum.VALIDATED)"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByBillingAccount", query = "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.subscription.id, ia.invoice.commercialOrder.id ,ia.invoice.id ,ia.billingAccount.id,  ia.billingAccount.customerAccount.id, ia.billingAccount.customerAccount.customer.id"
                + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.subscription.id, ia.invoice.commercialOrder.id , ia.invoice.id, ia.billingAccount.id, ia.billingAccount.customerAccount.id, ia.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByCustomerAccount", query = "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.id, ia.billingAccount.customerAccount.id"
                + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.id, ia.billingAccount.customerAccount.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByCustomer", query = "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.id, ia.billingAccount.customerAccount.customer.id"
                + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.id, ia.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.deleteByInvoiceIds", query = "delete from SubCategoryInvoiceAgregate ia where ia.invoice.id IN (:invoicesIds)"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.moveToQuarantineBRByInvoiceIds", query = "update SubCategoryInvoiceAgregate ia set ia.billingRun=:billingRun where ia.invoice.id IN (:invoiceIds)"),
        @NamedQuery(name = "SubCategoryInvoiceAggregate.removeInvoiceAggregateReferences", query = "UPDATE SubCategoryInvoiceAgregate ia set ia.categoryInvoiceAgregate= null WHERE ia.categoryInvoiceAgregate.id IN (:categoryInvoiceAggregateIds)")})
public class SubCategoryInvoiceAgregate extends InvoiceAgregate {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice sub category. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicesubcategory")
    private InvoiceSubCategory invoiceSubCategory;

    /** The category invoice agregate. */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_invoice_agregate")
    private CategoryInvoiceAgregate categoryInvoiceAgregate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /** The wallet. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /** The discount plan code. */
    @Column(name = "discount_plan_code", length = 50)
    @Size(max = 50)
    private String discountPlanCode;

    /** The discount plan item code. */
    @Column(name = "discount_plan_item_code", length = 50)
    @Size(max = 50)
    private String discountPlanItemCode;

    /**
     * Discount plan item applied
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
    private DiscountPlanItem discountPlanItem;

    /**
     * Discount percent applied
     */
    @Column(name = "discount_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountPercent;

    // Field no longer used since v.5.2
    @Deprecated
    @Column(name = "quantity")
    private BigDecimal quantity;

    // Field no longer used since v.5.2
    @Deprecated
    @Column(name = "discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discount;

    @Type(type = "numeric_boolean")
    @Column(name = "discount_aggregate", nullable = false)
    @NotNull
    private boolean discountAggregate;

    /** The old amount without tax. */
    @Transient
    private BigDecimal oldAmountWithoutTax;

    /** The old amount with tax. */
    @Transient
    private BigDecimal oldAmountWithTax;

    @Transient
    private List<IInvoiceable> invoiceablesToAssociate = new ArrayList<>();

    @Transient
    private List<InvoiceLine> invoiceLinesToAssociate = new ArrayList<>();

    /**
     * Tracks cumulative amounts by tax
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "billing_invoice_agr_amount", joinColumns = { @JoinColumn(name = "aggr_id") })
    @MapKeyJoinColumn(name = "tax_id")
//    @AttributeOverrides(value = { @AttributeOverride(name = "amountWithoutTax", column = @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)),
//            @AttributeOverride(name = "amountTax", column = @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)),
//            @AttributeOverride(name = "amountWithTax", column = @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)) })
    private Map<Tax, SubcategoryInvoiceAgregateAmount> amountsByTax;

    /**
     * Instantiates a new sub category invoice aggregate.
     */
    public SubCategoryInvoiceAgregate() {

    }

    /**
     * Instantiates a new sub category invoice aggregate
     * 
     * @param invoiceSubCategory Invoice subcategory
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param wallet Wallet instance
     * @param invoice Invoice
     * @param accountingCode Accounting code
     */
    public SubCategoryInvoiceAgregate(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, WalletInstance wallet, Invoice invoice, AccountingCode accountingCode) {
        super();
        this.invoiceSubCategory = invoiceSubCategory;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.wallet = wallet;
        this.invoice = invoice;
        if (invoice != null) {
            this.billingRun = invoice.getBillingRun();
        }
        this.accountingCode = accountingCode;
    }

    /**
     * Copies sub category invoice aggregate
     *
     * @param subCategoryInvoiceAgregate Sub category invoice aggregate to copy
     */
    public SubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        this.setAccountingCode(subCategoryInvoiceAgregate.getAccountingCode());
        this.setInvoiceSubCategory(subCategoryInvoiceAgregate.getInvoiceSubCategory());
        this.setWallet(subCategoryInvoiceAgregate.getWallet());
        this.setItemNumber(subCategoryInvoiceAgregate.getItemNumber());
        this.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
        this.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
        this.setAmountTax(subCategoryInvoiceAgregate.getAmountTax());
        this.setBillingAccount(subCategoryInvoiceAgregate.getBillingAccount());
        this.setBillingRun(subCategoryInvoiceAgregate.getBillingRun());
        this.setUserAccount(subCategoryInvoiceAgregate.getUserAccount());
        this.setDiscountAggregate(false);
    }

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * Gets the category invoice agregate.
     *
     * @return the category invoice agregate
     */
    public CategoryInvoiceAgregate getCategoryInvoiceAgregate() {
        return categoryInvoiceAgregate;
    }

    /**
     * Sets the category invoice agregate.
     *
     * @param categoryInvoiceAgregate the new category invoice agregate
     */
    public void setCategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
        this.categoryInvoiceAgregate = categoryInvoiceAgregate;
        if (categoryInvoiceAgregate != null && categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
            categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().add(this);
        }
    }

    /**
     * Associate Invoiceable data to an aggregate, increment the itemNumber field value and sum up amounts.
     * 
     * @param invoiceable Rated transaction to associate
     * @param isEnterprise Is it enterprise/b2b installation - interested to track
     * @param updateAmounts Shall Subcategory aggregate amounts be updated with values of rated transaction amounts
     */
    public void addInvoiceable(IInvoiceable invoiceable, boolean isEnterprise, boolean updateAmounts) {

        if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber++;
        this.invoiceablesToAssociate.add(invoiceable);

        if (updateAmounts) {
            if (isEnterprise) {
                addAmountWithoutTax(invoiceable.getAmountWithoutTax());
            } else {
                addAmountWithTax(invoiceable.getAmountWithTax());
            }
            addAmountTax(invoiceable.getAmountTax());

            if (amountsByTax == null) {
                amountsByTax = new LinkedHashMap<>();
            }
            if (!amountsByTax.containsKey(invoiceable.getTax())) {
                amountsByTax.put(invoiceable.getTax(), new SubcategoryInvoiceAgregateAmount(invoiceable.getAmountWithoutTax(), invoiceable.getAmountWithTax(), invoiceable.getAmountTax()));
            } else {
                amountsByTax.get(invoiceable.getTax()).addAmounts(invoiceable.getAmountWithoutTax(), invoiceable.getAmountWithTax(), invoiceable.getAmountTax());
            }
        }
    }

    /**
     * Disassociate Rated transaction to an aggregate, decrement the itemNumber field value and deduce amounts.
     * 
     * @param ratedTransaction Rated transaction to associate
     * @param isEnterprise Is it enterprise/b2b installation - interested to track
     * @param updateAmounts Shall Subcategory aggregate amounts be updated with values of rated transaction amounts
     */
    public void removeRatedTransaction(RatedTransaction ratedTransaction, boolean isEnterprise, boolean updateAmounts) {

        if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber--;
        if (this.itemNumber < 0) {
            this.itemNumber = 0;
        }
        this.invoiceablesToAssociate.remove(ratedTransaction);

        if (updateAmounts) {
            if (isEnterprise) {
                subtractAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
            } else {
                subtractAmountWithTax(ratedTransaction.getAmountWithTax());
            }
            subtractAmountTax(ratedTransaction.getAmountTax());

            if (amountsByTax == null) {
                amountsByTax = new LinkedHashMap<>();
            }
            if (amountsByTax.containsKey(ratedTransaction.getTax())) {
                amountsByTax.get(ratedTransaction.getTax()).subtractAmounts(ratedTransaction.getAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax());
                if (amountsByTax.get(ratedTransaction.getTax()).checkIsAllZeroAmounts()) {
                    amountsByTax.remove(ratedTransaction.getTax());
                }
            }
        }
    }

    /**
     * @return User account
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount User account
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return Wallet instance
     */
    public WalletInstance getWallet() {
        return wallet;
    }

    /**
     * @param wallet Wallet instance
     */
    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    /**
     * @return Corresponding accounting code
     */
    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    /**
     * @param accountingCode Corresponding accounting code
     */
    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * @return the discount plan code
     */
    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    /**
     * Sets the discount plan code.
     *
     * @param discountPlanCode the new discount plan code
     */
    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    /**
     * Gets the discount plan item code.
     *
     * @return the discount plan item code
     */
    public String getDiscountPlanItemCode() {
        return discountPlanItemCode;
    }

    /**
     * Sets the discount plan item code.
     *
     * @param discountPlanItemCode the new discount plan item code
     */
    public void setDiscountPlanItemCode(String discountPlanItemCode) {
        this.discountPlanItemCode = discountPlanItemCode;
    }

    /**
     * @return Discount percent applied
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent Discount percent applied
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * Gets the old amount without tax.
     *
     * @return the old amount without tax
     */
    public BigDecimal getOldAmountWithoutTax() {
        return oldAmountWithoutTax;
    }

    /**
     * Sets the old amount without tax.
     *
     * @param oldAmountWithoutTax the new old amount without tax
     */
    public void setOldAmountWithoutTax(BigDecimal oldAmountWithoutTax) {
        this.oldAmountWithoutTax = oldAmountWithoutTax;
    }

    /**
     * Gets the old amount with tax.
     *
     * @return the old amount with tax
     */
    public BigDecimal getOldAmountWithTax() {
        return oldAmountWithTax;
    }

    /**
     * Sets the old amount with tax.
     *
     * @param oldAmountWithTax the new old amount with tax
     */
    public void setOldAmountWithTax(BigDecimal oldAmountWithTax) {
        this.oldAmountWithTax = oldAmountWithTax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.model.BaseEntity#hashCode()
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.intValue();
        } else if (invoiceSubCategory != null) {
            return invoiceSubCategory.hashCode();
        }

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof SubCategoryInvoiceAgregate)) {
            return false;
        }

        SubCategoryInvoiceAgregate other = (SubCategoryInvoiceAgregate) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (this.getInvoiceSubCategory() == null) {
            return false;
        }

        return this.getInvoiceSubCategory().getCode().equals(other.getInvoiceSubCategory().getCode())
                && ((this.getUserAccount() == null && other.getUserAccount() == null)
                        || (this.getUserAccount() != null && other.getUserAccount() != null && this.getUserAccount().getId().equals(other.getUserAccount().getId())))
                && this.isDiscountAggregate() == other.isDiscountAggregate();
    }

    @Override
    public String toString() {
        return "SubCategoryInvoiceAgregate [id=" + id + ",invoiceSubCategory=" + (invoiceSubCategory == null ? null : invoiceSubCategory.getCode()) + ", oldAmountWithoutTax=" + oldAmountWithoutTax + ", oldAmountWithTax="
                + oldAmountWithTax + "]";
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @return Quantity
     */
    @Deprecated
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param quantity Quantity
     */
    @Deprecated
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param quantity Quantity
     */
    @Deprecated
    public void addQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @return Discount
     */
    @Deprecated
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param discount Discount
     */
    @Deprecated
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * @return Is this a discount aggregate
     */
    public boolean isDiscountAggregate() {
        return discountAggregate;
    }

    /**
     * @param discountAggregate Is this a discount aggregate
     */
    public void setDiscountAggregate(boolean discountAggregate) {
        this.discountAggregate = discountAggregate;
    }

    /**
     * @return Discount applied
     */
    public DiscountPlanItem getDiscountPlanItem() {
        return discountPlanItem;
    }

    /**
     * @param discountPlanItem Discount applied
     */
    public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
        this.discountPlanItem = discountPlanItem;
    }

    public BigDecimal getIsEnterpriseAmount(boolean isEnterprise) {
        return isEnterprise ? getAmountWithoutTax() : getAmountWithTax();
    }

    /**
     * A transient method.
     * 
     * @return A list of rated transactions to associate with an invoice subcategory aggregate.
     */
    public List<? extends IInvoiceable> getInvoiceablesToAssociate() {
        return invoiceablesToAssociate;
    }

    /**
     * A transient method.
     * 
     * @param invoiceablesToAssociate A list of invoiceable data to associate with an invoice subcategory aggregate
     */
    public void setInvoiceablesToAssociate(List<IInvoiceable> invoiceablesToAssociate) {
        this.invoiceablesToAssociate = invoiceablesToAssociate;
    }

    /**
     * Compute derived amounts amountWithoutTax/amountWithTax/amountTax. If taxPercent is null, or ZERO returned amountWithoutTax and amountWithTax values will be the same (which one, depending on isEnterprise value)
     * 
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply for amounts by tax amounts
     * @param roundingMode Rounding mode to apply for amounts by tax amounts
     * @param invoiceRounding Rounding precision to apply for category aggregate amounts
     * @param invoiceRoundingMode Rounding mode to apply for category aggregate amounts
     */
    public void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingMode roundingMode, int invoiceRounding, RoundingMode invoiceRoundingMode) {
        if (amountsByTax != null) {
            setAmountWithoutTax(BigDecimal.ZERO);
            setAmountWithTax(BigDecimal.ZERO);
            setAmountTax(BigDecimal.ZERO);

            for (Entry<Tax, SubcategoryInvoiceAgregateAmount> amountInfo : amountsByTax.entrySet()) {

                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amountInfo.getValue().getAmountWithoutTax(), amountInfo.getValue().getAmountWithTax(), amountInfo.getKey().getPercent(), isEnterprise, rounding,
                    roundingMode);
                amountInfo.getValue().setAmountWithoutTax(amounts[0]);
                amountInfo.getValue().setAmountWithTax(amounts[1]);
                amountInfo.getValue().setAmountTax(amounts[2]);

                amounts = NumberUtils.computeDerivedAmounts(amountInfo.getValue().getAmountWithoutTax(), amountInfo.getValue().getAmountWithTax(), amountInfo.getKey().getPercent(), isEnterprise, invoiceRounding,
                    invoiceRoundingMode);

                addAmountWithoutTax(amounts[0]);
                addAmountWithTax(amounts[1]);
                addAmountTax(amounts[2]);
            }

        } else {
            BigDecimal[] amounts = NumberUtils.computeDerivedAmountsWoutTaxPercent(getAmountWithoutTax(), getAmountWithTax(), getAmountTax(), isEnterprise, invoiceRounding, invoiceRoundingMode);
            setAmountWithoutTax(amounts[0]);
            setAmountWithTax(amounts[1]);
            setAmountTax(amounts[2]);
        }
    }

    /**
     * @return Cumulative amounts by tax
     */
    public Map<Tax, SubcategoryInvoiceAgregateAmount> getAmountsByTax() {
        return amountsByTax;
    }

    /**
     * @return Cumulative amounts by tax as a list of Amounts objects
     */
    public List<Amounts> getAmountByTaxAsList() {
        List<Amounts> amountsByTaxAsList = new ArrayList<Amounts>();
        for (Entry<Tax, SubcategoryInvoiceAgregateAmount> amountEntry : getAmountsByTax().entrySet()) {
            amountsByTaxAsList.add(new Amounts(amountEntry.getValue().getAmountWithoutTax(), amountEntry.getValue().getAmountWithTax(), amountEntry.getValue().getAmountTax(), amountEntry.getKey()));
        }

        return amountsByTaxAsList;
    }

    /**
     * @param amountsByTax Cumulative amounts by tax
     */
    public void setAmountsByTax(Map<Tax, SubcategoryInvoiceAgregateAmount> amountsByTax) {
        this.amountsByTax = amountsByTax;
    }

    public List<InvoiceLine> getInvoiceLinesToAssociate() {
        return invoiceLinesToAssociate;
    }

    public void setInvoiceLinesToAssociate(List<InvoiceLine> invoiceLinesToAssociate) {
        this.invoiceLinesToAssociate = invoiceLinesToAssociate;
    }

    public void addInvoiceLine(InvoiceLine invoiceLine, boolean isEnterprise, boolean addAmounts) {

        if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber++;
        this.invoiceLinesToAssociate.add(invoiceLine);

        BigDecimal amount = isEnterprise? invoiceLine.getAmountWithoutTax(): invoiceLine.getAmountWithTax();
        if(addAmounts) {
            if (isEnterprise) {
                addAmountWithoutTax(invoiceLine.getAmountWithoutTax());
            } else {
                addAmountWithTax(invoiceLine.getAmountWithTax());
            }
            addAmountTax(invoiceLine.getAmountTax());
        }
        if (amountsByTax == null) {
            amountsByTax = new HashMap<>();
        }
        if (!amountsByTax.containsKey(invoiceLine.getTax())) {
            amountsByTax.put(invoiceLine.getTax(), new SubcategoryInvoiceAgregateAmount(invoiceLine.getAmountWithoutTax(), invoiceLine.getAmountWithTax(), invoiceLine.getAmountTax()));
        } else {
            SubcategoryInvoiceAgregateAmount subcategoryInvoiceAgregate = amountsByTax.get(invoiceLine.getTax());
            subcategoryInvoiceAgregate.addAmounts(invoiceLine.getAmountWithoutTax(), invoiceLine.getAmountWithTax(), invoiceLine.getAmountTax());
            amountsByTax.put(invoiceLine.getTax(), subcategoryInvoiceAgregate);
        }
    }
    
    public String getCategoryAggKey() {
    	return ""+(this.userAccount==null?"":this.userAccount.getId())+this.invoiceSubCategory.getInvoiceCategory().getId();
    }
    
    @Transient
    private List<Long> ilIDs=new ArrayList<>();
    public void addILs(List<Long> ilIDs) {
    	this.ilIDs.addAll(ilIDs);
    }
    public List<Long> getIlIDs() {
    	return this.ilIDs;
    }

    /**
     * Set cumulative amounts by tax
     *
     * @param amountsByTax Amounts by tax - just as number
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     */
    public void setAmountsByTax(Map<Tax, BigDecimal> amountsByTax, boolean isEnterprise) {
        this.amountsByTax = new LinkedHashMap<Tax, SubcategoryInvoiceAgregateAmount>();

        for (Entry<Tax, BigDecimal> amountInfo : amountsByTax.entrySet()) {
            this.amountsByTax.put(amountInfo.getKey(), new SubcategoryInvoiceAgregateAmount(isEnterprise ? amountInfo.getValue() : null, isEnterprise ? null : amountInfo.getValue()));
        }
    }
}
