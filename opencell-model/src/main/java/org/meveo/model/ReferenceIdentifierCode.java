package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies what attributes is treated as code identifier for reference. Use if entity is not BusinessEntity.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ReferenceIdentifierCode {
    String value();
}
