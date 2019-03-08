package org.meveo.model.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the field that will be audited.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditTarget {
    AuditChangeType type();

    boolean history() default false;
    boolean notif() default false;
}
