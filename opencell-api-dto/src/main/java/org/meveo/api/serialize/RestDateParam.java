package org.meveo.api.serialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;

@Retention(RetentionPolicy.RUNTIME)
@StringParameterUnmarshallerBinder(RestParamDateConverter.class)
public @interface RestDateParam {
    // String value();
}