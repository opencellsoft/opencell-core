package org.meveo.model.audit.hibernate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to indicate to the hibernate interceptor, the field that will be audited.
 *
 * @author Abdellatif BARI
 * @since 5.3
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditTarget {
    AuditChangeType type();
}
