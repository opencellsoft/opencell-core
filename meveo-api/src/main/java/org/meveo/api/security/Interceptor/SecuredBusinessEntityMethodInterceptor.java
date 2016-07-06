package org.meveo.api.security.Interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.SecureMethodParameterHandler;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.meveo.service.security.SecuredBusinessEntityServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredBusinessEntityMethodInterceptor implements Serializable {

	private static final long serialVersionUID = 4656634337151866255L;

	private static final Logger log = LoggerFactory.getLogger(SecuredBusinessEntityMethodInterceptor.class);

	private static final String ALLOWING_METHOD_TO_BE_INVOKED = "Allowing method {}.{} to be invoked.";
	private static final String CHECKING_METHOD_FOR_SECURED_BUSINESS_ENTITIES = "Checking method {}.{} for secured BusinessEntities";
	private static final String METHOD_IS_NOT_ANNOTATED = "Method {}.{} is not annotated with @SecuredBusinessEntityProperty.  No need to check for authorization.";
	private static final String LOG_LINE_SEPARATOR = "\r\n\r\n===========================================================";
	private static final String ACCESS_TO_ENTITY_DENIED = "Access to entity details is not allowed.";
	private static final String USER_DOES_NOT_HAVE_ANY_RESTRICTIONS = "User does not have any restrictions.";
	private static final String FILTER_RESULTS_WITH = "Results will be filtered using {} filter.";

	@Inject
	private SecuredBusinessEntityServiceFactory serviceFactory;

	@Inject
	private SecureMethodResultFilterFactory filterFactory;

	@Inject
	private SecureMethodParameterHandler parameterHandler;

	@AroundInvoke
	public Object checkForSecuredEntities(InvocationContext context) throws Exception {

		Class<?> objectClass = context.getMethod().getDeclaringClass();
		String objectName = objectClass.getSimpleName();
		String methodName = context.getMethod().getName();

		SecuredBusinessEntityMethod annotation = context.getMethod().getAnnotation(SecuredBusinessEntityMethod.class);
		if (annotation == null) {
			log.debug(METHOD_IS_NOT_ANNOTATED, objectName, methodName);
			return context.proceed();
		}

		SecureMethodParameter userParameter = annotation.user();
		Object[] values = context.getParameters();
		User user = parameterHandler.getParameterValue(userParameter, values, User.class, null);

		boolean hasRestrictions = user != null && user.getSecuredEntities() != null && !user.getSecuredEntities().isEmpty();

		if (!hasRestrictions) {
			log.debug(USER_DOES_NOT_HAVE_ANY_RESTRICTIONS);
			return context.proceed();
		}
		Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap = getSecuredEntitiesMap(user.getSecuredEntities());

		log.debug(CHECKING_METHOD_FOR_SECURED_BUSINESS_ENTITIES, objectName, methodName);
		SecureMethodParameter[] parametersForValidation = annotation.validate();
		for (SecureMethodParameter parameter : parametersForValidation) {
			BusinessEntity entity = parameterHandler.getParameterValue(parameter, values, BusinessEntity.class, user);
			if (!SecuredBusinessEntityService.isEntityAllowed(entity, user, serviceFactory, securedEntitiesMap, false)) {
				throwErrorMessage(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, ACCESS_TO_ENTITY_DENIED);
			}
		}

		log.debug(ALLOWING_METHOD_TO_BE_INVOKED, objectName, methodName);
		Object result = context.proceed();

		SecureMethodResultFilter filter = filterFactory.getFilter(annotation.resultFilter());
		log.debug(FILTER_RESULTS_WITH, filter);
		result = filter.filterResult(result, user, securedEntitiesMap);
		return result;

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
			log.warn(e.getMessage(), e);
			;
		}
		return securedEntitiesMap;
	}

}
