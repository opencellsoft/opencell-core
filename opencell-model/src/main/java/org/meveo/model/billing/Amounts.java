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
     * Amount with tax
     */
    private BigDecimal amountWithTax;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax;

    public Amounts() {
    }

    public Amounts(BigDecimal amountWithTax, BigDecimal amountWithoutTax) {
        super();
        if (amountWithTax == null) {
            this.amountWithTax = BigDecimal.ZERO;
        } else {
            this.amountWithTax = amountWithTax;
        }
        if (amountWithoutTax == null) {
            this.amountWithoutTax = BigDecimal.ZERO;
        } else {
            this.amountWithoutTax = amountWithoutTax;
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
}