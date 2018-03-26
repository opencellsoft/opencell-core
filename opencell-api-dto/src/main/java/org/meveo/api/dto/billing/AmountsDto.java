package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.Amounts;

/**
 * Contains amounts with and without tax
 * 
 * @author Andrius Karpavicius
 * @since 5.0.1
 */
public class AmountsDto implements Serializable {

    private static final long serialVersionUID = 9184599956127715623L;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax;

    public AmountsDto() {
    }

    public AmountsDto(Amounts amounts) {
        super();
        this.amountWithTax = amounts.getAmountWithTax();
        this.amountWithoutTax = amounts.getAmountWithoutTax();
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