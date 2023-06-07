package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.TradingPricePlanMatrixLine;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class TradingPricePlanMatrixLineDto extends BaseEntityDto {

	private static final long serialVersionUID = -3498847257781821440L;

    @Schema(description = "trading price value")
	private BigDecimal tradingValue;

    @Schema(description = "trading currency")
	private TradingCurrencyDto tradingCurrency;

	@Schema(description = "the rate of trading currency")
    private BigDecimal rate;
    
	@Schema(description = "weither is will with billing account or not")
    private Boolean useForBillingAccounts;
	
	public TradingPricePlanMatrixLineDto() {
	}

	public TradingPricePlanMatrixLineDto(TradingPricePlanMatrixLine value) {
		tradingValue = value.getTradingValue();
		TradingCurrencyDto tradingCurrencyDto = new TradingCurrencyDto();
		tradingCurrencyDto.setId(value.getTradingCurrency().getId());
        tradingCurrencyDto.setCode(value.getTradingCurrency().getCurrencyCode());
		tradingCurrency = tradingCurrencyDto;
		rate = value.getRate();
		useForBillingAccounts = value.isUseForBillingAccounts();
	}

    public BigDecimal getTradingValue() {
        return tradingValue;
    }

    public TradingCurrencyDto getTradingCurrency() {
        return tradingCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Boolean getUseForBillingAccounts() {
        return useForBillingAccounts;
    }

    public void setTradingValue(BigDecimal tradingValue) {
        this.tradingValue = tradingValue;
    }

    public void setTradingCurrency(TradingCurrencyDto tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setUseForBillingAccounts(Boolean useForBillingAccounts) {
        this.useForBillingAccounts = useForBillingAccounts;
    }
	
}
