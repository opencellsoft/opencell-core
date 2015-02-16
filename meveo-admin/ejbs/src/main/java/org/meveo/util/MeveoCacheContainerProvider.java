
package org.meveo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.model.cache.CounterInstanceCache;
import org.meveo.model.cache.UsageChargeInstanceCache;
import org.meveo.model.cache.UsageChargeTemplateCache;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.mediation.Access;
import org.slf4j.Logger;


/**
  MeveoCacheContainerProvider 
 * 
 * @author R.AITYAAZZA
 * 
 */
@Startup
@Singleton
public class MeveoCacheContainerProvider {

    
	@Inject
	protected Logger log;

	private static BasicCache<String, HashMap<String, List<PricePlanMatrix>>> allPricePlan;
	private static BasicCache<Long, UsageChargeTemplateCache> usageChargeTemplateCacheCache;
	private static BasicCache<Long, List<UsageChargeInstanceCache>> usageChargeInstanceCache;
	private static BasicCache<Long,CounterInstanceCache> counterCache;
	private static  BasicCache<String, Map<String, UsageChargeTemplate>> usageChargeTemplateCache;
	private static BasicCache<String, List<Access>> accessCache;
	private static BasicCache<String, Integer> edrCache;
	
	

	

	@PostConstruct
	private void init() {
		try {
			log.info("MeveoCacheContainerProvider initializing...");
			CacheContainer meveoContainer=(CacheContainer)new InitialContext().lookup("java:jboss/infinispan/container/meveo");
			allPricePlan = meveoContainer.getCache("meveo-price-plan");
			usageChargeTemplateCacheCache = meveoContainer.getCache("meveo-usage-charge-template-cache-cache");
			
			usageChargeInstanceCache = meveoContainer.getCache("meveo-charge-instance-cache");
		
			counterCache = meveoContainer.getCache("meveo-counter-cache");
			accessCache = meveoContainer.getCache("meveo-access-cache");
			usageChargeTemplateCache = meveoContainer.getCache("meveo-usage-charge-template-cache");
			edrCache=meveoContainer.getCache("meveo-edr-cache");
		} catch (Exception e) {
			log.error("MeveoCacheContainerProvider init() error",e);
		}
		
	}




	public static BasicCache<String, HashMap<String, List<PricePlanMatrix>>> getAllPricePlan() {
		return allPricePlan;
	}




	public static BasicCache<Long, UsageChargeTemplateCache> getUsageChargeTemplateCacheCache() {
		return usageChargeTemplateCacheCache;
	}




	public  static BasicCache<Long, List<UsageChargeInstanceCache>> getUsageChargeInstanceCache() {
		return usageChargeInstanceCache;
	}




	public static BasicCache<Long, CounterInstanceCache> getCounterCache() {
		return counterCache;
	}




	public static BasicCache<String, Map<String, UsageChargeTemplate>> getUsageChargeTemplateCache() {
		return usageChargeTemplateCache;
	}




	public static BasicCache<String, List<Access>> getAccessCache() {
		return accessCache;
	}




	public static BasicCache<String, Integer> getEdrCache() {
		return edrCache;
	}
	
	
    
}
