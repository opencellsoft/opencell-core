/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * String converter, that converts empty string to null if needed for backend.
 * 
 * (Currently used only in billingAccount email field, because if email is not
 * null validator still validates it as wrong, so it has to be passed as null)
 * 
 * @author Ignas Lelys
 * @created Feb 17, 2011
 * 
 */
@Name("nullableStringConverter")
@org.jboss.seam.annotations.faces.Converter
@BypassInterceptors
public class NullableStringConverter implements Converter {

    /**
     * Get the given value as String. In case of an empty String, null is
     * returned.
     * 
     * @param value
     *            the value of the control
     * @param facesContext
     *            current facesContext
     * @param uiComponent
     *            the uicomponent providing the value
     * @return the given value as String. In case of an empty String, null is
     *         returned.
     * 
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

        if (facesContext == null) {
            throw new NullPointerException("facesContext");
        }
        if (uiComponent == null) {
            throw new NullPointerException("uiComponent");
        }

        return stringToValue(value);
    }

    /**
     * Convert the String to value (String or null).
     * 
     * @param value
     *            the string from webcomponent
     * @return the object (null if trimmed String is Empty String)
     */
    protected Object stringToValue(String value) {

        if (value != null) {
            value = value.trim();
            if (value.length() > 0) {
                return value + "";
            }
        }
        return null;
    }

    /**
     * Convert the value to String for web control.
     * 
     * @param value
     *            the value to be set
     * @param facesContext
     *            current facesContext
     * @param uiComponent
     *            the uicomponent to show the value
     * @return the String-converted parameter
     * 
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {

        if (facesContext == null) {
            throw new NullPointerException("facesContext");
        }
        if (uiComponent == null) {
            throw new NullPointerException("uiComponent");
        }
        return valueToString(value);
    }

    /**
     * Converts the value to HTMLized String.
     * 
     * @param value
     *            the object to be converted
     * @return String representation
     */
    protected String valueToString(Object value) {

        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value + "";
    }
}