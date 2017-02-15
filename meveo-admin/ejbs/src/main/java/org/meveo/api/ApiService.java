package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.IEntity;

/**
 * An interface of CRUD API service class
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public interface ApiService<E extends IEntity, T extends BaseDto> {

    /**
     * Find entity identified by code
     * 
     * @param code Entity code

     * @return A DTO of entity
     * @throws MeveoApiException
     */
    public T find(String code) throws MeveoApiException;

    /**
     * Create or update an entity from DTO
     * 
     * @param dtoData DTO data

     * @throws MeveoApiException
     * @throws BusinessException
     */
    public E createOrUpdate(T dtoData) throws MeveoApiException, BusinessException;

}
