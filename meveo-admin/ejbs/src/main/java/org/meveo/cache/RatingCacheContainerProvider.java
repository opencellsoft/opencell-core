package org.meveo.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.meveo.model.mediation.Access;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for event notification related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
@Startup
@Singleton
public class RatingCacheContainerProvider {

    @Inject
    protected Logger log;

    @Inject
    PricePlanMatrixService pricePlanMatrixService;

    /**
     * Contains association between charge code and price plans. Key format: <provider id>_<charge template code, which is pricePlanMatrix.eventCode>
     */
    private BasicCache<String, List<PricePlanMatrix>> pricePlanCache;

    private BasicCache<Long, UsageChargeTemplateCache> usageChargeTemplateCacheCache;
    private BasicCache<Long, List<UsageChargeInstanceCache>> usageChargeInstanceCache;
    private BasicCache<Long, CounterInstanceCache> counterCache;
    private BasicCache<String, List<Access>> accessCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("RatingCacheContainerProvider initializing...");
            CacheContainer meveoContainer = (CacheContainer) new InitialContext().lookup("java:jboss/infinispan/container/meveo");
            pricePlanCache = meveoContainer.getCache("meveo-price-plan");
            usageChargeTemplateCacheCache = meveoContainer.getCache("meveo-usage-charge-template-cache-cache");
            usageChargeInstanceCache = meveoContainer.getCache("meveo-charge-instance-cache");
            counterCache = meveoContainer.getCache("meveo-counter-cache");
            accessCache = meveoContainer.getCache("meveo-access-cache");

            populatePricePlanCache();

            log.debug("RatingCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("RatingCacheContainerProvider init() error", e);
        }
    }

    private void populatePricePlanCache() {

        log.info("start to populate price plan cache");
        List<PricePlanMatrix> activePricePlans = pricePlanMatrixService.getPricePlansForCache();

        for (PricePlanMatrix pricePlan : activePricePlans) {
            String cacheKey = pricePlan.getProvider().getId() + "_" + pricePlan.getEventCode();
            if (!pricePlanCache.containsKey(cacheKey)) {
                pricePlanCache.put(cacheKey, new ArrayList<PricePlanMatrix>());
            }
            List<PricePlanMatrix> chargePriceList = pricePlanCache.get(cacheKey);

            if (pricePlan.getCriteria1Value() != null && pricePlan.getCriteria1Value().length() == 0) {
                pricePlan.setCriteria1Value(null);
            }
            if (pricePlan.getCriteria2Value() != null && pricePlan.getCriteria2Value().length() == 0) {
                pricePlan.setCriteria2Value(null);
            }
            if (pricePlan.getCriteria3Value() != null && pricePlan.getCriteria3Value().length() == 0) {
                pricePlan.setCriteria3Value(null);
            }
            if (pricePlan.getCriteriaEL() != null && pricePlan.getCriteriaEL().length() == 0) {
                pricePlan.setCriteriaEL(null);
            }

            // Lazy loading workaround
            if (pricePlan.getOfferTemplate() != null) {
                pricePlan.getOfferTemplate().getCode();
            }
            if (pricePlan.getValidityCalendar() != null) {
                pricePlan.getValidityCalendar().getCode();
            }

            log.info("Add pricePlan for provider=" + pricePlan.getProvider().getCode() + "; chargeCode=" + pricePlan.getEventCode() + "; priceplan=" + pricePlan);
            chargePriceList.add(pricePlan);

        }

        for (List<PricePlanMatrix> chargePriceList : pricePlanCache.values()) {
            Collections.sort(chargePriceList);
        }

        log.info("price plan cache populated with {} price plans", activePricePlans.size());
    }

    public void addPricePlanToCache(PricePlanMatrix pricePlan) {

        String cacheKey = pricePlan.getProvider().getId() + "_" + pricePlan.getEventCode();
        if (!pricePlanCache.containsKey(cacheKey)) {
            pricePlanCache.put(cacheKey, new ArrayList<PricePlanMatrix>());
        }
        List<PricePlanMatrix> chargePriceList = pricePlanCache.get(cacheKey);

        if (pricePlan.getCriteria1Value() != null && pricePlan.getCriteria1Value().length() == 0) {
            pricePlan.setCriteria1Value(null);
        }
        if (pricePlan.getCriteria2Value() != null && pricePlan.getCriteria2Value().length() == 0) {
            pricePlan.setCriteria2Value(null);
        }
        if (pricePlan.getCriteria3Value() != null && pricePlan.getCriteria3Value().length() == 0) {
            pricePlan.setCriteria3Value(null);
        }
        if (pricePlan.getCriteriaEL() != null && pricePlan.getCriteriaEL().length() == 0) {
            pricePlan.setCriteriaEL(null);
        }

        // Lazy loading workaround
        if (pricePlan.getOfferTemplate() != null) {
            pricePlan.getOfferTemplate().getCode();
        }
        if (pricePlan.getValidityCalendar() != null) {
            pricePlan.getValidityCalendar().getCode();
        }

        chargePriceList.add(pricePlan);

        Collections.sort(chargePriceList);
    }

    public void removePricePlanFromCache(PricePlanMatrix pricePlan) {

        String cacheKey = pricePlan.getProvider().getId() + "_" + pricePlan.getEventCode();
        List<PricePlanMatrix> chargePriceList = pricePlanCache.get(cacheKey);

        int index = 0;
        for (PricePlanMatrix chargePricePlan : chargePriceList) {
            if (pricePlan.getId().equals(chargePricePlan.getId())) {
                chargePriceList.remove(index);
            }
            index++;
        }
    }

    public void updatePricePlanInCache(PricePlanMatrix pricePlan) {
        removePricePlanFromCache(pricePlan);
        addPricePlanToCache(pricePlan);
    }

    /**
     * Get applicable price plans for a given provider and charge code
     * 
     * @param providerId Provider id
     * @param chargeCode Charge code
     * @return A list of applicable price plans
     */
    public List<PricePlanMatrix> getPricePlansByChargeCode(Long providerId, String chargeCode) {
        return pricePlanCache.get(providerId + "_" + chargeCode);
    }

}