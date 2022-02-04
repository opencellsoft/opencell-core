package org.meveo.api.dto.cpq;

import java.math.BigDecimal;

public class OverridePriceDto {

    private Long id;
    private BigDecimal unitAmountWithoutTax;
    private Boolean priceOverCharged;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public Boolean getPriceOverCharged() {
        return priceOverCharged;
    }

    public void setPriceOverCharged(Boolean priceOverCharged) {
        this.priceOverCharged = priceOverCharged;
    }
}
