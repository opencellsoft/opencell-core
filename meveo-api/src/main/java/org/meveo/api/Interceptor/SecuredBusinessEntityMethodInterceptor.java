package org.meveo.api.Interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.SBEParam;
import org.meveo.model.SBEParamType;
import org.meveo.model.SecuredBusinessEntityFilter;
import org.meveo.model.SecuredBusinessEntityProperty;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.meveo.service.security.SecuredBusinessEntityServiceFactory;
import org.meveo.service.security.filter.SecuredBusinessEntityFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredBusinessEntityMethodInterceptor implements Serializable {

	private static final long serialVersionUID = 4656634337151866255L;

	private static final Logger log = LoggerFactory.getLogger(SecuredBusinessEntityMethodInterceptor.class);

	private static final String ALLOWING_METHOD_TO_BE_INVOKED = "Allowing method {}.{} to be invoked.";
	private static final String CHECKING_METHOD_FOR_SECURED_BUSINESS_ENTITIES = "Checking method {}.{} for secured BusinessEntities";
	private static final String METHOD_IS_NOT_ANNOTATED = "Method {}.{} is not annotated with @SecuredBusinessEntityProperty.  No need to check for authorization.";
	private static final String LOG_LINE_SEPARATOR = "\r\n\r\n===========================================================";
	private static final String ENTITY_ACCESS_ERROR = "Access to entity details is not allowed.";
	private static final String FAILED_TO_INSTANTIATE_INSTANCE = "Failed to instantiate instance of %s.";
	private static final String CODE_REQUIRED_ERROR = "The entity code is required.";
	private static final String FAILED_TO_RETRIEVE_PROPERTY = "Failed to retrieve property %s.%s.";
	private static final String USER_DOES_NOT_HAVE_ANY_RESTRICTIONS = "User does not have any restrictions.";
	private static final String VALIDATE_USER_ACCESS = "User access to the entities will be validated.";
	private static final String USER_ACCESS_GRANTED = "User has access to the entities.";
	private static final String INITIATE_FILTER_RESULTS = "Filter results initiated.";

	@Inject
	private UserService userService;

	@Inject
	private SecuredBusinessEntityServiceFactory serviceFactory;

	@Inject
	private SecuredBusinessEntityFilterFactory filterFactory;

	@AroundInvoke
	public Object checkForSecuredEntities(InvocationContext context) throws Exception {

		Class<?> objectClass = context.getMethod().getDeclaringClass();
		String objectName = objectClass.getSimpleName();
		String methodName = context.getMethod().getName();

		User user = null;
		boolean hasRestrictions = false;
		SecuredBusinessEntityProperty annotation = context.getMethod().getAnnotation(SecuredBusinessEntityProperty.class);

		if (annotation == null) {
			log.debug(METHOD_IS_NOT_ANNOTATED, objectName, methodName);
		} else {
			log.debug(CHECKING_METHOD_FOR_SECURED_BUSINESS_ENTITIES, objectName, methodName);

			Object[] parameters = context.getParameters();
			SBEParam[] sbeParams = annotation.parameters();
			Class<? extends BusinessEntity> entityClass = annotation.entityClass();

			user = getUserFromParameters(sbeParams, parameters);
			hasRestrictions = user != null && user.getSecuredEntities() != null && !user.getSecuredEntities().isEmpty();

			if (hasRestrictions) {
				log.debug(VALIDATE_USER_ACCESS);
				validateUserAccessToSecuredEntities(entityClass, sbeParams, parameters, user);
			} else {
				log.debug(USER_DOES_NOT_HAVE_ANY_RESTRICTIONS);
			}
		}

		log.debug(ALLOWING_METHOD_TO_BE_INVOKED, objectName, methodName);

		Object result = context.proceed();

		if (hasRestrictions) {
			log.debug(INITIATE_FILTER_RESULTS);
			SecuredBusinessEntityFilter filter = filterFactory.getFilter(annotation.filterClass());
			result = filter.filterResult(result, user, getSecuredEntitiesMap(user.getSecuredEntities()));
		}

		return result;
	}

	private void validateUserAccessToSecuredEntities(Class<? extends BusinessEntity> entityClass, SBEParam[] sbeParams, Object[] parameters, User user) throws MeveoApiException {

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
			throwErrorMessage(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, CODE_REQUIRED_ERROR);
		}

		BusinessEntity entity = null;

		try {
			entity = entityClass.newInstance();
			entity.setCode(code);
			boolean allow = SecuredBusinessEntityService.isEntityAllowed(entity, user, serviceFactory, false);
			if (!allow) {
				throwErrorMessage(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, ENTITY_ACCESS_ERROR);
			}
		} catch (IllegalAccessException | InstantiationException e) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_INSTANCE, entityClass.getSimpleName()), e);
		}

		log.debug(USER_ACCESS_GRANTED);
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
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_RETRIEVE_PROPERTY, dtoClass.getSimpleName(), property), e);
		}
		return code;
	}

	private void throwErrorMessage(MeveoApiErrorCodeEnum errorCode, String message) throws MeveoApiException {
		throwErrorMessage(errorCode, message, null);
	}

	private void throwErrorMessage(MeveoApiErrorCodeEnum errorCode, String message, Throwable e) throws MeveoApiException {
		log.error(LOG_LINE_SEPARATOR);
		if (e == null) {
			log.error(message);
		} else {
			log.error(message, e);
		}
		throw new MeveoApiException(errorCode, message);
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

	private Map<Class<?>, Set<SecuredEntity>> getSecuredEntitiesMap(Set<SecuredEntity> securedEntities) {
		Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap = new HashMap<>();
		Set<SecuredEntity> securedEntitySet = null;
		try {
			for (SecuredEntity securedEntity : securedEntities) {
				Class<?> securedBusinessEntityClass = Class.forName(securedEntity.getEntityClass());
				if (securedEntitiesMap.get(securedBusinessEntityClass) == null) {
					securedEntitySet = new HashSet<>();
					securedEntitiesMap.put(securedBusinessEntityClass, securedEntitySet);
				}
				securedEntitiesMap.get(securedBusinessEntityClass).add(securedEntity);
			}
		} catch (ClassNotFoundException e) {
			log.warn(e.getMessage(), e);;
		}
		return securedEntitiesMap;
	}

}
