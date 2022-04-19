package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

public class ExchangeRateDto {

    private Date fromDate;
    
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
