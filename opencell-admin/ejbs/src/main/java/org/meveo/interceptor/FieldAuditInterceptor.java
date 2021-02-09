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

package org.meveo.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.service.audit.AuditableFieldConfiguration;
import org.meveo.service.audit.AuditableFieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interceptor allows to intercept persistent objects, inspect and/or manipulate their properties before it is saved, updated, deleted or loaded.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */

public class FieldAuditInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -1175387060899608632L;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private AuditableFieldConfiguration auditableFieldConfiguration = (AuditableFieldConfiguration) EjbUtils.getServiceInterface("AuditableFieldConfiguration");

    private AuditableFieldService auditableFieldService = (AuditableFieldService) EjbUtils.getServiceInterface("AuditableFieldService");

    private Map<String, List<Field>> auditableEntities = new HashMap();


    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        //Called when an entity is detected to be dirty, during a flush.
        // log.debug("Flush Dirty Entity {}", entity.getClass().getName());

        //get only all the entities that extends the AuditableEntity interface and that contain fields that are marked by the AuditTarget annotation
        if (auditableEntities.isEmpty()) {
            auditableEntities.putAll(auditableFieldConfiguration.getAuditableEntities());
        }

        String calssName = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        if (!AuditableEntity.class.isAssignableFrom(entity.getClass()) || !auditableEntities.containsKey(calssName)) {
            return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        }

        //loop over the auditable fields of entity that are marked by the AuditTarget annotation
        List<Field> fields = auditableEntities.get(entity.getClass().getName());

        //store the dirty fields to process them before the end of the transaction
        auditableFieldService.setChangedFields((BaseEntity) entity, fields, currentState, previousState, propertyNames);

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        //Called before a transaction is committed (but not before rollback).
        try {
            auditableFieldService.registerChangedFields();
        } catch (BusinessException e) {
            log.error("exception in interceptor beforeTransactionCompletion()", e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        //Called after a transaction is committed or rolled back.
        auditableFieldService.resetChangedEntities();
    }
    
}