package org.meveo.api.rest.module.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.ModuleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.module.ModuleRs;
import org.meveo.model.module.MeveoModule;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ModuleRsImpl extends BaseRs implements ModuleRs {

    @Inject
    private ModuleApi moduleApi;

    @Override
    public ActionStatus create(ModuleDto moduleData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            moduleApi.create(moduleData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when create meveoModule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown exception when create meveoModule ", e);
        }

        return result;
    }

    @Override
    public ActionStatus update(ModuleDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            moduleApi.update(moduleDto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when update meveoModule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown error when update meveoModule ", e);
        }

        return result;
    }

    @Override
    public ActionStatus delete(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            moduleApi.delete(code, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when delete meveoModule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown exception when delete meveoModule ", e);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse list() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            result.setModules(moduleApi.list(getCurrentUser()));
        } catch (Exception e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("Error when list meveoModule ", e);
        }

        return result;
    }

    @Override
    public MeveoModuleDtoResponse get(String code) {
        MeveoModuleDtoResponse result = new MeveoModuleDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            result.setModule(moduleApi.get(code, MeveoModule.class, getCurrentUser()));
        } catch (Exception e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("Error when get a meveoModule by code {}", code, e);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ModuleDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(moduleDto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }
}
