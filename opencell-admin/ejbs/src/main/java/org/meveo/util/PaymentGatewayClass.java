/**
 * 
 */
package org.meveo.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Used on a class that implement a paymentGateway.
 * 
 * @author anasseh
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface PaymentGatewayClass {

}
