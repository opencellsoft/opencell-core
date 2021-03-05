package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * Re-rate details
 * 
 * @author Andrius Karpavicius
 */
public class ReratingInfo implements Serializable {

    private static final long serialVersionUID = -5151554692390013070L;

    /**
     * Offer template identifier
     */
    private Long offerTemplateId;

    /**
     * Service template identifier
     */
    private Long serviceTemplateId;

    /**
     * Charge type
     */
    private ChargeMainTypeEnum chargeType;

    /**
     * Starting which date to re-rate
     */
    private Date fromDate;

    /**
     * Additional Wallet operation filtering criteria with field name as a key. Custom field names are prefixed by "CF." value.
     */
    private Map<String, Object> additionalCriteria;

    /**
     * @return Offer template identifier
     */
    public Long getOfferTemplateId() {
        return offerTemplateId;
    }

    /**
     * @return Service template identifier
     */
    public Long getServiceTemplateId() {
        return serviceTemplateId;
    }

    /**
     * @return Charge type
     */
    public ChargeMainTypeEnum getChargeType() {
        return chargeType;
    }

    /**
     * @return Starting which date to re-rate
     */
    public Date getFromDate() {
        return fromDate;
    }

    public Map<String, Object> getAdditionalCriteria() {
        return additionalCriteria;
    }

    /**
     * Re-rate details
     * 
     * @param offerTemplate Offer template
     * @param serviceTemplate Service template
     * @param chargeType Charge type
     * @param fromDate Starting which date to re-rate
     * @param additionalCriteria Additional Wallet operation filtering criteria with field name as a key. Custom field names are prefixed by "CF." value.
     */
    public ReratingInfo(OfferTemplate offerTemplate, ServiceTemplate serviceTemplate, ChargeMainTypeEnum chargeType, Date fromDate, Object... additionalCriteria) {
        super();
        this.offerTemplateId = offerTemplate.getId();
        this.serviceTemplateId = serviceTemplate.getId();
        this.chargeType = chargeType;
        this.fromDate = fromDate;

        if (additionalCriteria.length > 1) {
            this.additionalCriteria = new HashMap<>();
            for (int i = 0; i < additionalCriteria.length; i = i + 2) {
                this.additionalCriteria.put((String) additionalCriteria[i], additionalCriteria[i + 1]);
            }
        }
    }
}