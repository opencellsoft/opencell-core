package org.meveo.model;

import java.lang.annotation.*;

/**
 * Specifies what named query can be used to get a list of entities.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 5.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ReferenceIdentifierQuery {
    String value();
}
