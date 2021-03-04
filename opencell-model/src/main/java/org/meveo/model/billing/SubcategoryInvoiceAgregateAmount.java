package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;

/**
 * Sub category invoice aggregate amounts by tax
 * 
 * @author Andrius Karpavicius
 */
@Embeddable
public class SubcategoryInvoiceAgregateAmount implements Serializable, Cloneable {

    private static final long serialVersionUID = -7075592208552089533L;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax = BigDecimal.ZERO;

    /**
     * Tax amount
     */
    private BigDecimal amountTax = BigDecimal.ZERO;

    /**
     * Instantiate
     */
    public SubcategoryInvoiceAgregateAmount() {
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     */
    public SubcategoryInvoiceAgregateAmount(BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
        this(amountWithoutTax, amountWithTax, null);
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Tax amount
     */
    public SubcategoryInvoiceAgregateAmount(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax) {
        super();

        if (amountWithoutTax == null) {
            this.amountWithoutTax = BigDecimal.ZERO;
        } else {
            this.amountWithoutTax = amountWithoutTax;
        }

        if (amountWithTax == null) {
            this.amountWithTax = BigDecimal.ZERO;
        } else {
            this.amountWithTax = amountWithTax;
        }

        if (amountTax == null) {
            this.amountTax = BigDecimal.ZERO;
        } else {
            this.amountTax = amountTax;
        }
    }

    /**
     * @return Amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * @param amountWithTax Amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * @return Amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax Amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * @return Tax amount
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * @param amountTax Tax amount
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Get one of the amounts requested
     * 
     * @param isWithTax Should return amount with tax
     * @return Amount with tax if isWithTax=true or amount without tax if isWithTax=false
     */
    public BigDecimal getAmount(boolean isWithTax) {
        if (isWithTax) {
            return amountWithTax;
        } else {
            return amountWithoutTax;
        }
    }

    @Override
    public String toString() {
        return "amountWithTax=" + amountWithTax + ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax;
    }

    /**
     * Add given amounts
     * 
     * @param amountWithoutTaxToAdd Amount without tax
     * @param amountWithTaxToAdd Amount with tax
     */
    public void addAmounts(BigDecimal amountWithoutTaxToAdd, BigDecimal amountWithTaxToAdd, BigDecimal amountTaxToAdd) {

        if (amountWithoutTaxToAdd != null) {
            this.amountWithoutTax = this.amountWithoutTax.add(amountWithoutTaxToAdd);
        }

        if (amountWithTaxToAdd != null) {
            this.amountWithTax = this.amountWithTax.add(amountWithTaxToAdd);
        }

        if (amountTaxToAdd != null) {
            this.amountTax = this.amountTax.add(amountTaxToAdd);
        }
    }

    /**
     * Add given amounts
     * 
     * @param amountsToAdd Amounts to add
     */
    public void addAmounts(SubcategoryInvoiceAgregateAmount amountsToAdd) {
        if (amountsToAdd == null) {
            return;
        }
        this.amountWithoutTax = this.amountWithoutTax.add(amountsToAdd.getAmountWithoutTax());
        this.amountWithTax = this.amountWithTax.add(amountsToAdd.getAmountWithTax());
        this.amountTax = this.amountTax.add(amountsToAdd.getAmountTax());
    }

    /**
     * Calculate derived amounts: amount with or without tax depending if its enterprise configuration based on other two amounts
     * 
     * @param isEnterprise If true, will recalculate amountWithTax as in amountWithoutTax - amountTax. If false will recalculate amountWithoutTax as in amountWithTax - amountTax
     */
    public void calculateDerivedAmounts(boolean isEnterprise) {
        if (isEnterprise) {
            amountWithTax = amountWithoutTax.add(amountTax);
        } else {
            amountWithoutTax = amountWithTax.subtract(amountTax);
        }
    }

    /**
     * Get a copy of the object
     *
     * @return A copy of an object
     */
    @Override
    public SubcategoryInvoiceAgregateAmount clone() {
        return new SubcategoryInvoiceAgregateAmount(amountWithoutTax, amountWithTax, amountTax);
    }

    /**
     * Negate amounts.
     *
     * @return a nagated amounts
     */
    public SubcategoryInvoiceAgregateAmount negate() {
        this.amountWithoutTax = this.amountWithoutTax.negate();
        this.amountWithTax = this.amountWithTax.negate();
        this.amountTax = this.amountTax.negate();
        return this;
    }
}