package org.meveo.api;

import org.meveo.api.dto.BaseDto;
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

}
