/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.base.local;

import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;

/**
 * Generic interface that defines the methods to implement for every persistence service.
 * 
 * @param <E> Class that inherits from {@link BaseEntity}
 */
public interface IPersistenceService<E extends IEntity> {

    /**
     * Find an entity by its id.
     * 
     * @param id Id to find entity by.
     * @return Entity found.
     */
    E findById(Long id);

    /**
     * Find entities by its id.
     * 
     * @param ids A list of Ids to find entity by.
     * @return A list of entities
     */
    List<E> findByIds(List<Long> ids);

    /**
     * Find entities by its id.
     * 
     * @param ids A list of Ids to find entity by.
     * @param fetchFields List of fields to fetch.
     * @return A list of entities.
     */
    List<E> findByIds(List<Long> ids, List<String> fetchFields);

    /**
     * Find an entity by its id and fetch required fields.
     * 
     * @param id Id to find entity by.
     * @param fetchFields List of fields to fetch.
     * @return Entity found.
     */
    E findById(Long id, List<String> fetchFields);

    /**
     * Find an entity by its id.
     * 
     * @param id Id to find entity by.
     * @param refresh Is entity refresh after load needed.
     * @return Entity found.
     */
    E findById(Long id, boolean refresh);

    /**
     * Find an entity by its id and fetch required fields.
     * 
     * @param id Id to find entity by.
     * @param fetchFields List of fields to fetch.
     * @param refresh Is entity refresh after load needed.
     * @return Entity found.
     */
    E findById(Long id, List<String> fetchFields, boolean refresh);

    /**
     * Persist an entity.
     * 
     * @param e Entity to persist.
     * 
     * @throws BusinessException business exception.
     */
    void create(E e) throws BusinessException;

    /**
     * Update an entity.
     * 
     * @param e Entity to update.
     * @return entity
     * @throws BusinessException business exception.
     */
    E update(E e) throws BusinessException;

    /**
     * Disable an entity.
     * 
     * @param id Entity id which has to be disabled.
     * @return Updated entity
     * 
     * @throws BusinessException business exception.
     */
    E disable(Long id) throws BusinessException;

    /**
     * Disable an entity.
     * 
     * @param e Entity to be disabled.
     * @return Updated entity
     * 
     * @throws BusinessException business exception.
     */
    E disable(E e) throws BusinessException;

    /**
     * Enable an entity.
     * 
     * @param id Entity id which has to be enabled.
     * @return Updated entity
     * 
     * @throws BusinessException business exception.
     */
    E enable(Long id) throws BusinessException;

    /**
     * Enable an entity.
     * 
     * @param e Entity to be enabled.
     * @return Updated entity
     * @throws BusinessException business exception.
     */
    public E enable(E e) throws BusinessException;

    /**
     * Delete an entity.
     * 
     * @param id Entity id which has to be deleted.
     * 
     * @throws BusinessException business exception.
     */
    void remove(Long id) throws BusinessException;

    /**
     * Delete an entity.
     * 
     * @param e Entity to delete.
     * 
     * @throws BusinessException business exception.
     */
    void remove(E e) throws BusinessException;

    /**
     * Delete list of entities by provided ids.
     * 
     * @param ids Entities ids to delete.
     * 
     * @throws BusinessException business exception.
     */
    void remove(Set<Long> ids) throws BusinessException;

    /**
     * The entity class which the persistence is managed by the persistence service.
     * 
     * @return Entity class.
     */
    Class<E> getEntityClass();

    /**
     * Load and return the complete list of the entities from database.
     * 
     * @return List of entities.
     */
    List<E> list();// ? extends E

    /**
     * Load and return the complete list of active entities from database.
     * 
     * @return List of entities.
     */
    List<E> listActive();

    /**
     * Load and return the list of the entities from database according to sorting and paging information in {@link PaginationConfiguration} object.
     * 
     * @param config Data filtering, sorting and pagination criteria
     * @return List of entities.
     */
    List<E> list(PaginationConfiguration config); // ? extends E

    /**
     * Count number of entities in database.
     * 
     * @return Number of entities.
     */
    long count();

    /**
     * Count number of filtered entities in database.
     * 
     * @param config Data filtering, sorting and pagination criteria
     * @return Number of filtered entities.
     */
    long count(PaginationConfiguration config);

    /**
     * Detach an entity.
     * 
     * @param entity Entity which has to be detached.
     */
    void detach(E entity);

    /**
     * Refresh entity with state from database.
     * 
     * @param entity entity to refresh.
     */
    void refresh(IEntity entity);

    /**
     * Refresh entity with state from database, or if it is not managed - retrieve it freshly from DB.
     * 
     * @param entity Entity to refresh/retrieve
     * @return Refreshed/retrieved entity.
     */
    E refreshOrRetrieve(E entity);

    /**
     * Refresh entity with state from database, or if it is not managed - retrieve it freshly from DB.
     * 
     * @param entities A list of entities to refresh/retrieve
     * @return A list of refreshed/retrieved entities.
     */
    List<E> refreshOrRetrieve(List<E> entities);

    /**
     * Refresh entity with state from database, or if it is not managed - retrieve it freshly from DB.
     * 
     * @param entities A set of entities to refresh/retrieve
     * @return A set of refreshed/retrieved entities
     */
    Set<E> refreshOrRetrieve(Set<E> entities);

    /**
     * If entity is not managed - retrieve it freshly from DB. If entity is managed - return as is.
     * 
     * @param entity Entity to retrieve
     * @return Retrieved entity.
     */
    E retrieveIfNotManaged(E entity);

    /**
     * If entity is not managed - retrieve it freshly from DB. If entity is managed - return as is.
     * 
     * @param entities A list of entities to refresh/retrieve
     * @return List of retrieved entities.
     */
    List<E> retrieveIfNotManaged(List<E> entities);

    /**
     * If entity is not managed - retrieve it freshly from DB. If entity is managed - return as is.
     * 
     * @param entities A set of entities to refresh/retrieve
     * @return Set of retrieved entities
     */
    Set<E> retrieveIfNotManaged(Set<E> entities);

    /**
     * Flush data to DB. NOTE: unlike the name suggest, no transaction commit is done
     */
    public default void commit() {
        getEntityManager().flush();
    }

    /**
     * Return an entity manager for a current provider
     * 
     * @return Entity manager
     */
    EntityManager getEntityManager();

    String findReferencedByEntities(Class<E> clazz, Long id);

    /**
     * Removes an entity with a given class and id.
     * 
     * @param parentClass Class of the entity
     * @param parentId id of the entity
     */
    void remove(@SuppressWarnings("rawtypes") Class parentClass, Object parentId);
}