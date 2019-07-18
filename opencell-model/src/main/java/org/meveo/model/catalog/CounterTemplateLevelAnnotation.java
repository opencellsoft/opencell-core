package org.meveo.model.catalog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Khalid HORRI
 * @lastModifiedVersion 6.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CounterTemplateLevelAnnotation {
    public CounterTemplateLevel value() default CounterTemplateLevel.UA;
}
