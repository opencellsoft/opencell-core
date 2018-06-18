package org.meveo.export;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;

import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;

/**
 * Converts a java.math.BigDecimal to a String, with custom precision.
 * 
 * @author Edward P. Legaspi
 */
public class CustomBigDecimalConverter extends BigDecimalConverter {

	ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(BigDecimal.class);
	}

	@Override
	public Object fromString(String str) {
		return new BigDecimal(str);
	}

	@Override
	public String toString(Object obj) {
		return obj == null ? BigDecimal.ZERO.toString() : NumberFormat.getNumberInstance().format(new BigDecimal(obj.toString()));
	}

}
