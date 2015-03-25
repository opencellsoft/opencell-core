package org.meveo.model.cache;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;

public class CachedUsageChargeTemplate {

    private Long id;
    private String code;
    private Date lastUpdate;
    private int priority;
    private String filterExpression;
    private String filter1;
    private String filter2;
    private String filter3;
    private String filter4;
    private Set<CachedTriggeredEDR> edrTemplates = new HashSet<CachedTriggeredEDR>();
    private Set<Long> subscriptionIds = new HashSet<Long>();

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public int getPriority() {
        return priority;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public String getFilter1() {
        return filter1;
    }

    public String getFilter2() {
        return filter2;
    }

    public String getFilter3() {
        return filter3;
    }

    public String getFilter4() {
        return filter4;
    }

    public Set<CachedTriggeredEDR> getEdrTemplates() {
        return edrTemplates;
    }

    public Set<Long> getSubscriptionIds() {
        return subscriptionIds;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String
            .format(
                "CachedUsageChargeTemplate [id=%s, code=%s, lastUpdate=%s, priority=%s, filterExpression=%s, filter1=%s, filter2=%s, filter3=%s, filter4=%s, edrTemplates=%s, subscriptionIds=%s]",
                id, code, lastUpdate, priority, filterExpression, filter1, filter2, filter3, filter4, edrTemplates != null ? toString(edrTemplates, maxLen) : null,
                subscriptionIds != null ? toString(subscriptionIds, maxLen) : null);
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

    public void populateFromUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate) {

        id = usageChargeTemplate.getId();
        code = usageChargeTemplate.getCode();
        filterExpression = StringUtils.stripToNull(usageChargeTemplate.getFilterExpression());
        filter1 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam1());
        filter2 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam2());
        filter3 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam3());
        filter4 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam4());

        edrTemplates = new HashSet<CachedTriggeredEDR>();
        if (usageChargeTemplate.getEdrTemplates() != null && usageChargeTemplate.getEdrTemplates().size() > 0) {
            for (TriggeredEDRTemplate edrTemplate : usageChargeTemplate.getEdrTemplates()) {
                CachedTriggeredEDR trigerredEDRCache = new CachedTriggeredEDR(edrTemplate);
                edrTemplates.add(trigerredEDRCache);
            }
        }
        if (getPriority() != usageChargeTemplate.getPriority()) {
            priority = usageChargeTemplate.getPriority();
        }
    }
}