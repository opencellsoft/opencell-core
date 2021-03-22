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

package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityTemplateApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.EntityCustomActionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.BusinessEntityResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.EntityCustomizationRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class EntityCustomizationRsImpl extends BaseRs implements EntityCustomizationRs {

    @Inject
    private CustomEntityTemplateApi customEntityTemplateApi;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    @Override
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.create(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.update(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeEntityTemplate(String customEntityTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.remove(customEntityTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityTemplateResponseDto findEntityTemplate(String customEntityTemplateCode) {
        CustomEntityTemplateResponseDto result = new CustomEntityTemplateResponseDto();

        try {
            result.setCustomEntityTemplate(customEntityTemplateApi.find(customEntityTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityTemplatesResponseDto listEntityTemplates(String code) {

        CustomEntityTemplatesResponseDto result = new CustomEntityTemplatesResponseDto();

        try {
            result.setCustomEntityTemplates(customEntityTemplateApi.listCustomEntityTemplates(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;

    }

    @Override
    public CustomEntityTemplatesResponseDto listEntityTemplates() {
        return listEntityTemplates(null);
    }

    @Override
    public ActionStatus customizeEntity(EntityCustomizationDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.customizeEntity(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;

    }

    @Override
    public EntityCustomizationResponseDto findEntityCustomizations(String customizedEntityClass) {

        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityTemplateApi.findEntityCustomizations(customizedEntityClass));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(dto, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(dto, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findField(String customFieldTemplateCode, String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.createOrUpdate(dto, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.create(dto, null);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.update(dto, null);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeAction(String actionCode, String appliesTo) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.remove(actionCode, appliesTo);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public EntityCustomActionResponseDto findAction(String actionCode, String appliesTo) {

        EntityCustomActionResponseDto result = new EntityCustomActionResponseDto();

        try {
            result.setEntityAction(entityCustomActionApi.find(actionCode, appliesTo));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateAction(EntityCustomActionDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.createOrUpdate(dto, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public BusinessEntityResponseDto listBusinessEntityForCFVByCode(String code, String wildcode) {
        BusinessEntityResponseDto result = new BusinessEntityResponseDto();

        try {
            result.setBusinessEntities(customEntityTemplateApi.listBusinessEntityForCFVByCode(code, wildcode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public EntityCustomizationResponseDto listELFiltered(String appliesTo, String entityCode, Long id) {
        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityTemplateApi.listELFiltered(appliesTo, entityCode, id));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus execute(String actionCode, String appliesTo, String entityCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage(entityCustomActionApi.execute(actionCode, appliesTo, entityCode));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Used for create or update template.
     * 
     * @see org.meveo.api.rest.custom.EntityCustomizationRs#createOrUpdateCustumizedEntityTemplate(org.meveo.api.dto.CustomEntityTemplateDto)
     */
    @Override
    public ActionStatus createOrUpdateCustumizedEntityTemplate(CustomEntityTemplateDto dto) {
        return this.createOrUpdateEntityTemplate(dto);
    }

    @Override
    public ActionStatus enableEntityTemplate(String code) {
        ActionStatus result = new ActionStatus();

        try {
            customEntityTemplateApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableEntityTemplate(String code) {
        ActionStatus result = new ActionStatus();

        try {
            customEntityTemplateApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus();

        try {
            customFieldTemplateApi.enableOrDisable(customFieldTemplateCode, appliesTo, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus();

        try {
            customFieldTemplateApi.enableOrDisable(customFieldTemplateCode, appliesTo, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableAction(String actionCode, String appliesTo) {
        ActionStatus result = new ActionStatus();

        try {
            entityCustomActionApi.enableOrDisable(actionCode, appliesTo, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableAction(String actionCode, String appliesTo) {
        ActionStatus result = new ActionStatus();

        try {
            entityCustomActionApi.enableOrDisable(actionCode, appliesTo, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findFieldV2(String customFieldTemplateCode, String appliesTo) {
        return findField(customFieldTemplateCode, appliesTo);
    }

    @Override
    public EntityCustomActionResponseDto findActionV2(String actionCode, String appliesTo) {
        return findActionV2(actionCode, appliesTo);
    }

    @Override
    public BusinessEntityResponseDto listBusinessEntityForCFVByCodeV2(String code, String wildcode) {
        return listBusinessEntityForCFVByCode(code, wildcode);
    }
}