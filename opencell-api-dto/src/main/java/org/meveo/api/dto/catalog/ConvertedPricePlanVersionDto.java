package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CurrencyDto;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConvertedPricePlanVersionDto extends BaseEntityDto {

	private static final long serialVersionUID = 4515155675514862123L;

	private Long pricePlanMatrixVersionId;

	private BigDecimal convertedPrice;

	private BigDecimal rate;

	private boolean useForBillingAccounts;

	private CurrencyDto tradingCurrency;

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
	 * @return the convertedPrice
	 */
	public BigDecimal getConvertedPrice() {
		return convertedPrice;
	}

	/**
	 * @param convertedPrice the convertedPrice to set
	 */
	public void setConvertedPrice(BigDecimal convertedPrice) {
		this.convertedPrice = convertedPrice;
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
	 * @return the useForBillingAccounts
	 */
	public boolean isUseForBillingAccounts() {
		return useForBillingAccounts;
	}

	/**
	 * @param useForBillingAccounts the useForBillingAccounts to set
	 */
	public void setUseForBillingAccounts(boolean useForBillingAccounts) {
		this.useForBillingAccounts = useForBillingAccounts;
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
