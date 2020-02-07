package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BaseEntity;

/**
 * An interface of CRUD API service class
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public interface ApiService<E extends BaseEntity, T extends BaseEntityDto> {

    /**
     * Find entity identified by code
     * 
     * @param code Entity code
     * 
     * @return A DTO of entity
     * @throws EntityDoesNotExistsException Entity was not found
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    T find(String code) throws MeveoApiException;

    /**
     * Find entity identified by ID
     * 
     * @param id Entity id
     * 
     * @return A DTO of entity
     * @throws EntityDoesNotExistsException Entity was not found
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    T find(Long id) throws MeveoApiException;

    /**
     * Find entity identified by code. Return null if not found
     * 
     * @param code Entity code
     * 
     * @return A DTO of entity or NULL if not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    T findIgnoreNotFound(String code) throws MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Find entity identified by ID. Return null if not found
     * 
     * @param id Entity ID
     * 
     * @return A DTO of entity or NULL if not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    T findIgnoreNotFound(Long id) throws MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Create or update an entity from DTO.
     * 
     * @param dtoData DTO data
     * @return Created or updated entity.
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    E createOrUpdate(T dtoData) throws MeveoApiException, BusinessException;

    /**
     * Create an entity from DTO.
     * 
     * @param dtoData DTO data
     * @return Created entity
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    E create(T dtoData) throws MeveoApiException, BusinessException;

    /**
     * Update an entity from DTO.
     * 
     * @param dtoData DTO data
     * @return Updated entity
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    E update(T dtoData) throws MeveoApiException, BusinessException;

    /**
     * Enable or disable entity by code
     * 
     * @param code Entity code
     * @param enable Should entity be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws BusinessException A general business exception
     */
    void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException;

    /**
     * Enable or disable entity by ID
     * 
     * @param id Entity ID
     * @param enable Should entity be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws BusinessException A general business exception
     */
    void enableOrDisable(Long id, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException;

    /**
     * Remove entity by code
     * 
     * @param code Entity code
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws BusinessException A general business exception
     */
    void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException;

    /**
     * Remove entity by ID
     * 
     * @param id Entity ID
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws BusinessException A general business exception
     */
    void remove(Long id) throws MissingParameterException, EntityDoesNotExistsException, BusinessException;

}