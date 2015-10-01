package org.meveo.commons.utils.beanio;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

public class PaymentTypeHandler implements TypeHandler{

	public Object parse(String text) throws TypeConversionException {
		if("VIREMENT".equals(text))  return "WIRETRANSFER";
		if("PRELEVEMENT".equals(text))  return "DIRECTDEBIT";
		if("CHEQUE".equals(text))  return "CHECK";
		if("CARTE".equals(text))  return "CARD";
		throw new TypeConversionException("Unknown payment method '"+text+"'");
       
    }
    public String format(Object value) {
		if("WIRETRANSFER".equals((String)value))  return "VIREMENT";
		if("DIRECTDEBIT".equals((String)value))  return "PRELEVEMENT";
		if("CHECK".equals((String)value))  return "CHEQUE";
		if("CARD".equals((String)value))  return "CARTE";
		return (String)value;
    }
    public Class<?> getType() {
        return String.class;
    }
}
