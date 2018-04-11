package org.meveo.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Date;

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
import org.meveo.service.base.IVersionedBusinessEntityService;
import org.meveo.service.base.PersistenceService;

/**
 * Base API service for CRUD operations on entity that is versioned - that is has validity dates
 * 
 * @author Andrius Karpavicius
 * 
 * @param <E> Entity class
 * @param <T> Dto class
 */
public abstract class BaseCrudVersionedApi<E extends IEntity, T extends BaseDto> extends BaseApi implements ApiVersionedService<E, T> {

    private PersistenceService<E> ps;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BaseCrudVersionedApi() {

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
     * @see org.meveo.api.ApiVersionedService#findIgnoreNotFound(java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public T findIgnoreNotFound(String code, Date validFrom, Date validTo) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code, validFrom, validTo);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    /**
     * @see org.meveo.api.ApiService#enableOrDisable(java.lang.String, java.util.Date, java.util.Date, boolean)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void enableOrDisable(String code, Date validFrom, Date validTo, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        BusinessEntity entity = ((IVersionedBusinessEntityService) ps).findByCode(code, validFrom, validTo);
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