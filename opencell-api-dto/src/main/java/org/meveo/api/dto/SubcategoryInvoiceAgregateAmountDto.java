package org.meveo.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.SubcategoryInvoiceAgregateAmount;
import org.meveo.model.billing.Tax;

/**
 * Invoice subcategory amount by tax DTO
 * 
 * @author Andrius Karpavicius
 */
public class SubcategoryInvoiceAgregateAmountDto implements Serializable {

    private static final long serialVersionUID = -1536062903602729917L;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax;

    /**
     * Tax amount
     */
    private BigDecimal amountTax;

    /**
     * Tax applied
     */
    private TaxDto tax;

    /**
     * Instantiate
     */
    public SubcategoryInvoiceAgregateAmountDto() {
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amount Subcategory invoice aggregate amount
     * @param amountTax Tax amount
     */
    public SubcategoryInvoiceAgregateAmountDto(SubcategoryInvoiceAgregateAmount amount, Tax tax) {

        this.amountWithoutTax = amount.getAmountWithoutTax() == null ? BigDecimal.ZERO : amount.getAmountWithoutTax();
        this.amountWithTax = amount.getAmountWithTax() == null ? BigDecimal.ZERO : amount.getAmountWithTax();
        this.amountTax = amount.getAmountTax() == null ? BigDecimal.ZERO : amount.getAmountTax();
        this.tax = new TaxDto(tax, null, true);
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
     * @return Tax applied
     */
    public TaxDto getTax() {
        return tax;
    }

    /**
     * @param tax Tax applied
     */
    public void setTax(TaxDto tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "amountWithTax=" + amountWithTax + ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax;
    }
}