package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Contains amounts with and without tax
 * 
 * @author Andrius Karpavicius
 * @since 5.0.1
 */
public class Amounts implements Serializable {

    private static final long serialVersionUID = 9184599956127715623L;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax = BigDecimal.ZERO;

    /**
     * Instantiate
     */
    public Amounts() {
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     */
    public Amounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
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
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
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
        return "amountWithTax=" + amountWithTax + ", amountWithoutTax=" + amountWithoutTax;
    }

    /**
     * Add given amounts
     * 
     * @param amountWithoutTaxToAdd Amount without tax
     * @param amountWithTaxToAdd Amount with tax
     */
    public void addAmounts(BigDecimal amountWithoutTaxToAdd, BigDecimal amountWithTaxToAdd) {

        if (amountWithoutTaxToAdd != null) {
            this.amountWithoutTax = this.amountWithoutTax.add(amountWithoutTaxToAdd);
        }

        if (amountWithTaxToAdd != null) {
            this.amountWithTax = this.amountWithTax.add(amountWithTaxToAdd);
        }
    }

    /**
     * Add given amounts
     * 
     * @param amountsToAdd Amounts to add
     */
    public void addAmounts(Amounts amountsToAdd) {
        this.amountWithoutTax = this.amountWithoutTax.add(amountsToAdd.getAmountWithoutTax());
        this.amountWithTax = this.amountWithTax.add(amountsToAdd.getAmountWithTax());
    }
}