package org.meveo.api.Interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.seam.international.status.Messages;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.SecuredBusinessEntityProperty;
import org.meveo.model.admin.SecuredEntity;
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

		log.debug(LOG_LINE_SEPARATOR);
		log.debug("Checking method {}.{} for secured BusinessEntities", objectName, methodName);

		validateUserAccessToSecuredEntities(context);

		log.debug(LOG_LINE_SEPARATOR);
		log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);

		return context.proceed();
	}

	private void validateUserAccessToSecuredEntities(InvocationContext context) throws MeveoApiException {

		SecuredBusinessEntityProperty annotation = context.getMethod().getAnnotation(SecuredBusinessEntityProperty.class);

		if (annotation != null) {

			String property = annotation.property();
			Class<?> dtoClass = annotation.dtoClass();
			Class<?> entityClass = annotation.entityClass();

			Map<Class<?>, Object> parameterMap = parameterToMap(context.getParameters());
			User user = getUserFromParameters(parameterMap);

			if (user.getSecuredEntities() != null && !user.getSecuredEntities().isEmpty()) {
				try {
					String code = null;
					if (dtoClass.equals(Null.class)) {
						code = (String) parameterMap.get(String.class);
					} else {
						Object dto = parameterMap.get(dtoClass);
						code = (String) getPropertyValue(dto, property);
					}

					if (code != null) {
						BusinessEntity entity = (BusinessEntity) entityClass.newInstance();
						entity.setCode(code);
						Set<SecuredEntity> securedEntities = user.getSecuredEntities();
						boolean allow = entityFoundInSecuredEntities(entity, securedEntities);
						if (!allow) {
							// Check entity parents if they are found in
							// securedEntities. In order to get the entity's
							// parents, the entity is retrieved from DB.
							SecuredBusinessEntityService service = factory.getService(entity.getClass().getTypeName());
							entity = service.getEntityByCode(code, user);
							if (entity != null) {
								for (BusinessEntity parentEntity : service.getParentEntities(entity)) {
									if (entityFoundInSecuredEntities(parentEntity, securedEntities)) {
										allow = true;
										break;
									}
								}
							} else {
								// If the entity was not found, it does not
								// exist, i.e. nothing to secure. Therefore,
								// there is no need to check further.
								allow = true;
							}

						}

						// Still not found? it means user has no access to the
						// entity.
						if (!allow) {
							throwErrorMessage(entityClass);
						}

					} else {
						// The user is only allowed to access specific entities.
						// The entity's code must be provided.
						throwErrorMessage(entityClass);
					}

				} catch (IllegalAccessException e) {
					String message = String.format(FAILED_TO_RETRIEVE_PROPERTY, dtoClass.getSimpleName(), property);
					log.error(LOG_LINE_SEPARATOR);
					log.error(message, e);
					throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, message);
				} catch (InstantiationException e) {
					String message = String.format(FAILED_TO_INSTANTIATE_INSTANCE, entityClass.getSimpleName());
					log.error(LOG_LINE_SEPARATOR);
					log.error(message, e);
					throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, message);
				}
			} else {
				log.debug(LOG_LINE_SEPARATOR);
				log.debug("User does not have any related secured entities.");
			}
		}
	}

	private User getUserFromParameters(Map<Class<?>, Object> parameterMap) {
		User user = (User) parameterMap.get(User.class);
		if (user != null) {
			user = userService.refreshOrRetrieve(user);
		}
		return user;
	}

	private void throwErrorMessage(Class<?> entityClass) throws MeveoApiException {
		String message = String.format(ENTITY_ACCESS_ERROR, ReflectionUtils.getHumanClassName(entityClass.getTypeName()));
		log.error(LOG_LINE_SEPARATOR);
		log.error(message);
		throw new MeveoApiException(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION, message);
	}

	private boolean entityFoundInSecuredEntities(BusinessEntity entity, Set<SecuredEntity> securedEntities) {
		boolean found = false;
		for (SecuredEntity securedEntity : securedEntities) {
			if (entity.equals(securedEntity)) {
				found = true;
				break;
			}
		}
		return found;
	}

	private Map<Class<?>, Object> parameterToMap(Object[] parameters) {
		Map<Class<?>, Object> parameterMap = new HashMap<>();
		if (parameters != null) {
			for (Object parameter : parameters) {
				log.debug("Parameter {}", parameter == null ? null : parameter.toString());
				parameterMap.put(parameter.getClass(), parameter);
			}
		}
		return parameterMap;
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
