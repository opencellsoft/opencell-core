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
	
	
	public static void putInCache(UsageChargeInstance usageChargeInstance,
			Cache<Long, List<UsageChargeInstanceCache>> usageCache,Cache<Long, CounterInstanceCache> counterCache) {
		if(usageChargeInstance!=null){
			
			UsageChargeInstanceCache cachedValue = new UsageChargeInstanceCache();
			UsageChargeTemplate usageChargeTemplate=(UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
			
			Long key = usageChargeInstance.getServiceInstance().getSubscription().getId();
			log.info("put in cache key (subs Id)="+key);
			boolean cacheContainsKey=usageCache.containsKey(key);
			boolean cacheContainsCharge=false;

			List<UsageChargeInstanceCache> charges = null;
			if(cacheContainsKey){
				log.info("the cache contains the key");
				charges = usageCache.get(key);
				for(UsageChargeInstanceCache charge:charges){
					if(charge.getChargeInstanceId()==usageChargeInstance.getId()){
						if(charge.getLastUpdate().before(usageChargeInstance.getLastUpdate())){
							log.info("the cache contains the charge and is dirty, so it is updated");
							cachedValue=charge;//cache is older than DB, we will update it
							cacheContainsCharge=true;
							//TODO: check that it works for associated counters	
						} else {
							log.info("the cache contains the charge but is not dirty, we do not update it");
							//DB is older than cache.. we dont update the cache
							return;
							//TODO: make sure this is what we want
						}
					}
				}
			} else {
				log.info("the cache does not contains the key");
				charges =  new ArrayList<UsageChargeInstanceCache>();
			}
			
			cachedValue.setChargeDate(usageChargeInstance.getChargeDate());
			cachedValue.setChargeInstanceId(usageChargeInstance.getId());
			cachedValue.setProvider(usageChargeInstance.getProvider());
			cachedValue.setCurrencyId(usageChargeInstance.getServiceInstance().getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency().getId());
			if(usageChargeInstance.getCounter()!=null){
				CounterInstanceCache counterCacheValue = null;
				Long counterKey = CounterInstanceCache.getKey(usageChargeInstance.getCounter());

				log.info("counter key:"+counterKey);
				if(counterCache.containsKey(counterKey)){
					log.info("the counter cache contains the key");
					counterCacheValue=counterCache.get(counterKey);
				}
				else{
					log.info("the counter cache doesnt contain the key, we add it");
					counterCacheValue=CounterInstanceCache.getInstance(usageChargeInstance.getCounter());
					counterCache.put(counterKey, counterCacheValue);
				}
				cachedValue.setCounter(counterCacheValue);
			}
			cachedValue.setFilter1(usageChargeTemplate.getFilterParam1());
			cachedValue.setFilter2(usageChargeTemplate.getFilterParam2());
			cachedValue.setFilter3(usageChargeTemplate.getFilterParam3());
			cachedValue.setFilter4(usageChargeTemplate.getFilterParam4());
			cachedValue.setTerminationDate(usageChargeInstance.getTerminationDate());
			cachedValue.setFilterExpression(usageChargeTemplate.getFilterExpression());
			cachedValue.setUnityMultiplicator(usageChargeTemplate.getUnityMultiplicator());
			cachedValue.setUnityNbDecimal(usageChargeTemplate.getUnityNbDecimal());
			
			if(!cacheContainsCharge){
				log.info("charge added");
				charges.add(cachedValue);
			}
			if(cacheContainsKey){
				log.info("key added to charge cache");
				usageCache.put(key, charges);
			}
		}
	}
    
	
}
