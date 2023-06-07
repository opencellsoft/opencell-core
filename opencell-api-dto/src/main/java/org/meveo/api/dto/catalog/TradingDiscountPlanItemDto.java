package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CurrencyDto;

@XmlAccessorType(XmlAccessType.FIELD)
public class TradingDiscountPlanItemDto extends BaseEntityDto {

	private static final long serialVersionUID = 1L;

	private Long discountPlanItemId;

	private BigDecimal tradingDiscountValue;

	private BigDecimal rate;

	private CurrencyDto tradingCurrency;

	/**
	 * @return the discountPlanItemId
	 */
	public Long getDiscountPlanItemId() {
		return discountPlanItemId;
	}

	/**
	 * @param discountPlanItemId the discountPlanItemId to set
	 */
	public void setDiscountPlanItemId(Long discountPlanItemId) {
		this.discountPlanItemId = discountPlanItemId;
	}

	/**
	 * @return the tradingDiscountValue
	 */
	public BigDecimal getTradingDiscountValue() {
		return tradingDiscountValue;
	}

	/**
	 * @param tradingDiscountValue the tradingDiscountValue to set
	 */
	public void setTradingDiscountValue(BigDecimal tradingDiscountValue) {
		this.tradingDiscountValue = tradingDiscountValue;
	}

	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	/**
	 * @return the tradingCurrency
	 */
	public CurrencyDto getTradingCurrency() {
		return tradingCurrency;
	}

	/**
	 * @param tradingCurrency the tradingCurrency to set
	 */
	public void setTradingCurrency(CurrencyDto tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

}
