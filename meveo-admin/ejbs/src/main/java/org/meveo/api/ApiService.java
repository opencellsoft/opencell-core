package org.meveo.api;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;

/**
 * An interface of CRUD API service class
 * 
 * @author Andrius Karpavicius
 * 
 * @param <T> Dto class
 */
public interface ApiService<T extends BaseDto> {

    /**
     * Find entity identified by code
     * 
     * @param code Entity code
     * @param currentUser Current user
     * @return A DTO of entity
     * @throws MeveoApiException
     */
    public T find(String code, User currentUser) throws MeveoApiException;

    /**
     * Create or update an entity from DTO
     * 
     * @param dtoData DTO data
     * @param currentUser Current user
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdate(T dtoData, User currentUser) throws MeveoApiException, BusinessException;

}
