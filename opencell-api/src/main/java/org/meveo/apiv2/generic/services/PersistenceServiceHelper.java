package org.meveo.apiv2.generic.services;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.PersistenceService;
import org.reflections.Reflections;

/**
 * Helper class for entities and core persistence services.
 */
public final class PersistenceServiceHelper {

    /**
	 * @param entityClass
	 * @return
	 */
	public static PersistenceService getPersistenceService(Class entityClass) {
		return getPersistenceService(entityClass, null);
	}
	
    /**
     * Get a persistence service for an entity class.
     * If it doesn't exist then get {@link BaseEntityService}
     *
     * @param entityClass entity class
     * @return corresponding entity's persistence service
     */
    public static PersistenceService getPersistenceService(Class entityClass, PaginationConfiguration searchConfig) {
        if(Modifier.isAbstract(entityClass.getModifiers()) && searchConfig!=null && searchConfig.getFilters()!=null) {
        	final Set<String> filterKeys = searchConfig.getFilters().keySet();
			if(!allFieldsExistsOnClass(entityClass, filterKeys)) {
	        	Reflections reflections = new Reflections(entityClass);
	        	Set<Class> classes = reflections.getSubTypesOf(entityClass);
	        	for(Class subclass : classes) {
	        		if(allFieldsExistsOnClass(subclass, filterKeys)) {
	        			entityClass = subclass;
	        		}
	        	}
			}
        }
		String entityName = entityClass.getSimpleName().replace("Impl", "").replace("MediationSetting", "Mediationsetting");
		PersistenceService serviceInterface = (PersistenceService) EjbUtils.getServiceInterface( entityName+ "Service");
        if(serviceInterface == null){
            serviceInterface = (PersistenceService) EjbUtils.getServiceInterface("BaseEntityService");
            ((BaseEntityService) serviceInterface).setEntityClass(entityClass);
        }
        return serviceInterface;
    }
    
    /**
	 * @param subclass
	 * @param filterKeys
	 * @return
	 */
	private static boolean allFieldsExistsOnClass(Class clazz, Set<String> filterKeys) {
		final Set<Field> allFields = listAllFields(clazz);
		return allFields.containsAll(filterKeys);
	}

	private static Set<Field> listAllFields(Class clazz) {
        Set<Field> fields = new HashSet<Field>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz .getSuperclass();
        }
        return fields;
    }

    /**
     * Get a persistence service for an entity class by its name.
     * If it doesn't exist then get {@link BaseEntityService}
     *
     * @param entityName entity name
     * @return corresponding entity's persistence service
     */
    public static PersistenceService getPersistenceService(String entityName) {
        return getPersistenceService(GenericHelper.getEntityClass(entityName));
    }

	/**
     * Get the function that represent {@link #getPersistenceService(Class)}
     * to use in lambda expressions
     *
     * @return {@code Function} representing {@link #getPersistenceService(Class)}
     */
    public static Function<Class, PersistenceService> getPersistenceService(){
        return PersistenceServiceHelper::getPersistenceService;
    }
}
