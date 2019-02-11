package org.meveo.model.audit.hibernate;

/**
 * Indicates the type of audit that allows to choose the treatment to be done later.
 *
 * @author Abdellatif BARI
 * @author Mounir Bahije
 * @since 5.3
 */
public enum AuditChangeType {
    STATUS, SUBSCRIPTION_RENEWAL;
}
