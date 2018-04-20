package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.jobs.TimerEntity;

/**
 * An interface of CRUD API service class
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public interface ApiService<E extends BusinessEntity, T extends BusinessDto> {

    /**
     * Find entity identified by code.
     * 
     * @param code Entity code
     * 
     * @return A DTO of entity
     * @throws EntityDoesNotExistsException Entity was not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    T find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException;

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
     * Enable or disable entity
     * 
     * @param code Entity code
     * @param enable Should entity be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws BusinessException A general business exception
     */
    void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException;

    /**
     * Remove entity
     * 
     * @param code Entity code
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws BusinessException A general business exception
     */
    void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException;

}