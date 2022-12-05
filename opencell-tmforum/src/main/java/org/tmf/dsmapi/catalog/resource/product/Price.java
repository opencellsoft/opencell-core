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

package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.commons.OutputUtilities;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pierregauthier
 * 
 *         { "taxIncludedAmount": "12.00", "dutyFreeAmount": "10.00", "taxRate": "20.00", "currencyCode": "EUR", "percentage": 0 }
 * 
 */
@XmlType(name = "Price", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class Price implements Serializable {
    private final static long serialVersionUID = 1L;

    private BigDecimal taxIncludedAmount;

    private BigDecimal dutyFreeAmount;

    private BigDecimal taxRate;

    private String currencyCode;

    private BigDecimal percentage;

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
        return "Price{" + "taxIncludedAmount=" + taxIncludedAmount + ", dutyFreeAmount=" + dutyFreeAmount + ", taxRate=" + taxRate + ", currencyCode=" + currencyCode
                + ", percentage=" + percentage + '}';
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
