package org.meveo.api.Interceptor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.seam.international.status.Messages;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.SBEParam;
import org.meveo.model.SBEParamType;
import org.meveo.model.SecuredBusinessEntityProperty;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.meveo.service.security.SecuredBusinessEntityServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredBusinessEntityMethodInterceptor implements Serializable {

	private static final String LOG_LINE_SEPARATOR = "\r\n\r\n===========================================================";

	private static final long serialVersionUID = 4656634337151866255L;

	private static final Logger log = LoggerFactory.getLogger(SecuredBusinessEntityMethodInterceptor.class);

	private static final String ENTITY_ACCESS_ERROR = "Access to %s entity details is not allowed";

	private static final String FAILED_TO_INSTANTIATE_INSTANCE = "Failed to instantiate instance of %s.";

	private static final String FAILED_TO_RETRIEVE_PROPERTY = "Failed to retrieve property %s.%s";

	@Inject
	private UserService userService;

	@Inject
	private SecuredBusinessEntityServiceFactory factory;

	@Inject
	protected Messages messages;

	@AroundInvoke
	public Object checkForSecuredEntities(InvocationContext context) throws Exception {

		Class<?> objectClass = context.getMethod().getDeclaringClass();
		String objectName = objectClass.getSimpleName();
		String methodName = context.getMethod().getName();

		SecuredBusinessEntityProperty annotation = context.getMethod().getAnnotation(SecuredBusinessEntityProperty.class);

		if (annotation == null) {
			log.debug(LOG_LINE_SEPARATOR);
			log.debug("Method {}.{} is not annotated with @SecuredBusinessEntityProperty.  No need to check for authorization.", objectName, methodName);
		} else {
			log.debug(LOG_LINE_SEPARATOR);
			log.debug("Checking method {}.{} for secured BusinessEntities", objectName, methodName);
			validateUserAccessToSecuredEntities(context, annotation);
		}

		log.debug(LOG_LINE_SEPARATOR);
		log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);

		return context.proceed();
	}

	private void validateUserAccessToSecuredEntities(InvocationContext context, SecuredBusinessEntityProperty annotation) throws MeveoApiException {

		SBEParam[] sbeParams = annotation.parameters();
		Object[] parameters = context.getParameters();

		Class<?> entityClass = annotation.entityClass();

		User user = getUserFromParameters(sbeParams, parameters);

		if (user.getSecuredEntities() == null || user.getSecuredEntities().isEmpty()) {
			log.debug(LOG_LINE_SEPARATOR);
			log.debug("User does not have any restrictions.");
			return;
		}

		try {

			String code = null;
			SBEParam dtoParam = getSBEParamByType(sbeParams, SBEParamType.REQUEST_DTO);

			if (dtoParam == null) {
				SBEParam codeParam = getSBEParamByType(sbeParams, SBEParamType.CODE);
				code = (String) getValueByType(codeParam, parameters);
			} else {
				code = (String) getDtoPropertyValue(dtoParam, parameters);
			}

			if (code == null) {
				// The user is only allowed to access specific entities. The
				// entity's code must be provided.
				throwErrorMessage(entityClass);
			}

			BusinessEntity entity = (BusinessEntity) entityClass.newInstance();
			entity.setCode(code);
			boolean allow = SecuredBusinessEntityService.isEntityAllowed(entity, user, factory, false);
			if (!allow) {
				throwErrorMessage(entityClass);
			}
		} catch (IllegalAccessException | InstantiationException e) {
			String message = String.format(FAILED_TO_INSTANTIATE_INSTANCE, entityClass.getSimpleName());
			log.error(LOG_LINE_SEPARATOR);
			log.error(message, e);
			throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, message);
		}
	}

	private SBEParam getSBEParamByType(SBEParam[] sbeParameters, SBEParamType type) {
		for (SBEParam sbeParam : sbeParameters) {
			if (type.equals(sbeParam.type())) {
				return sbeParam;
			}
		}
		return null;
	}

	private Object getValueByType(SBEParam sbeParameter, Object[] contextParameters) {
		if (sbeParameter == null) {
			return null;
		}
		return contextParameters[sbeParameter.index()];
	}

	private User getUserFromParameters(SBEParam[] sbeParams, Object[] parameters) {
		SBEParam userParam = getSBEParamByType(sbeParams, SBEParamType.USER);
		User user = (User) getValueByType(userParam, parameters);
		if (user != null) {
			user = userService.refreshOrRetrieve(user);
		}
		return user;
	}

	private String getDtoPropertyValue(SBEParam dtoParam, Object[] parameters) throws MeveoApiException {
		Class<?> dtoClass = dtoParam.dataClass();
		String property = dtoParam.property();
		String code = null;
		try {
			Object dto = getValueByType(dtoParam, parameters);
			code = (String) getPropertyValue(dto, dtoParam.property());
		} catch (IllegalAccessException e) {
			String message = String.format(FAILED_TO_RETRIEVE_PROPERTY, dtoClass.getSimpleName(), property);
			log.error(LOG_LINE_SEPARATOR);
			log.error(message, e);
			throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, message);
		}
		return code;
	}

	private void throwErrorMessage(Class<?> entityClass) throws MeveoApiException {
		String message = String.format(ENTITY_ACCESS_ERROR, ReflectionUtils.getHumanClassName(entityClass.getTypeName()));
		log.error(LOG_LINE_SEPARATOR);
		log.error(message);
		throw new MeveoApiException(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, message);
	}

	private Object getPropertyValue(Object obj, String property) throws IllegalAccessException {
		int fieldIndex = property.indexOf(".");
		if (property.indexOf(".") != -1) {
			String fieldName = property.substring(0, fieldIndex);
			Object fieldValue = FieldUtils.readField(obj, fieldName, true);
			return getPropertyValue(fieldValue, property.substring(fieldIndex + 1));
		} else {
			return FieldUtils.readField(obj, property, true);
		}
	}

}
