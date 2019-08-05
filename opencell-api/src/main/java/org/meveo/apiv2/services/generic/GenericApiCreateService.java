package org.meveo.apiv2.services.generic;

import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.service.base.PersistenceService;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;

public class GenericApiCreateService extends GenericApiService {
    public Long create(String entityName, String dto) {
        checkEntityName(entityName).checkDto(dto);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);

        BaseEntity entityToCreate = (BaseEntity) JacksonUtil.fromString(dto, entityClass);
        PersistenceService service = getPersistenceService(entityClass);
        service.create(entityToCreate);
        return entityToCreate.getId();
    }
}
