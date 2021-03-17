package org.meveo.commons.utils.beanio;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.LoggerFactory;

public class DoubleTypeHandler implements TypeHandler {
	
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(DoubleTypeHandler.class);

	public Object parse(String text) throws TypeConversionException {
		if(StringUtils.isBlank(text)){
			return null;
		}
		Double d = null;
		try{
			d = new Double(text.replaceAll(",", "."));
		}catch(Exception e){
			log.error("error = {}", e);
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