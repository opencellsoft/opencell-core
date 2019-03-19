package org.meveo.model.audit;

/**
 * Indicates the name of auditable field.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public enum AuditableFieldNameEnum {
    STATUS("status");

    /**
     * field name
     */
    private String fieldName;

    /**
     * field name
     *
     * @return field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * private constructor
     *
     * @param fieldName field name
     */
    AuditableFieldNameEnum(final String fieldName) {
        this.fieldName = fieldName;
    }
}
