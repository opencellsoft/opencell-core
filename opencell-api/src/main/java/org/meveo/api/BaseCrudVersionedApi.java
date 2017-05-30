package org.meveo.api;

import java.util.Date;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.IEntity;

/**
 * Base API service for CRUD operations on entity that is versioned - that is has validity dates
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public abstract class BaseCrudVersionedApi<E extends IEntity, T extends BaseDto> extends BaseApi implements ApiVersionedService<E, T> {

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiVersionedService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public T findIgnoreNotFound(String code, Date validFrom, Date validTo) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code, validFrom, validTo);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }
}
