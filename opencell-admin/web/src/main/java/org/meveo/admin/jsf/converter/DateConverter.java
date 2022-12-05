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
package org.meveo.admin.jsf.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Named("dateConverter")
public class DateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        String dateFormat = paramBeanFactory.getInstance().getDateFormat();
        DateFormat df = new SimpleDateFormat(dateFormat, facesContext.getViewRoot().getLocale());

        try {
            return df.parse(str);
        } catch (ParseException e) {
            return "";
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object obj) {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        String dateFormat = paramBeanFactory.getInstance().getDateFormat();
        DateFormat df = new SimpleDateFormat(dateFormat, facesContext.getViewRoot().getLocale());

        return df.format(obj);
    }

}
