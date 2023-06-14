package org.meveo.api.dto.cpq;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CurrencyDto;

@XmlAccessorType(XmlAccessType.FIELD)
public class TradingContractItemDto extends BaseEntityDto {

	private static final long serialVersionUID = 1L;

	private Long contractItemId;

	private BigDecimal tradingValue;

	private BigDecimal rate;

	private CurrencyDto tradingCurrency;

	/**
	 * @return the contractItemId
	 */
	public Long getContractItemId() {
		return contractItemId;
	}

	/**
	 * @param contractItemId the contractItemId to set
	 */
	public void setContractItemId(Long contractItemId) {
		this.contractItemId = contractItemId;
	}

	/**
	 * @return the tradingValue
	 */
	public BigDecimal getTradingValue() {
		return tradingValue;
	}

	/**
	 * @param tradingValue the tradingValue to set
	 */
	public void setTradingValue(BigDecimal tradingValue) {
		this.tradingValue = tradingValue;
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
