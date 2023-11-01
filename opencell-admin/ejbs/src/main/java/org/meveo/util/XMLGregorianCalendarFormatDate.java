package org.meveo.util;

import org.meveo.admin.exception.BusinessException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public final class XMLGregorianCalendarFormatDate {
	
	
	private XMLGregorianCalendarFormatDate() {
	}
	
	static final String SIMPLE_FORMAT = "yyyy-MM-dd";
	public static XMLGregorianCalendar parse(String v){
		if(v == null)
			return null;
		Date date;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat(SIMPLE_FORMAT);
			date = fmt.parse(v);
		} catch (ParseException e) {
			throw new BusinessException(e);
		}
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		try {
			return  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			throw new BusinessException(e);
		}
	}
	
	public static String marshal(XMLGregorianCalendar v) {
		if(v == null)
			return null;
		SimpleDateFormat fmt = new SimpleDateFormat(SIMPLE_FORMAT);
		v.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		fmt.setCalendar(v.toGregorianCalendar());
		return fmt.format(v.toGregorianCalendar().getTime());
	}
}
