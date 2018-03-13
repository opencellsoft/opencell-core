package org.meveo.event;

import java.io.Serializable;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;

/**
 * Custom field value end of period event
 * 
 * @author Edward P. Legaspi
 **/
public class CFEndPeriodEvent implements Serializable {

    private static final long serialVersionUID = -1937181899381134353L;

    /*
     * Entity class containing the CF value
     */
    private String entityClass;

    /**
     * ID of an entity containing the CF value
     */
    private Long entityId;

    /**
     * Custom field code
     */
    private String cfCode;

    /**
     * Period to track
     */
    private DatePeriod period;

    /**
     * Tenant/provider code that data belongs to (used in case of multitenancy)
     */
    private String providerCode;

    public CFEndPeriodEvent() {
    }

    public CFEndPeriodEvent(ICustomFieldEntity entity, String cfCode, DatePeriod period, String providerCode) {
        this.entityClass = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        this.entityId = (Long) ((IEntity) entity).getId();
        this.cfCode = cfCode;
        this.period = period;
        this.providerCode = providerCode;
    }

    @Override
    public String toString() {
        return "CFEndPeriodEvent [entityClass=" + entityClass + ", entityId=" + entityId + ", cfCode=" + cfCode + ", period=" + period + ", providerCode=" + providerCode + "]";
    }

    public String getEntityClass() {
        return entityClass;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getCfCode() {
        return cfCode;
    }

    public DatePeriod getPeriod() {
        return period;
    }

    public String getProviderCode() {
        return providerCode;
    }
}