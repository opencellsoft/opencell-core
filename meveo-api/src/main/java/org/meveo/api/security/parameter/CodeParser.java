package org.meveo.api.security.parameter;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;

public class CodeParser extends SecureMethodParameterParser<BusinessEntity> {

	@Override
	public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException {
		if (parameter == null) {
			return null;
		}

		String code = (String) values[parameter.index()];
		if (StringUtils.isBlank(code)) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, CODE_REQUIRED);
		}
		Class<? extends BusinessEntity> entityClass = parameter.entity();

		BusinessEntity entity = null;
		try {
			entity = entityClass.newInstance();
			entity.setCode(code);
		} catch (InstantiationException | IllegalAccessException e) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, entityClass.getSimpleName()), e);
		}

		return entity;
	}

}
