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

package org.meveo.api.dto.audit;

import org.meveo.api.dto.BusinessEntityDto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class AuditableFieldDto.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@XmlRootElement(name = "auditableField")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditableFieldDto extends BusinessEntityDto {


    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -681203783968787919L;

    /**
     * Entity class
     */
    @XmlAttribute()
    private String entityClass;

    /**
     * Field name
     */
    @XmlAttribute()
    private String fieldName;

    /**
     * Change origin
     */
    @XmlAttribute()
    private String changeOrigin;

    /**
     * Origin name
     */
    @XmlAttribute()
    private String originName;

    /**
     * Previous state
     */
    @XmlAttribute()
    private String previousState;

    /**
     * Current state
     */
    @XmlAttribute()
    private String currentState;

    /**
     * Created date
     */
    @XmlAttribute()
    private String created;

    /**
     * User who created the record
     */
    @XmlAttribute()
    private String actor;

    public AuditableFieldDto() {

    }

    /**
     * Gets the entityClass
     *
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the entityClass.
     *
     * @param entityClass the new entityClass
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
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
     * Gets the changeOrigin
     *
     * @return the changeOrigin
     */
    public String getChangeOrigin() {
        return changeOrigin;
    }

    /**
     * Sets the changeOrigin.
     *
     * @param changeOrigin the new changeOrigin
     */
    public void setChangeOrigin(String changeOrigin) {
        this.changeOrigin = changeOrigin;
    }

    /**
     * Gets the originName
     *
     * @return the originName
     */
    public String getOriginName() {
        return originName;
    }

    /**
     * Sets the originName.
     *
     * @param originName the new originName
     */
    public void setOriginName(String originName) {
        this.originName = originName;
    }

    /**
     * Gets the previousState
     *
     * @return the previousState
     */
    public String getPreviousState() {
        return previousState;
    }

    /**
     * Sets the previousState.
     *
     * @param previousState the new previousState
     */
    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    /**
     * Gets the currentState
     *
     * @return the currentState
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * Sets the currentState.
     *
     * @param currentState the new currentState
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    /**
     * Gets the created
     *
     * @return the created
     */
    public String getCreated() {
        return created;
    }

    /**
     * Sets the created.
     *
     * @param created the new created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * Gets the actor
     *
     * @return the actor
     */
    public String getActor() {
        return actor;
    }

    /**
     * Sets the actor.
     *
     * @param actor the new actor
     */
    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return "AuditableFieldDto{" +
                "entityClass='" + entityClass + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", changeOrigin='" + changeOrigin + '\'' +
                ", originName='" + originName + '\'' +
                ", previousState='" + previousState + '\'' +
                ", currentState='" + currentState + '\'' +
                ", created='" + created + '\'' +
                ", actor='" + actor + '\'' +
                '}';
    }
}