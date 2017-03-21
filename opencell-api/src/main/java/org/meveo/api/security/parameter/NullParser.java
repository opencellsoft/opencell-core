package org.meveo.api.security.parameter;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.BusinessEntity;

/**
 * This parser can be used if there is no need to validate the parameters of a method of if there are no parameters on the method.
 * @author Tony Alejandro.
 */
public class NullParser extends SecureMethodParameterParser<BusinessEntity>{
	@Override
	public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values) throws MeveoApiException {
		return null;
	}
}
