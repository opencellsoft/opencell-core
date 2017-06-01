package org.meveo.audit.logging.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Edward P. Legaspi
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.PARAMETER, ElementType.FIELD })
public @interface DeIdentify {

	/**
	 * Left.
	 * 
	 * @return the int
	 */
	public int left() default 0;

	/**
	 * Right.
	 * 
	 * @return the int
	 */
	public int right() default 0;

	/**
	 * From left.
	 * 
	 * @return the int
	 */
	public int fromLeft() default 0;

	/**
	 * From right.
	 * 
	 * @return the int
	 */
	public int fromRight() default 0;

}