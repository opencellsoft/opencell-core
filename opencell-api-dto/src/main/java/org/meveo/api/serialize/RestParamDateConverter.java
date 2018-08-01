package org.meveo.api.serialize;

import java.lang.annotation.Annotation;
import java.util.Date;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.DateUtils;

/**
 * Convert string parameter to a date. Example taken from https://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html/StringConverter.html
 * 
 */
public class RestParamDateConverter implements StringParameterUnmarshaller<Date> {


    public void setAnnotations(Annotation[] annotations) {
        // DateFormat format = FindAnnotation.findAnnotation(annotations, DateFormat.class);
        // formatter = new SimpleDateFormat(format.value());
    }

    public Date fromString(String str) {
        if (!StringUtils.isBlank(str)) {
            return DateUtils.guessDate(str, "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss");
        } else {
            return null;
        }
    }
}