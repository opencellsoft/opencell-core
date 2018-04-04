package org.meveo.api;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.module.ModulePropertyFlagLoader;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.IEntity;

/**
 * Base API service for CRUD operations on entity
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public abstract class BaseCrudApi<E extends IEntity, T extends BaseDto> extends BaseApi implements ApiService<E, T> {

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public T findIgnoreNotFound(String code) throws MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public T find(String code, ModulePropertyFlagLoader modulePropertyFlagLoader) throws MeveoApiException {
        throw new UnsupportedOperationException();
    }
}