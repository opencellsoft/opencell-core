package org.meveo.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

/**
 * Base API service for CRUD operations on entity
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public abstract class BaseCrudApi<E extends IEntity, T extends BaseDto> extends BaseApi implements ApiService<E, T> {

    private PersistenceService<E> ps;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BaseCrudApi() {

        Class<E> entityClass;
        Class clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

        if (o instanceof TypeVariable) {
            entityClass = (Class<E>) ((TypeVariable) o).getBounds()[0];
        } else {
            entityClass = (Class<E>) o;
        }

        try {
            ps = getPersistenceService(entityClass, true);
        } catch (BusinessException e) {
            log.error("Failed to obtain a persistenceService for {}", getClass());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public T findIgnoreNotFound(String code) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    /**
     * @see org.meveo.api.ApiService#enableOrDisable(java.lang.String, boolean)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        BusinessEntity entity = ((BusinessService) ps).findByCode(code);
        if (entity == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
        if (enable) {
            ps.enable((E) entity);
        } else {
            ps.disable((E) entity);
        }
    }
}