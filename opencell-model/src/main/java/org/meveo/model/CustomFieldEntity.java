package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author phung
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CustomFieldEntity {

    /**
     * (Required) A custom field template code prefix.
     */
    /**
     * @return cft code prefix
     */
    String cftCodePrefix() default "";

    /**
     * Additional fields of an entity that should be included to complete a custom field template code value.
     * 
     * @return array of CFT code fields.
     */
    String[] cftCodeFields() default {};

    /**
     * Setting to true will allow the entity to be shown in the Custom Entities page.
     * 
     * @return true/false
     */
    boolean isManuallyManaged() default true;

    /**
     * Parent custom field entity (field names) in case custom field values should be inherited from a parent entity
     * 
     * @return Array of entity field names
     */
    String[] inheritCFValuesFrom() default {};

    /**
     * Should custom field values be inherited from a provider
     * 
     * @return True if value should be inherited
     */
    boolean inheritFromProvider() default false;
}