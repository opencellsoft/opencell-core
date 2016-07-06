package org.meveo.api.security.parameter;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;

public class UserParser extends SecureMethodParameterParser<User> {

	@Override
	public User getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException {
		if (parameter == null) {
			return null;
		}
		
		Object parameterValue = values[parameter.index()];
		if(!(parameterValue instanceof User)){
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(INVALID_PARAMETER_TYPE, User.class.getTypeName()));
		}
		
		return (User) parameterValue;
	}
}
