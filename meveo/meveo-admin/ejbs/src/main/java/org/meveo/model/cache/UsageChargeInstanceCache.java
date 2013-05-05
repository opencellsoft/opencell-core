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

public class UsageChargeInstanceCache {

    private static Logger log=Logger.getLogger(UsageChargeInstanceCache.class.getName());
	
	private Long chargeInstanceId;
	private Provider provider;
	private Long currencyId;
	private Date lastUpdate;
	private BigDecimal unityMultiplicator = BigDecimal.ONE;
	private int unityNbDecimal = 2;
	private String filterExpression;
	private CounterInstanceCache counter;
	private Date chargeDate;
	private Date terminationDate;
	private String filter1;
	private String filter2;
	private String filter3;
	private String filter4;
	
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
	public String getFilterExpression() {
		return filterExpression;
	}
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
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
	
	public String getFilter1() {
		return filter1;
	}
	public void setFilter1(String filter1) {
		this.filter1 = filter1;
	}
	public String getFilter2() {
		return filter2;
	}
	public void setFilter2(String filter2) {
		this.filter2 = filter2;
	}
	public String getFilter3() {
		return filter3;
	}
	public void setFilter3(String filter3) {
		this.filter3 = filter3;
	}
	public String getFilter4() {
		return filter4;
	}
	public void setFilter4(String filter4) {
		this.filter4 = filter4;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	public Long getCurrencyId() {
		return currencyId;
	}
    
	
}
