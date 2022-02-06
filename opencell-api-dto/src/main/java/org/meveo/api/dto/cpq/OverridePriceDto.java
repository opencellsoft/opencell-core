package org.meveo.api.dto.cpq;

import java.math.BigDecimal;

public class OverridePriceDto {

    private Long offerId;
    private String accountingArticleCode;
    private BigDecimal unitAmountWithoutTax;
    private Boolean priceOverCharged;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getAccountingArticleCode() {
        return accountingArticleCode;
    }

    public void setAccountingArticleCode(String accountingArticleCode) {
        this.accountingArticleCode = accountingArticleCode;
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
