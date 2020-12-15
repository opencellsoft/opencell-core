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
package org.meveo.model;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.security.MeveoUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Tracks Field change history
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@Entity
@Table(name = "audit_field_changes_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "audit_field_changes_history_seq"),})
public class AuditableField extends BaseEntity {

    private static final long serialVersionUID = -7263546632393279781L;

    /**
     * Auditable entity name
     */
    @Column(name = "entity_class", length = 255)
    @Size(max = 255)
    @NotNull
    private String entityClass;


    /**
     * Auditable entity id
     */
    @Column(name = "entity_id")
    @NotNull
    private Long entityId;

    /**
     * Auditable field name
     */
    @Column(name = "field_name", length = 255)
    @Size(max = 255)
    private String name;

    /**
     * Field change type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private AuditChangeTypeEnum changeType = AuditChangeTypeEnum.OTHER;


    /**
     * Field change origin
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "change_origin")
    private ChangeOriginEnum changeOrigin = ChangeOriginEnum.OTHER;


    /**
     * Field change origin name
     */
    @Column(name = "origin_name", length = 255)
    @Size(max = 255)
    private String originName;

    /**
     * Previous state of field
     */
    @Column(name = "previous_state", length = 255)
    @Size(max = 255)
    private String previousState;

    /**
     * Current state of field
     */
    @Column(name = "current_state", length = 255)
    @Size(max = 255)
    private String currentState;


    /**
     * Record/entity creation timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created;

    /**
     * Username of a user that updated the field
     */
    @Column(name = "actor", updatable = false, length = 200)
    private String actor;


    public AuditableField() {
    }

    /**
     * Gets the entity class
     *
     * @return the entity class
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the entity class.
     *
     * @param entityClass the new entity class
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Gets the entity id
     *
     * @return the entity id
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the entity id.
     *
     * @param entityId the new entity id
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the changeType
     *
     * @return the changeType
     */
    public AuditChangeTypeEnum getChangeType() {
        return changeType;
    }

    /**
     * Sets the changeType.
     *
     * @param changeType the new changeType
     */
    public void setChangeType(AuditChangeTypeEnum changeType) {
        this.changeType = changeType;
    }

    /**
     * Gets the changeOrigin
     *
     * @return the changeOrigin
     */
    public ChangeOriginEnum getChangeOrigin() {
        return changeOrigin;
    }

    /**
     * Sets the changeOrigin.
     *
     * @param changeOrigin the new changeOrigin
     */
    public void setChangeOrigin(ChangeOriginEnum changeOrigin) {
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
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the created.
     *
     * @param created the new created
     */
    public void setCreated(Date created) {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof AuditableField)) {
            return false;
        }
        AuditableField other = (AuditableField) obj;
        return getId().equals(other.getId());
    }
}
