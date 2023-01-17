package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConvertedPricePlanMatrixLineDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3498847257781821440L;

    @Schema(description = "converted price value")
	private BigDecimal convertedValue;

    @Schema(description = "trading currency")
	private TradingCurrencyDto tradingCurrency;

	@Schema(description = "the rate of converted currency")
    private BigDecimal rate;
    @Schema(description = "weither is will with billing account or not")
    private Boolean useForBillingAccounts;


    public BigDecimal getConvertedValue() {
        return convertedValue;
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



    public void setConvertedValue(BigDecimal convertedValue) {
        this.convertedValue = convertedValue;
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
