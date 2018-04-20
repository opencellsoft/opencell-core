package org.meveo.api;

import java.util.Date;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BusinessDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;

/**
 * An interface of CRUD API service class for entities that are versioned - that contain validity dates.
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public interface ApiVersionedService<E extends BusinessEntity, T extends BusinessDto> {

    /**
     * Find entity identified by code.
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * 
     * @return A DTO of entity
     * @throws EntityDoesNotExistsException Entity was not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    public T find(String code, Date validFrom, Date validTo) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Find entity identified by code. Return null if not found
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * 
     * @return A DTO of entity or NULL if not found
     * @throws InvalidParameterException Some search parameter is incorrect
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws MeveoApiException Any other exception is wrapped to MeveoApiException
     */
    public T findIgnoreNotFound(String code, Date validFrom, Date validTo) throws MissingParameterException, InvalidParameterException, MeveoApiException;

    /**
     * Create or update an entity from DTO.
     * 
     * @param dtoData DTO data
     * @return Created entity.
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException Business exception
     */
    public E createOrUpdate(T dtoData) throws MeveoApiException, BusinessException;

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
     * Enable or disable entity identified by code and a validity period
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * @param enable Should entity be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, Date validFrom, Date validTo, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException;

    /**
     * Remove entity identified by code and a validity period
     * 
     * @param code Entity code
     * @param validFrom Entity validity range - from date
     * @param validTo Entity validity range - to date
     * @throws MissingParameterException A parameter, necessary to find an entity, was not provided
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws BusinessException A general business exception
     */
    void remove(String code, Date validFrom, Date validTo) throws MissingParameterException, EntityDoesNotExistsException, BusinessException;

}