/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.model.cache.CounterInstanceCache;
import org.meveo.model.cache.UsageChargeInstanceCache;
import org.meveo.model.cache.UsageChargeTemplateCache;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.mediation.Access;


/**
 * {@link CacheContainerProvider}'s implementation creating a DefaultCacheManager 
 * which is configured programmatically. Infinispan's libraries need to be bundled 
 * with the application - this is called "library" mode.
 * 
 * 
 * @author R.AITYAAZZA
 * 
 */
@Stateless
public class MeveoCacheContainerProvider {

    
	@Resource(name = "java:jboss/infinispan/container/meveo")
	private CacheContainer meveoContainer;

	private static BasicCache<String, HashMap<String, List<PricePlanMatrix>>> allPricePlan;
	private static BasicCache<Long, UsageChargeTemplateCache> usageChargeTemplateCacheCache;
	private static BasicCache<Long, List<UsageChargeInstanceCache>> usageChargeInstanceCache;
	private static BasicCache<Long,CounterInstanceCache> counterCache;
	private static  BasicCache<String, Map<String, UsageChargeTemplate>> usageChargeTemplateCache;
	private static BasicCache<String, List<Access>> accessCache;
	private static BasicCache<String, Integer> edrCache;
	
	

	

	@PostConstruct
	private void init() {
		
		allPricePlan = meveoContainer.getCache("meveo-price-plan");
		usageChargeTemplateCacheCache = meveoContainer.getCache("meveo-usage-charge-template-cache-cache");
		
		usageChargeInstanceCache = meveoContainer.getCache("meveo-charge-instance-cache");
	
		counterCache = meveoContainer.getCache("meveo-counter-cache");
		accessCache = meveoContainer.getCache("meveo-access-cache");
		usageChargeTemplateCache = meveoContainer.getCache("meveo-usage-charge-template-cache");
		meveoContainer.getCache("meveo-edr-cache");
	}




	public  BasicCache<String, HashMap<String, List<PricePlanMatrix>>> getAllPricePlan() {
		return allPricePlan;
	}




	public  BasicCache<Long, UsageChargeTemplateCache> getUsageChargeTemplateCacheCache() {
		return usageChargeTemplateCacheCache;
	}




	public  BasicCache<Long, List<UsageChargeInstanceCache>> getUsageChargeInstanceCache() {
		return usageChargeInstanceCache;
	}




	public  BasicCache<Long, CounterInstanceCache> getCounterCache() {
		return counterCache;
	}




	public  BasicCache<String, Map<String, UsageChargeTemplate>> getUsageChargeTemplateCache() {
		return usageChargeTemplateCache;
	}




	public  BasicCache<String, List<Access>> getAccessCache() {
		return accessCache;
	}




	public  BasicCache<String, Integer> getEdrCache() {
		return edrCache;
	}
	
	
    
}
