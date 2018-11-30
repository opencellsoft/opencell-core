package org.meveo.event;

import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.audit.hibernate.AuditChangeType;

import java.io.Serializable;

/**
 * Represents a entity field audit
 *
 * @author Abdellatif BARI
 * @since 5.3
 */
public class FieldAudit implements Serializable, IEvent {


    private static final long serialVersionUID = -8884656138473066328L;

    /**
     * Associated entity
     */
    private Object entity;

    /**
     * previous state of field
     */
    private Object previousState;

    /**
     * current state of field
     */
    private Object currentState;


    /**
     * type of audit
     */
    private AuditChangeType auditType;


    public FieldAudit() {

    }

    public FieldAudit(Object entity, Object currentState, Object previousState, AuditChangeType auditType) {
        this.entity = entity;
        this.currentState = currentState;
        this.previousState = previousState;
        this.auditType = auditType;
    }

    /**
     * Sets the entity.
     *
     * @param entity the new entity
     */
    public void setEntity(Object entity) {
        this.entity = entity;
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

    /**
     * Gets the auditType
     *
     * @return the auditType
     */
    public AuditChangeType getAuditType() {
        return auditType;
    }

    /**
     * Sets the auditType.
     *
     * @param auditType the new auditType
     */
    public void setAuditType(AuditChangeType auditType) {
        this.auditType = auditType;
    }

    @Override
    public IEntity getEntity() {
        return (BaseEntity) entity;
    }

    @Override
    public String toString() {
        return "FieldAudit{" +
                "entity=" + entity +
                ", previousState=" + previousState +
                ", currentState=" + currentState +
                ", auditType=" + auditType +
                '}';
    }
}