package org.meveo.api.dto.account;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.model.BusinessEntity;

/**
 * Identifies the filtering rule to apply to items selected for filtering
 * 
 * Specifies how to reconstruct an object used to compare what user has access to. Used in conjunction with {@link FilterResults}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FilterProperty {

    /**
     * Name of a property of item selected for filtering. The value will be used to reconstruct an object of a given entity class
     * 
     * @return name of property.
     */
    String property();

    /**
     * Identifies the entity type that property value corresponds to. e.g. if CustomerAccount.class is passed into this attribute, then property value resolved from a "property"
     * will correspond to code field of a CustomerAccount object.
     * 
     * @return business entity class.
     */
    Class<? extends BusinessEntity> entityClass();

    /**
     * Shall access to an entity be granted in cases when property is resolved to a null value. If set to True, user will have access to entities that match his security settings
     * and those that have no property value set.
     * 
     * @return true/false
     */
    boolean allowAccessIfNull() default false;
}