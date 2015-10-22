package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.OutputUtilities;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author pierregauthier
 *
 * {
 *     "taxIncludedAmount": "12.00",
 *     "dutyFreeAmount": "10.00",
 *     "taxRate": "20.00",
 *     "currencyCode": "EUR",
 *     "percentage": 0
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class Price implements Serializable {
    private final static long serialVersionUID = 1L;

    @Column(name = "TAX_INCLUDED_AMOUNT", nullable = true)
    BigDecimal taxIncludedAmount;

    @Column(name = "DUTY_FREE_AMOUNT", nullable = true)
    BigDecimal dutyFreeAmount;

    @Column(name = "TAX_RATE", nullable = true)
    BigDecimal taxRate;

    @Column(name = "CURRENCY_CODE", nullable = true)
    String currencyCode;
    
    @Column(name = "PERCENTAGE", nullable = true)
    BigDecimal percentage;

    public Price() {
    }

    public BigDecimal getTaxIncludedAmount() {
        return taxIncludedAmount;
    }

    public void setTaxIncludedAmount(BigDecimal taxIncludedAmount) {
        this.taxIncludedAmount = taxIncludedAmount;
    }

    public BigDecimal getDutyFreeAmount() {
        return dutyFreeAmount;
    }

    public void setDutyFreeAmount(BigDecimal dutyFreeAmount) {
        this.dutyFreeAmount = dutyFreeAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @JsonProperty(value = "taxIncludedAmount")
    public String taxIncludedAmountToJson() {
        return OutputUtilities.formatCurrency(taxIncludedAmount);
    }

    @JsonProperty(value = "dutyFreeAmount")
    public String dutyFreeAmountToJson() {
        return OutputUtilities.formatCurrency(dutyFreeAmount);
    }

    @JsonProperty(value = "taxRate")
    public String taxRateToJson() {
        return OutputUtilities.formatCurrency(taxRate);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;

        hash = 53 * hash + (this.taxIncludedAmount != null ? this.taxIncludedAmount.hashCode() : 0);
        hash = 53 * hash + (this.dutyFreeAmount != null ? this.dutyFreeAmount.hashCode() : 0);
        hash = 53 * hash + (this.taxRate != null ? this.taxRate.hashCode() : 0);
        hash = 53 * hash + (this.currencyCode != null ? this.currencyCode.hashCode() : 0);
        hash = 53 * hash + (this.percentage != null ? this.percentage.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Price other = (Price) object;
        if (Utilities.areEqual(this.taxIncludedAmount, other.taxIncludedAmount) == false) {
            return false;
        }

        if (Utilities.areEqual(this.dutyFreeAmount, other.dutyFreeAmount) == false) {
            return false;
        }

        if (Utilities.areEqual(this.taxRate, other.taxRate) == false) {
            return false;
        }

        if (Utilities.areEqual(this.currencyCode, other.currencyCode) == false) {
            return false;
        }

        if (Utilities.areEqual(this.percentage, other.percentage) == false) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "Price{" + "taxIncludedAmount=" + taxIncludedAmount + ", dutyFreeAmount=" + dutyFreeAmount + ", taxRate=" + taxRate + ", currencyCode=" + currencyCode + ", percentage=" + percentage + '}';
    }

    public static Price createProto() {
        Price price = new Price();

        price.taxIncludedAmount = new BigDecimal(13.00);
        price.dutyFreeAmount = new BigDecimal(12.20);
        price.taxRate = new BigDecimal(14.01);
        price.currencyCode = "currency code";
        price.percentage = new BigDecimal(0.00);

        return price;
    }

}
