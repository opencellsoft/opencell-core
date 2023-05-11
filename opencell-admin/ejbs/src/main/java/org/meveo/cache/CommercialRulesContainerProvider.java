/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.slf4j.Logger;

public class CommercialRulesContainerProvider implements Serializable {

    private static final String TIRET = "-";

    @Inject
    protected Logger log;

    @EJB
    private CommercialRuleHeaderService commercialRuleHeaderService;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    private static boolean useCommercialRuleCache = true;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-commercial-rule-offer-product")
    private Cache<CacheKeyStr, List<CommercialRuleHeader>> offerAndProduct;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-commercial-rule-product-attribute")
    private Cache<CacheKeyStr, List<CommercialRuleHeader>> productAndAtttribute;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-commercial-rule-product-grpattr")
    private Cache<CacheKeyStr, List<CommercialRuleHeader>> productAndGrpAttribute;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-commercial-rule-offer-attribute")
    private Cache<CacheKeyStr, List<CommercialRuleHeader>> offerAndAttribute;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        useCommercialRuleCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cacheCommercialRule", "true"));
    }

    public void populateCache() {

        if (!useCommercialRuleCache) {
            log.info("CommercialRuleHeader cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulateCommercialRuleCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCommercialRule.prepopulate", "true"));

        if (!prepopulateCommercialRuleCache) {
            log.info("CommercialRuleHeader cache pre-population will be skipped");
            return;
        }

        String provider = currentUser.getProviderCode();

        log.debug("Start to pre-populate CommercialRuleHeader cache for provider {}.", provider);

        List<CommercialRuleHeader> ruleHeaders = commercialRuleHeaderService.findAll();
        for (CommercialRuleHeader rule : ruleHeaders) {
//            Cache 1 ===> key= productCode , value = all linked commercial rules having targetProduct=productCode, targetAttribute=null, targetGroupedAttribute=null
//            Cache 2 ===> key= productCode-attributeCode , value = all linked commercial rules having targetProduct=productCode, targetAttribute=attributeCode, targetGroupedAttribute=null
//            Cache 3 ===> key= productCode-targetGroupedAttri , value = all linked commercial rules having targetProduct=productCode, targetAttribute=null, targetGroupedAttribute=groupedattrb
//            Cache 4 ===> key= offer-attributeCode , value = all linked commercial rules having targetOffer=offerCode, targetAttribute=atributeCode, targetGroupedAttribute=null

//            commercialRuleHeaderService.getProductRulesWithoutCheck(offerCode, offerProduct.getProduct().getCode()) == Cache ??
//            commercialRuleHeaderService.getProductAttributeRulesWithoutCheck(attributeDto.getCode(), offerProduct.getProduct().getCode()) == Cache 2
//            commercialRuleHeaderService.getGroupedAttributesRulesWithoutCheck(groupedAttributeDTO.getCode(), offerProduct.getProduct().getCode()); == Cache 3
//            commercialRuleHeaderService.getOfferAttributeRules(attributeDto.getCode(), offertemplateDTO.getCode()) == Cache 4
            add(rule);

        }

        log.info("CommercialRuleHeader cache populated with {} entries for provider {}", ruleHeaders.size(), provider);
    }

    public void add(CommercialRuleHeader rule) {
        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache1Key = new CacheKeyStr(null, rule.getTargetOfferTemplateCode() + TIRET + rule.getTargetProductCode());
            // Cache 1
            List<CommercialRuleHeader> offerAndProductContents = offerAndProduct.get(cache1Key);
            if (CollectionUtils.isEmpty(offerAndProductContents)) {
                offerAndProductContents = new ArrayList<>();
                offerAndProduct.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache1Key, offerAndProductContents);
            }
            offerAndProductContents.add(rule);
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null ) {
            CacheKeyStr cache2Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 2
            List<CommercialRuleHeader> productAndAtttributeContents = productAndAtttribute.get(cache2Key);
            if (CollectionUtils.isEmpty(productAndAtttributeContents)) {
                productAndAtttributeContents = new ArrayList<>();
                productAndAtttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache2Key, productAndAtttributeContents);
            }
            productAndAtttributeContents.add(rule);
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() != null) {
            CacheKeyStr cache3Key = new CacheKeyStr(null, rule.getTargetGroupedAttributes().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 3
            List<CommercialRuleHeader> productAndGrpAttributeContents = productAndGrpAttribute.get(cache3Key);
            if (CollectionUtils.isEmpty(productAndGrpAttributeContents)) {
                productAndGrpAttributeContents = new ArrayList<>();
                productAndGrpAttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache3Key, productAndGrpAttributeContents);
            }
            productAndGrpAttributeContents.add(rule);
        }

        if (rule.getTargetOfferTemplate() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache4Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetOfferTemplateCode());
            // Cache 4
            List<CommercialRuleHeader> offerAndAttributeContents = offerAndAttribute.get(cache4Key);
            if (CollectionUtils.isEmpty(offerAndAttributeContents)) {
                offerAndAttributeContents = new ArrayList<>();
                offerAndAttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache4Key, offerAndAttributeContents);
            }
            offerAndAttributeContents.add(rule);
        }
    }

    public void update(CommercialRuleHeader rule) {
        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache1Key = new CacheKeyStr(null, rule.getTargetOfferTemplateCode() + TIRET + rule.getTargetProductCode());
            // Cache 1
            List<CommercialRuleHeader> offerAndProductContents = offerAndProduct.get(cache1Key);
            if (CollectionUtils.isEmpty(offerAndProductContents)) {
                offerAndProductContents = new ArrayList<>();
                offerAndProduct.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache1Key, offerAndProductContents);
            }
            offerAndProductContents.remove(rule);
            offerAndProductContents.add(rule);
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache2Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 2
            List<CommercialRuleHeader> productAndAtttributeContents = productAndAtttribute.get(cache2Key);
            if (CollectionUtils.isEmpty(productAndAtttributeContents)) {
                productAndAtttributeContents = new ArrayList<>();
                productAndAtttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache2Key, productAndAtttributeContents);
            }
            productAndAtttributeContents.remove(rule);
            productAndAtttributeContents.add(rule);
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() != null) {
            CacheKeyStr cache3Key = new CacheKeyStr(null, rule.getTargetGroupedAttributes().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 3
            List<CommercialRuleHeader> productAndGrpAttributeContents = productAndGrpAttribute.get(cache3Key);
            if (CollectionUtils.isEmpty(productAndGrpAttributeContents)) {
                productAndGrpAttributeContents = new ArrayList<>();
                productAndGrpAttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache3Key, productAndGrpAttributeContents);
            }
            productAndGrpAttributeContents.remove(rule);
            productAndGrpAttributeContents.add(rule);
        }

        if (rule.getTargetOfferTemplate() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache4Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetOfferTemplateCode());
            // Cache 4
            List<CommercialRuleHeader> offerAndAttributeContents = offerAndAttribute.get(cache4Key);
            if (CollectionUtils.isEmpty(offerAndAttributeContents)) {
                offerAndAttributeContents = new ArrayList<>();
                offerAndAttribute.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cache4Key, offerAndAttributeContents);
            }
            offerAndAttributeContents.remove(rule);
            offerAndAttributeContents.add(rule);
        }
    }

    public void remove(CommercialRuleHeader rule) {
        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache1Key = new CacheKeyStr(null, rule.getTargetOfferTemplateCode() + TIRET + rule.getTargetProductCode());
            // Cache 1
            List<CommercialRuleHeader> offerAndProductContents = offerAndProduct.get(cache1Key);
            if (CollectionUtils.isNotEmpty(offerAndProductContents)) {
                offerAndProductContents.remove(rule);
            }
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache2Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 2
            List<CommercialRuleHeader> productAndAtttributeContents = productAndAtttribute.get(cache2Key);
            if (CollectionUtils.isNotEmpty(productAndAtttributeContents)) {
                productAndAtttributeContents.remove(rule);
            }
        }

        if (rule.getTargetProductCode() != null && rule.getTargetAttribute() == null && rule.getTargetGroupedAttributes() != null) {
            CacheKeyStr cache3Key = new CacheKeyStr(null, rule.getTargetGroupedAttributes().getCode() + TIRET + rule.getTargetProductCode());
            // Cache 3
            List<CommercialRuleHeader> productAndGrpAttributeContents = productAndGrpAttribute.get(cache3Key);
            if (CollectionUtils.isNotEmpty(productAndGrpAttributeContents)) {
                productAndGrpAttributeContents.remove(rule);
            }
        }

        if (rule.getTargetOfferTemplate() != null && rule.getTargetAttribute() != null && rule.getTargetGroupedAttributes() == null) {
            CacheKeyStr cache4Key = new CacheKeyStr(null, rule.getTargetAttribute().getCode() + TIRET + rule.getTargetOfferTemplateCode());
            // Cache 4
            List<CommercialRuleHeader> offerAndAttributeContents = offerAndAttribute.get(cache4Key);
            if (CollectionUtils.isNotEmpty(offerAndAttributeContents)) {
                offerAndAttributeContents.remove(rule);
            }
        }
    }

    public List<CommercialRuleHeader> getForOfferAndProduct(String identifier) {
        return offerAndProduct.get(new CacheKeyStr(null, identifier));
    }

    public List<CommercialRuleHeader> getForProductAndAtttribute(String identifier) {
        return productAndAtttribute.get(new CacheKeyStr(null, identifier));
    }

    public List<CommercialRuleHeader> getForProductAndGrpAttribute(String identifier) {
        return productAndGrpAttribute.get(new CacheKeyStr(null, identifier));
    }

    public List<CommercialRuleHeader> getForOfferAndAttribute(String identifier) {
        return offerAndAttribute.get(new CacheKeyStr(null, identifier));
    }
    
    /**
     * Get a summary of cached information.
     *
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<>();
        summaryOfCaches.put(offerAndAttribute.getName(), offerAndAttribute);
        summaryOfCaches.put(productAndAtttribute.getName(), productAndAtttribute);
        summaryOfCaches.put(offerAndAttribute.getName(), offerAndAttribute);
        summaryOfCaches.put(productAndGrpAttribute.getName(), productAndGrpAttribute);

        return summaryOfCaches;
    }

}