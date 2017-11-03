package org.meveo.api.dto.account;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.model.BusinessEntity;

/**
 * Specified what properties of an object to apply filtering. Used in conjunction with {@link FilterResults}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FilterProperty {

    String property();

    /**
     * Identifies the entity type that property value corresponds to. e.g. if CustomerAccount.class is passed into this attribute, then property value will correspond to code field
     * of a CustomerAccount object.
     * 
     * @return
     */
    Class<? extends BusinessEntity> entityClass();
}
