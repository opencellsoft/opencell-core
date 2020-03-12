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