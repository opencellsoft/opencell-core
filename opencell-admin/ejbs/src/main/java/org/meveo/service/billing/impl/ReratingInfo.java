package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.util.Date;

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

    /**
     * Re-rate details
     * 
     * @param offerTemplate Offer template
     * @param serviceTemplate Service template
     * @param chargeType Charge type
     * @param fromDate Starting which date to re-rate
     */
    public ReratingInfo(OfferTemplate offerTemplate, ServiceTemplate serviceTemplate, ChargeMainTypeEnum chargeType, Date fromDate) {
        super();
        this.offerTemplateId = offerTemplate.getId();
        this.serviceTemplateId = serviceTemplate.getId();
        this.chargeType = chargeType;
        this.fromDate = fromDate;
    }
}