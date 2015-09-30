package org.meveo.commons.utils.beanio;

import java.util.Locale;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.meveo.commons.utils.StringUtils;

public class UpperCaseHandler implements TypeHandler {

	public Object parse(String text) throws TypeConversionException {
		if(StringUtils.isBlank(text)){
			return text;
		}
		return StringUtils.truncate(text, 50, true).toUpperCase(Locale.getDefault());
    }
	
    public String format(Object value) {
		return (String)value;
    }
    
    public Class<?> getType() {
        return String.class;
    }
}
