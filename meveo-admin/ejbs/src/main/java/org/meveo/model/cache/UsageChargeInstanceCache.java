package org.meveo.model.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.meveo.model.BaseEntity;
import org.meveo.model.crm.Provider;

public class UsageChargeInstanceCache implements Comparable<UsageChargeInstanceCache> {

	private Long chargeInstanceId;
	private Provider provider;
	private Long currencyId;
	private Date lastUpdate;
	private BigDecimal unityMultiplicator = BigDecimal.ONE;
	private int unityNbDecimal = 2;
	int roundingUnityNbDecimal = 2;
	int roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
	private CounterInstanceCache counter;
	private Date chargeDate;
	private Date subscriptionDate;
	private Date terminationDate;
	private UsageChargeTemplateCache templateCache;

	public Long getChargeInstanceId() {
		return chargeInstanceId;
	}

	public void setChargeInstanceId(Long chargeInstanceId) {
		this.chargeInstanceId = chargeInstanceId;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public BigDecimal getUnityMultiplicator() {
		return unityMultiplicator;
	}

	public void setUnityMultiplicator(BigDecimal unityMultiplicator) {
		this.unityMultiplicator = unityMultiplicator;
		computeRoundingValues();
	}

	public int getUnityNbDecimal() {
		return unityNbDecimal;
	}

	public void setUnityNbDecimal(int unityNbDecimal) {
		this.unityNbDecimal = unityNbDecimal;
		computeRoundingValues();
	}

	private void computeRoundingValues() {
		try {
			if (unityNbDecimal >= BaseEntity.NB_DECIMALS) {
				roundingUnityNbDecimal = BaseEntity.NB_DECIMALS;
			} else {
				roundingUnityNbDecimal = unityNbDecimal;
				roundingEdrNbDecimal = (int) Math.round(unityNbDecimal
						+ Math.floor(Math.log10(unityMultiplicator.doubleValue())));
				if (roundingEdrNbDecimal > BaseEntity.NB_DECIMALS) {
					roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
				}
			}
		} catch (Exception e) {
		}
	}

	public CounterInstanceCache getCounter() {
		return counter;
	}

	public void setCounter(CounterInstanceCache counter) {
		this.counter = counter;
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public UsageChargeTemplateCache getTemplateCache() {
		return templateCache;
	}

	public void setTemplateCache(UsageChargeTemplateCache templateCache) {
		this.templateCache = templateCache;
	}

	@Override
	public int compareTo(UsageChargeInstanceCache o) {
		return this.getTemplateCache().getPriority() - o.getTemplateCache().getPriority();
	}

	public BigDecimal getInChargeUnit(BigDecimal edrUnitValue) {
		if (unityMultiplicator == null)
			unityMultiplicator = BigDecimal.ONE;
		BigDecimal result = edrUnitValue.multiply(unityMultiplicator);

		if (unityNbDecimal > 0) {
			result = result.setScale(roundingUnityNbDecimal, RoundingMode.HALF_UP);
		}

		return result;
	}

	public BigDecimal getInEDRUnit(BigDecimal chargeUnitValue) {
		return chargeUnitValue.divide(unityMultiplicator, roundingEdrNbDecimal, RoundingMode.HALF_UP);
	}
}
