package org.meveo.model.audit;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains auditable field history
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public class AuditableFieldHistory implements Serializable {

    private static final long serialVersionUID = -7582914091083954014L;

    /**
     * Field name
     */
    private String fieldName;

    /**
     * Previous state of field
     */
    private Object previousState;

    /**
     * Current state of field
     */
    private Object currentState;

    /**
     * Type of audit
     */
    private AuditChangeTypeEnum auditType;

    /**
     * Is historable field
     */
    private boolean historable;

    /**
     * Is notfiable field
     */
    private boolean notfiable;

    /**
     * Is historized field
     */
    private boolean historized;

    /**
     * Is notified field
     */
    private boolean notified;


    /**
     * Instantiate
     */
    public AuditableFieldHistory() {
    }


    /**
     * Instantiate with given parameters
     *
     * @param fieldName     Field name
     * @param previousState Previous state of field
     * @param currentState  Current state of field
     * @param auditType     Type of audit
     * @param historable    Is historable field
     * @param notfiable     Is notfiable field
     */
    public AuditableFieldHistory(String fieldName, Object previousState, Object currentState, AuditChangeTypeEnum auditType, boolean historable, boolean notfiable) {
        super();
        this.fieldName = fieldName;
        this.currentState = currentState;
        this.previousState = previousState;
        this.auditType = auditType;
        this.historable = historable;
        this.notfiable = notfiable;
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

    /**
     * Gets the auditType
     *
     * @return the auditType
     */
    public AuditChangeTypeEnum getAuditType() {
        return auditType;
    }

    /**
     * Sets the auditType.
     *
     * @param auditType the new auditType
     */
    public void setAuditType(AuditChangeTypeEnum auditType) {
        this.auditType = auditType;
    }

    /**
     * Gets the historable
     *
     * @return the historable
     */
    public boolean isHistorable() {
        return historable;
    }

    /**
     * Sets the historable.
     *
     * @param historable the new historable
     */
    public void setHistorable(boolean historable) {
        this.historable = historable;
    }

    /**
     * Gets the notfiable
     *
     * @return the notfiable
     */
    public boolean isNotfiable() {
        return notfiable;
    }

    /**
     * Sets the notfiable.
     *
     * @param notfiable the new notfiable
     */
    public void setNotfiable(boolean notfiable) {
        this.notfiable = notfiable;
    }

    /**
     * Gets the historized
     *
     * @return the historized
     */
    public boolean isHistorized() {
        return historized;
    }

    /**
     * Sets the historized.
     *
     * @param historized the new historized
     */
    public void setHistorized(boolean historized) {
        this.historized = historized;
    }

    /**
     * Gets the notified
     *
     * @return the notified
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * Sets the notified.
     *
     * @param notified the new notified
     */
    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditableFieldHistory that = (AuditableFieldHistory) o;
        return Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(previousState, that.previousState) &&
                Objects.equals(currentState, that.currentState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, previousState, currentState);
    }

    @Override
    public String toString() {
        return "AuditableFieldHistory{" +
                "fieldName='" + fieldName + '\'' +
                ", previousState=" + previousState +
                ", currentState=" + currentState +
                ", auditType=" + auditType +
                ", historable=" + historable +
                ", notfiable=" + notfiable +
                ", historized=" + historized +
                ", notified=" + notified +
                '}';
    }
}