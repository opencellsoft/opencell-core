package org.meveo.api.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.meveo.model.shared.DateUtils;

/**
 * This is Adaptor class which has main responsibility to convert from java.util.Date to format string of date. For unmarshaling will support a number of formats:
 * DateUtils.DATE_TIME_PATTERN, meveo.dateTimeFormat (defaults to "dd/MM/yyyy HH:mm"), DateUtils.DATE_FORMAT, meveo.dateFormat (defaults to "dd/MM/yyyy")
 *
 * @author Abdellatif BARI
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.length() == 0) {
            return null;
        } else {
            return DateUtils.parseDate(dateTimeString);
        }
    }

    @Override
    public String marshal(Date object) {
        if (object == null) {
            return null;
        } else {
            return DateUtils.formatDateWithPattern(object, DateUtils.DATE_TIME_PATTERN);
        }
    }
}
