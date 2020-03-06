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

package org.meveo.audit;

import org.meveo.event.IEvent;
import org.meveo.model.BaseEntity;

import java.io.Serializable;

/**
 * Represents a auditable field event object
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public class AuditableFieldEvent implements Serializable, IEvent {


    private static final long serialVersionUID = -8884656138473066328L;

    /**
     * Associated entity
     */
    private BaseEntity entity;

    /**
     * Field name
     */
    private String fieldName;

    /**
     * previous state of field
     */
    private Object previousState;

    /**
     * current state of field
     */
    private Object currentState;


    public AuditableFieldEvent() {

    }

    public AuditableFieldEvent(Object entity, String fieldName, Object currentState, Object previousState) {
        super();
        this.entity = (BaseEntity) entity;
        this.fieldName = fieldName;
        this.currentState = currentState;
        this.previousState = previousState;
    }

    /**
     * Gets the entity
     *
     * @return the entity
     */
    @Override
    public BaseEntity getEntity() {
        return entity;
    }

    /**
     * Sets the entity.
     *
     * @param entity the new entity
     */
    public void setEntity(BaseEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets the fieldName
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the fieldName.
     *
     * @param fieldName the new fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the previousState
     *
     * @return the previousState
     */
    public Object getPreviousState() {
        return previousState;
    }

    /**
     * Sets the previousState.
     *
     * @param previousState the new previousState
     */
    public void setPreviousState(Object previousState) {
        this.previousState = previousState;
    }

    /**
     * Gets the currentState
     *
     * @return the currentState
     */
    public Object getCurrentState() {
        return currentState;
    }

    /**
     * Sets the currentState.
     *
     * @param currentState the new currentState
     */
    public void setCurrentState(Object currentState) {
        this.currentState = currentState;
    }
}