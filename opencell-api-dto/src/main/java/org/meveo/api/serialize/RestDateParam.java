package org.meveo.api.serialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;

/**
 * The Interface RestDateParam.
 * 
 * @author anasseh
 */
@Retention(RetentionPolicy.RUNTIME)
@StringParameterUnmarshallerBinder(RestParamDateConverter.class)
public @interface RestDateParam {
    // String value();
}