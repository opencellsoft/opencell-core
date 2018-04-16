package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.Amounts;

/**
 * Contains amounts with and without tax.
 *
 * @author Andrius Karpavicius
 * @since 5.0.1
 */
public class AmountsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9184599956127715623L;

    /** Amount with tax. */
    private BigDecimal amountWithTax;

    /** Amount without tax. */
    private BigDecimal amountWithoutTax;

    /**
     * Instantiates a new amounts dto.
     */
    public AmountsDto() {
    }

    /**
     * Instantiates a new amounts dto.
     *
     * @param amounts the amounts
     */
    public AmountsDto(Amounts amounts) {
        super();
        this.amountWithTax = amounts.getAmountWithTax();
        this.amountWithoutTax = amounts.getAmountWithoutTax();
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Get one of the amounts requested.
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