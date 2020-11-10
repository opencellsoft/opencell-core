package org.meveo.apiv2.generic.services;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.security.interceptor.SecuredBusinessEntityCheckInterceptor;
import org.meveo.apiv2.generic.security.interceptor.UserPermissionCheckInterceptor;
import org.meveo.model.IEntity;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;


/**
 * Class delegate for core persistence services. It's useful if we need to insert
 * additional logic between API service and Core persistence service like using
 * secured entities interceptor
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
@Stateless
@Interceptors({UserPermissionCheckInterceptor.class, SecuredBusinessEntityCheckInterceptor.class})
public class GenericApiPersistenceDelegate {

    /**
     * Search and list entities
     *
     * @param entityClass entity class to list
     * @param searchConfig search params
     * @return list of entities wrapped in {@link SearchResult} object
     */
    public SearchResult list(Class entityClass, PaginationConfiguration searchConfig) {
        List entityList = getPersistenceService(entityClass).list(searchConfig);
        long count = this.count(entityClass, searchConfig);

        SearchResult searchResult = new SearchResult(entityList, count);
        return searchResult;
    }

    /**
     * Get total count of entities
     *
     * @param entityClass entity class to count
     * @param searchConfig search params
     * @return total count of searched entity
     */
    public long count(Class<?> entityClass, PaginationConfiguration searchConfig) {
        return getPersistenceService(entityClass).count(searchConfig);
    }

    /**
     * Find an entity by its id
     *
     * @param entityClass entity class
     * @param id entity id
     * @param fetchFields list of fields to fetch
     * @return entity of type {@link IEntity}
     */
    public IEntity find(Class<?> entityClass, Long id, List<String> fetchFields) {
        return getPersistenceService(entityClass).findById(id, fetchFields);
    }

    /**
     * Find an entity by its id
     *
     * @param entityClass Entity class
     * @param id Entity id
     * @return Entity of type {@link IEntity}
     */
    public IEntity find(Class<?> entityClass, Long id) {
        return getPersistenceService(entityClass).findById(id);
    }

    /**
     *  Create a new entity
     *
     * @param entityClass Entity class to create
     * @param iEntity The entity to create
     */
    public void create(Class entityClass, IEntity iEntity) {
        getPersistenceService(entityClass).create(iEntity);
    }

    /**
     *  Update an existing entity
     *
     * @param entityClass Entity class to update
     * @param iEntity The entity to update
     * @return The updated entity
     */
    public IEntity update(Class entityClass, IEntity iEntity) {
        return getPersistenceService(entityClass).update(iEntity);
    }

    /**
     * Remove an existing entity
     *
     * @param entityClass Entity class to remove
     * @param iEntity The entity to remove
     */
    public void remove(Class entityClass, IEntity iEntity) {
        getPersistenceService(entityClass).remove(iEntity);
    }
}
