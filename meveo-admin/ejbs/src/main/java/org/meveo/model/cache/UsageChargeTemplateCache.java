package org.meveo.model.cache;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsageChargeTemplateCache {

    private Date lastUpdate;
    private int priority;
    private String filterExpression;
    private String filter1;
    private String filter2;
    private String filter3;
    private String filter4;
    private Set<TriggeredEDRCache> edrTemplates = new HashSet<TriggeredEDRCache>();
    private Set<Long> subscriptionIds = new HashSet<Long>();

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
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

    public Set<TriggeredEDRCache> getEdrTemplates() {
        return edrTemplates;
    }

    public void setEdrTemplates(Set<TriggeredEDRCache> edrTemplates) {
        this.edrTemplates = edrTemplates;
    }

    public Set<Long> getSubscriptionIds() {
        return subscriptionIds;
    }

    public void setSubscriptionIds(Set<Long> subscriptionIds) {
        this.subscriptionIds = subscriptionIds;
    }

    public String toString() {
        return lastUpdate + "," + priority + "," + filterExpression + "," + filter1 + "," + filter2 + "," + filter3 + "," + filter4;
    }

    public void populateFromUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate) {

        setFilterExpression(StringUtils.stripToNull(usageChargeTemplate.getFilterExpression()));
        setFilter1(StringUtils.stripToNull(usageChargeTemplate.getFilterParam1()));
        setFilter2(StringUtils.stripToNull(usageChargeTemplate.getFilterParam2()));
        setFilter3(StringUtils.stripToNull(usageChargeTemplate.getFilterParam3()));
        setFilter4(StringUtils.stripToNull(usageChargeTemplate.getFilterParam4()));

        edrTemplates = new HashSet<TriggeredEDRCache>();
        if (usageChargeTemplate.getEdrTemplates() != null && usageChargeTemplate.getEdrTemplates().size() > 0) {
            for (TriggeredEDRTemplate edrTemplate : usageChargeTemplate.getEdrTemplates()) {
                TriggeredEDRCache trigerredEDRCache = new TriggeredEDRCache();
                trigerredEDRCache.setConditionEL(edrTemplate.getConditionEl());

                trigerredEDRCache.setCode(edrTemplate.getCode());
                trigerredEDRCache.setSubscriptionEL(edrTemplate.getSubscriptionEl());

                if (edrTemplate.getQuantityEl() == null || (edrTemplate.getQuantityEl().equals(""))) {
                    Logger log = LoggerFactory.getLogger(this.getClass());
                    log.error("edrTemplate QuantityEL must be set for triggeredEDRTemplate=" + edrTemplate.getId());
                } else {
                    trigerredEDRCache.setQuantityEL(edrTemplate.getQuantityEl());
                }
                if (edrTemplate.getParam1El() == null || (edrTemplate.getParam1El().equals(""))) {
                    Logger log = LoggerFactory.getLogger(this.getClass());
                    log.error("edrTemplate param1El must be set for triggeredEDRTemplate=" + edrTemplate.getId());
                } else {
                    trigerredEDRCache.setParam1EL(edrTemplate.getParam1El());
                }

                trigerredEDRCache.setParam2EL(StringUtils.stripToNull(edrTemplate.getParam2El()));
                trigerredEDRCache.setParam3EL(StringUtils.stripToNull(edrTemplate.getParam3El()));
                trigerredEDRCache.setParam4EL(StringUtils.stripToNull(edrTemplate.getParam4El()));

                edrTemplates.add(trigerredEDRCache);
            }
        }
        if (getPriority() != usageChargeTemplate.getPriority()) {
            setPriority(usageChargeTemplate.getPriority());            
        }
        setFilterExpression(usageChargeTemplate.getFilterExpression());
    }
}