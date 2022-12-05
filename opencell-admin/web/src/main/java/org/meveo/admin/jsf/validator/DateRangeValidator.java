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

package org.meveo.admin.jsf.validator;

import org.meveo.commons.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@Stateless
public class DateRangeValidator implements Serializable {

    private static final long serialVersionUID = -3269306744443460502L;

    /**
     * Validate that if two dates are provided, the From value is before the To value.
     * 
     * @param context Faces context
     * @param components Components being validated
     * @param values Values to validate
     * @return Is valid or not
     */
    public boolean validateDateRange(FacesContext context, List<UIInput> components, List<Object> values) {

        if (values.size() != 2) {
            throw new RuntimeException("Please bind validator to two components in the following order: dateFrom, dateTo");
        }
        Date from = !StringUtils.isBlank(values.get(0)) ? (Date) values.get(0) : null;
        Date to = !StringUtils.isBlank(values.get(1)) ? (Date) values.get(1) : null;

        // if (values.get(0) != null) {
        // if (values.get(0) instanceof String) {
        // from = DateUtils.parseDateWithPattern((String) values.get(0), datePattern);
        // } else {
        // from = (Date) values.get(0);
        // }
        // }
        // if (values.get(1) != null) {
        // if (values.get(1) instanceof String) {
        // to = DateUtils.parseDateWithPattern((String) values.get(1), datePattern);
        // } else {
        // to = (Date) values.get(1);
        // }
        // }

        // Check that two dates are one after another
        return !(from != null && to != null && from.compareTo(to) > 0);
    }
}