package org.meveo.commons.utils;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.meveo.model.MultilanguageEntity;
import org.reflections.Reflections;

public class ClassUtils {
	
	public static Class<?> getEntityClass(String className) {
		Class<?> entityClass = null;
		if (!StringUtils.isBlank(className)) {
			Set<Class<?>> multiLanguageClasses = getClassesOfType(MultilanguageEntity.class);
			for (Class<?> multiLanguageClass : multiLanguageClasses) {
				if (className.equals(multiLanguageClass.getSimpleName())) {
					entityClass = multiLanguageClass;
					break;
				}
			}
		}
		return entityClass;
	}
	
	public static Set<Class<?>> getClassesOfType(Class<? extends Annotation> type) {
		Reflections reflections = new Reflections("org.meveo.model");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(type);
		return classes;
	}
}
