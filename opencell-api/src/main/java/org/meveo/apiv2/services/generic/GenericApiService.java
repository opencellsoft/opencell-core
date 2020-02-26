package org.meveo.apiv2.services.generic;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;
import static org.meveo.apiv2.ValidationUtils.checkRecord;

@Stateless
public abstract class GenericApiService {
    
    @Inject
    @MeveoJpa
    protected EntityManagerWrapper entityManagerWrapper;
    
    public BaseEntity find(Class entityClass, Long id) {
        return checkRecord((BaseEntity) entityManagerWrapper.getEntityManager().find(entityClass, id),
                entityClass.getSimpleName(), id);
    }

    public Function<Class, PersistenceService> getPersistenceService() {
        return this::getPersistenceService;
    }

    public PersistenceService getPersistenceService(Class entityClass) {
        return (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
    }

    public static Class getEntityClass(String entityName) {
        return GenericHelper.getEntityClass(entityName);
    }
    
}
