package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.CustomEntityInstanceRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomEntityInstanceRsImpl extends BaseRs implements CustomEntityInstanceRs {

    @Inject
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Override
    public ActionStatus create(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.create(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.update(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String customEntityTemplateCode, String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customEntityInstanceApi.remove(customEntityTemplateCode, code);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityInstanceResponseDto find(String customEntityTemplateCode, String code) {
        CustomEntityInstanceResponseDto result = new CustomEntityInstanceResponseDto();

        try {

            result.setCustomEntityInstance(customEntityInstanceApi.find(customEntityTemplateCode, code));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CustomEntityInstancesResponseDto list(String customEntityTemplateCode) {
        CustomEntityInstancesResponseDto result = new CustomEntityInstancesResponseDto();

        try {

            result.setCustomEntityInstances(customEntityInstanceApi.list(customEntityTemplateCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.createOrUpdate(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}