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

package org.meveo.admin.action;

import jakarta.inject.Inject;

import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

/**
 * Backing bean for support custom field instances value data entry
 * 
 * @param <T> the type of the value
 */
public abstract class CustomFieldBean<T extends IEntity> extends BaseBean<T> {

    private static final long serialVersionUID = 1L;
    //
    // private CustomFieldTemplate customFieldSelectedTemplate;
    //
    // private CustomFieldInstance customFieldSelectedPeriod;
    //
    // private String customFieldSelectedPeriodId;
    //
    // private boolean customFieldPeriodMatched;


    @Inject
    protected CustomFieldDataEntryBean customFieldDataEntryBean;
    
    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    public CustomFieldBean() {
    }

    public CustomFieldBean(Class<T> clazz) {
        super(clazz);
    }

    // @Override
    // public T initEntity() {
    // T result = super.initEntity();
    // return result;
    // }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        boolean isNew = entity.isTransient();
        if (entity instanceof ICustomFieldEntity) {
            customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) entity, isNew);
        }
        String outcome = super.saveOrUpdate(killConversation);
        
        return outcome;
    }    
}