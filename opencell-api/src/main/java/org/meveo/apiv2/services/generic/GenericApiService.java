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
import java.util.Map;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkRecord;

@Stateless
public abstract class GenericApiService {
    
    @Inject
    @MeveoJpa
    protected EntityManagerWrapper entityManagerWrapper;
    protected static Map<String, Class> entitiesByName;
    
    static {
        entitiesByName = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
    }
    
    public BaseEntity find(Class entityClass, Long id) {
        return checkRecord((BaseEntity) entityManagerWrapper.getEntityManager().find(entityClass, id),
                entityClass.getSimpleName(), id);
    }
    
    public PersistenceService getPersistenceService(Class entityClass) {
        return (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
    }
    
}
