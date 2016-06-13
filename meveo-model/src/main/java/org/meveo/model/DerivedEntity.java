package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a JPA class field that requires a parser to derive the entity/entities within the field's value.
 *
 * @author Tony Alejandro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DerivedEntity {
}
