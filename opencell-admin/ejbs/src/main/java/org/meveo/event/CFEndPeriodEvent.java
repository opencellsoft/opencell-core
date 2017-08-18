package org.meveo.event;

import java.io.Serializable;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;

/**
 * @author Edward P. Legaspi
 **/
public class CFEndPeriodEvent implements Serializable {

    private static final long serialVersionUID = -1937181899381134353L;

    private String entityClass;
    private Long entityId;
    private String cfCode;
    private DatePeriod period;

    public CFEndPeriodEvent() {
    }

    public CFEndPeriodEvent(ICustomFieldEntity entity, String cfCode, DatePeriod period) {
        this.entityClass = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        this.entityId = (Long) ((IEntity) entity).getId();
        this.cfCode = cfCode;
        this.period = period;
    }

    @Override
    public String toString() {
        return "CFEndPeriodEvent [entityClass=" + entityClass + ", entityId=" + entityId + ", cfCode=" + cfCode + ", period=" + period + "]";
    }
}