/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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