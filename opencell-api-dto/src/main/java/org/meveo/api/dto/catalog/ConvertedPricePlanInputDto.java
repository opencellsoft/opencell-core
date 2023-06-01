package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConvertedPricePlanInputDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3498847257781821440L;

    @Schema(description = "price plan matrix version id")
	private Long pricePlanMatrixVersionId;

    @Schema(description = "trading currency")
	private TradingCurrencyDto tradingCurrency;

	/**
	 * @return the pricePlanMatrixVersionId
	 */
	public Long getPricePlanMatrixVersionId() {
		return pricePlanMatrixVersionId;
	}

	/**
	 * @param pricePlanMatrixVersionId the pricePlanMatrixVersionId to set
	 */
	public void setPricePlanMatrixVersionId(Long pricePlanMatrixVersionId) {
		this.pricePlanMatrixVersionId = pricePlanMatrixVersionId;
	}

	/**
	 * @return the tradingCurrency
	 */
	public TradingCurrencyDto getTradingCurrency() {
		return tradingCurrency;
	}

	/**
	 * @param tradingCurrency the tradingCurrency to set
	 */
	public void setTradingCurrency(TradingCurrencyDto tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}


	
}
