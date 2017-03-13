package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies what attributes are treated as identifiers for export. If no annotation is present, ID will be used as default
 * 
 * @author Andrius Karpavicius
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ExportIdentifier {
    String[] value();
}
