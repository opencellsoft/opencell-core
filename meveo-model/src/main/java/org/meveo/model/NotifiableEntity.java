package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  With this annotation can be used as classNameFilter for notifications, it can trigger a notifiable event at any time, any position
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface NotifiableEntity {

}


