package org.meveo.model.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.infinispan.Cache;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;

public class UsageChargeInstanceCache implements Comparable<UsageChargeInstanceCache>{

    private static Logger log=Logger.getLogger(UsageChargeInstanceCache.class.getName());
	
	private Long chargeInstanceId;
	private Provider provider;
	private Long currencyId;
	private Date lastUpdate;
	private BigDecimal unityMultiplicator = BigDecimal.ONE;
	private int unityNbDecimal = 2;
	private CounterInstanceCache counter;
	private Date chargeDate;
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
	}
	public int getUnityNbDecimal() {
		return unityNbDecimal;
	}
	public void setUnityNbDecimal(int unityNbDecimal) {
		this.unityNbDecimal = unityNbDecimal;
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
		return this.getTemplateCache().getPriority()-o.getTemplateCache().getPriority();
	}
	
}
