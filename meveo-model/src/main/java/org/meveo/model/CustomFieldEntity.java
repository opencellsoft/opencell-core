package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.model.crm.AccountLevelEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CustomFieldEntity {

    /**
     * (Required) A related custom field template account level
     */
    AccountLevelEnum accountLevel();    
}