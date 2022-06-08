/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.IVersionedDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.shared.DateUtils;
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
public abstract class BaseCrudVersionedApi<E extends BusinessEntity, T extends BusinessEntityDto> extends BaseApi implements ApiVersionedService<E, T> {

    /**
     * Persistence service corresponding to a entity that API implementation corresponds to
     */
    private IVersionedBusinessEntityService<E> ps;

    /**
     * Entity class that API implementation corresponds to
     */
    private Class<E> entityClass;

    /**
     * Constructor
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BaseCrudVersionedApi() {

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
            ps = (IVersionedBusinessEntityService<E>) getPersistenceService(entityClass, true);
        } catch (BusinessException e) {
            log.error("Failed to obtain a persistenceService for {}", getClass());
        }
    }

    @Override
    public T findIgnoreNotFound(String code, Date validFrom, Date validTo) throws MissingParameterException, InvalidParameterException, MeveoApiException {
        try {
            return find(code, validFrom, validTo);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    public E createOrUpdate(T dtoData) throws MeveoApiException, BusinessException {
        E entity = ps.findByCode(dtoData.getCode(), ((IVersionedDto) dtoData).getValidFrom(), ((IVersionedDto) dtoData).getValidTo());

        if (entity == null) {
            return create(dtoData);
        } else {
            return update(dtoData);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void enableOrDisable(String code, Date validFrom, Date validTo, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        BusinessEntity entity = ps.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (entity == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(entityClass,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }
        if (enable) {
            ((PersistenceService) ps).enable((E) entity);
        } else {
            ((PersistenceService) ps).disable((E) entity);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void remove(String code, Date validFrom, Date validTo) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        E entity = ps.findByCodeBestValidityMatch(code, validFrom, validTo);

        if (entity == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(entityClass,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        ((PersistenceService) ps).remove(entity);
    }
}