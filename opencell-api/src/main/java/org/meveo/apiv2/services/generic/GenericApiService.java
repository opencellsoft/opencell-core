package org.meveo.apiv2.services.generic;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IEntity;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.function.Function;
import static org.meveo.apiv2.ValidationUtils.checkRecord;

@Stateless
public abstract class GenericApiService {
    
    @Inject
    @MeveoJpa
    protected EntityManagerWrapper entityManagerWrapper;
    
    public IEntity find(Class entityClass, Long id) {
        return checkRecord((IEntity) entityManagerWrapper.getEntityManager().find(entityClass, id),
                entityClass.getSimpleName(), id);
    }

    public Function<Class, PersistenceService> getPersistenceService() {
        return this::getPersistenceService;
    }

    public PersistenceService getPersistenceService(Class entityClass) {
        PersistenceService serviceInterface = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
        if(serviceInterface == null){
            serviceInterface = (PersistenceService) EjbUtils.getServiceInterface("BaseEntityService");
            ((BaseEntityService) serviceInterface).setEntityClass(entityClass);
        }
        return serviceInterface;
    }

    public static Class getEntityClass(String entityName) {
        return GenericHelper.getEntityClass(entityName);
    }
    
}
