package org.meveo.commons.utils.beanio;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.meveo.commons.utils.StringUtils;

public class DoubleTypeHandler implements TypeHandler {

	public Object parse(String text) throws TypeConversionException {
		if(StringUtils.isBlank(text)){
			return null;
		}
		Double d = null;
		try{
			d = new Double(text.replaceAll(",", "."));
		}catch(Exception e){
			e.printStackTrace();
			throw new TypeConversionException("Cant parse double '"+text+"'");
		}
		
		return   d;
    }
	
    public String format(Object value) {
		return (String)value;
    }
    
    public Class<?> getType() {
        return Double.class;
    }
}