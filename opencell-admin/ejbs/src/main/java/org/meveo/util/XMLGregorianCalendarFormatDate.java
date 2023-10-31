package org.meveo.util;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;

public final class XMLGregorianCalendarFormatDate {
	
	static final String SIMPLE_FORMAT = "yyyy-MM-dd";
	static SimpleDateFormat fmt = new SimpleDateFormat(SIMPLE_FORMAT);
	public static XMLGregorianCalendar parse(String v){
		return null;
	}
	
	public static String marshal(XMLGregorianCalendar v) {
		if(v == null)
			return null;
		v.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		fmt.setCalendar(v.toGregorianCalendar());
		return fmt.format(v.toGregorianCalendar().getTime());
	}
}
