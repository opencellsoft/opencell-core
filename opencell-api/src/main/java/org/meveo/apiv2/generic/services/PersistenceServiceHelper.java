package org.meveo.apiv2.generic.services;

import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.PersistenceService;

import java.util.function.Function;

/**
 * Helper class for entities and core persistence services.
 */
public final class PersistenceServiceHelper {

    /**
     * Get a persistence service for an entity class.
     * If it doesn't exist then get {@link BaseEntityService}
     *
     * @param entityClass entity class
     * @return corresponding entity's persistence service
     */
    public static PersistenceService getPersistenceService(Class entityClass) {
        PersistenceService serviceInterface = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
        if(serviceInterface == null){
            serviceInterface = (PersistenceService) EjbUtils.getServiceInterface("BaseEntityService");
            ((BaseEntityService) serviceInterface).setEntityClass(entityClass);
        }
        return serviceInterface;
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
