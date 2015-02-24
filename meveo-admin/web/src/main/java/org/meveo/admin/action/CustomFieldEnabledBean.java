package org.meveo.admin.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.model.crm.AccountLevelEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CustomFieldEnabledBean {

    /**
     * (Required) A related custom field template account level
     */
    AccountLevelEnum accountLevel();
}
