package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.rest.catalog.BusinessServiceModelRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.catalog.BusinessServiceModel;

/**
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 * @lastModifiedVersion 5.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BusinessServiceModelRsImpl extends BaseRs implements BusinessServiceModelRs {

    @Inject
    private MeveoModuleApi moduleApi;

    @Override
    public ActionStatus create(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessServiceModelResponseDto find(String businessServiceModelCode) {
        GetBusinessServiceModelResponseDto result = new GetBusinessServiceModelResponseDto();

        try {
            result.setBusinessServiceModel((BusinessServiceModelDto) moduleApi.find(businessServiceModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String businessServiceModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.delete(businessServiceModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse list() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessServiceModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus install(BusinessServiceModelDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(moduleDto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}