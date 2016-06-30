package org.meveo.service.security;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.SecuredBusinessEntity;
import org.meveo.model.SecuredBusinessEntityProperty;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service implementation.
 *
 * @author Tony Alejandro
 */
public abstract class SecuredBusinessEntityService {
	@Inject
	private Logger log;
	
	
	public abstract List<? extends BusinessEntity> list();
	public abstract Class<? extends BusinessEntity> getEntityClass();
	
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

	public Set<SecuredBusinessEntity> dtoToEntitySet(Class<?> objectClass, Object dto, String sourceMethod)
			throws BusinessException {
		SecuredBusinessEntityProperty annotation = (SecuredBusinessEntityProperty) ReflectionUtils
				.getMethodAnnotations(objectClass, sourceMethod, SecuredBusinessEntityProperty.class);
		if (annotation != null) {
			String property = annotation.property();
			Class<?> entityClass = annotation.entityClass();
			try {
				Object propertyValue = getPropertyValue(dto, property);
				objectClass.cast(propertyValue);

			} catch (IllegalAccessException e) {
				String message = "Failed to retrieve value of: " + property + " from Class: "
						+ ReflectionUtils.getHumanClassName(entityClass.getName());
				log.error(message, e);
				throw new BusinessException(message, e);
			}
		}

		return null;
	}

	public boolean isUserAllowedToAccessEntities(User user, Class<?> dtoClass, Object dto, String sourceMethod) {
		return true;
	}
	
	public Set<Class<?>> getSecuredBusinessEntityClasses() {
		return ReflectionUtils.getClassesAnnotatedWith(SecuredBusinessEntity.class);
	}
}