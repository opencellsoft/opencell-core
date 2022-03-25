package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

public class ExchangeRateDto {

    private Date fromDate;
    
    @NotNull
    private BigDecimal exchangeRate;

    public ExchangeRateDto() {
        super();
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
