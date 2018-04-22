package org.meveo.admin.jsf.validator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
        Date from = (Date) values.get(0);
        Date to = (Date) values.get(1);

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