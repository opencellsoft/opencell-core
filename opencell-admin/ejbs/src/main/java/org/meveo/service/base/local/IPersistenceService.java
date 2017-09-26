/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.base.local;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

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
    public E findById(Long id);

    /**
     * Find an entity by its id and fetch required fields.
     * 
     * @param id Id to find entity by.
     * @param fetchFields List of fields to fetch.
     * @return Entity found.
     */
    public E findById(Long id, List<String> fetchFields);

    /**
     * Find an entity by its id.
     * 
     * @param id Id to find entity by.
     * @param refresh Is entity refresh after load needed.
     * @return Entity found.
     */
    public E findById(Long id, boolean refresh);

    /**
     * Find an entity by its id and fetch required fields.
     * 
     * @param id Id to find entity by.
     * @param fetchFields List of fields to fetch.
     * @param refresh Is entity refresh after load needed.
     * @return Entity found.
     */
    public E findById(Long id, List<String> fetchFields, boolean refresh);

    /**
     * Persist an entity.
     * 
     * @param e Entity to persist.
     * 
     * @throws BusinessException
     */
    public void create(E e) throws BusinessException;

    /**
     * Update an entity.
     * 
     * @param e Entity to update.
     * 
     * @throws BusinessException
     */
    public E update(E e) throws BusinessException;

    /**
     * Disable an entity.
     * 
     * @param id Entity id which has to be disabled.
     * @return Updated entity
     * 
     * @throws BusinessException
     */
    public E disable(Long id) throws BusinessException;

    /**
     * Disable an entity.
     * 
     * @param e Entity to be disabled.
     * @return Updated entity
     * 
     * @throws BusinessException
     */
    public E disable(E e) throws BusinessException;

    /**
     * Enable an entity.
     * 
     * @param id Entity id which has to be enabled.
     * @return Updated entity
     * 
     * @throws BusinessException
     */
    public E enable(Long id) throws BusinessException;

    /**
     * Enable an entity.
     * 
     * @param e Entity to be enabled.
     * @return Updated entity
     * @throws BusinessException
     */
    public E enable(E e) throws BusinessException;

    /**
     * Delete an entity.
     * 
     * @param id Entity id which has to be deleted.
     * 
     * @throws BusinessException
     */
    public void remove(Long id) throws BusinessException;

    /**
     * Delete an entity.
     * 
     * @param e Entity to delete.
     * 
     * @throws BusinessException
     */
    public void remove(E e) throws BusinessException;

    /**
     * Delete list of entities by provided ids.
     * 
     * @param ids Entities ids to delete.
     * 
     * @throws BusinessException
     */
    public void remove(Set<Long> ids) throws BusinessException;

    /**
     * The entity class which the persistence is managed by the persistence service.
     * 
     * @return Entity class.
     */
    public Class<E> getEntityClass();

    /**
     * Load and return the complete list of the entities from database.
     * 
     * @return List of entities.
     */
    public List<E> list();// ? extends E

    /**
     * Load and return the complete list of active entities from database.
     * 
     * @return List of entities.
     */
    public List<E> listActive();

    /**
     * Load and return the list of the entities from database according to sorting and paging information in {@link PaginationConfiguration} object.
     * 
     * @return List of entities.
     */
    public List<E> list(PaginationConfiguration config); // ? extends E

    /**
     * Count number of entities in database.
     * 
     * @return Number of entities.
     */
    public long count();

    /**
     * Count number of filtered entities in database.
     * 
     * @return Number of filtered entities.
     */
    public long count(PaginationConfiguration config);

    /**
     * Detach an entity.
     * 
     * @param entity Entity which has to be detached.
     */
    public void detach(E entity);

    public E attach(E e);

    /**
     * Refresh entity with state from database.
     */
    public void refresh(E entity);

    /**
     * Refresh entity with state from database, or if it is not managed - retrieve it freshly from DB.
     * 
     * @param entity Entity to refresh
     */
    public E refreshOrRetrieve(E entity);

    /**
     * Refresh entity with state from database, or if it is not managed - retrieve it freshly from DB.
     * 
     * @param entities A list of entities to refresh
     */
    public List<E> refreshOrRetrieve(List<E> entities);

    public void commit();

    public EntityManager getEntityManager();

    public Set<E> refreshOrRetrieve(Set<E> entities);
}