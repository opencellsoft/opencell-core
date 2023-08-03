package org.meveo.service.audit;

import java.io.Serializable;

import org.meveo.model.audit.AuditChangeTypeEnum;

/**
 * Summarize @AuditTarget annotation information about a field which value change to track
 */
public class AuditFieldInfo implements Serializable {

    private static final long serialVersionUID = -6715859202488576517L;

    /**
     * Field name to track
     */
    String fieldName;

    /**
     * Indicates the type of audit that allows to choose the treatment to be done later
     */
    AuditChangeTypeEnum changeType;

    /**
     * Should a record about field value change be stored in DB
     */
    boolean preserveHistory;

    /**
     * Should notification be fired upon field value change
     */
    boolean notify;

    /**
     * Is it a composite/embedded field where composite field values must be collected
     */
    boolean compositeField;

    /**
     * Constructor
     * 
     * @param fieldName Field name to track
     * @param changeType Indicates the type of audit that allows to choose the treatment to be done later
     * @param preserveHistory Should a record about field value change be stored in DB.
     * @param notify Should notification be fired upon field value change
     * @param compositeField Is it a composite/embedded field where composite field values must be collected
     */
    public AuditFieldInfo(String fieldName, AuditChangeTypeEnum changeType, boolean preserveHistory, boolean notify, boolean compositeField) {
        super();
        this.fieldName = fieldName;
        this.changeType = changeType;
        this.preserveHistory = preserveHistory;
        this.notify = notify;
        this.compositeField = compositeField;
    }

    /**
     * @return Field name to track
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return Indicates the type of audit that allows to choose the treatment to be done later
     */
    public AuditChangeTypeEnum getChangeType() {
        return changeType;
    }

    /**
     * @return Should a record about field value change be stored in DB
     */
    public boolean isPreserveHistory() {
        return preserveHistory;
    }

    /**
     * @return Should notification be fired upon field value change
     */
    public boolean isNotify() {
        return notify;
    }

    /**
     * @return Is it a composite/embedded field where composite field values must be collected
     */
    public boolean isCompositeField() {
        return compositeField;
    }
}