package org.meveo.model.cache;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedTriggeredEDR {

    private Long id;
    private String code;
    private String subscriptionEL;
    private String conditionEL;
    private String quantityEL;
    private String param1EL;
    private String param2EL;
    private String param3EL;
    private String param4EL;

    public CachedTriggeredEDR() {

    }

    public CachedTriggeredEDR(TriggeredEDRTemplate edrTemplate) {

        conditionEL = edrTemplate.getConditionEl();

        id = edrTemplate.getId();
        code = edrTemplate.getCode();
        subscriptionEL = edrTemplate.getSubscriptionEl();

        if (edrTemplate.getQuantityEl() == null || (edrTemplate.getQuantityEl().equals(""))) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate QuantityEL must be set for triggeredEDRTemplate=" + edrTemplate.getId());
        } else {
            quantityEL = edrTemplate.getQuantityEl();
        }
        if (edrTemplate.getParam1El() == null || (edrTemplate.getParam1El().equals(""))) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("edrTemplate param1El must be set for triggeredEDRTemplate=" + edrTemplate.getId());
        } else {
            param1EL = edrTemplate.getParam1El();
        }

        param2EL = StringUtils.stripToNull(edrTemplate.getParam2El());
        param3EL = StringUtils.stripToNull(edrTemplate.getParam3El());
        param4EL = StringUtils.stripToNull(edrTemplate.getParam4El());

    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getSubscriptionEL() {
        return subscriptionEL;
    }

    public String getConditionEL() {
        return conditionEL;
    }

    public String getQuantityEL() {
        return quantityEL;
    }

    public String getParam1EL() {
        return param1EL;
    }

    public String getParam2EL() {
        return param2EL;
    }

    public String getParam3EL() {
        return param3EL;
    }

    public String getParam4EL() {
        return param4EL;
    }

    @Override
    public String toString() {
        return String.format("CachedTriggeredEDR [id=%s, code=%s, subscriptionEL=%s, conditionEL=%s, quantityEL=%s, param1EL=%s, param2EL=%s, param3EL=%s, param4EL=%s]", id, code,
            subscriptionEL, conditionEL, quantityEL, param1EL, param2EL, param3EL, param4EL);
    }
}