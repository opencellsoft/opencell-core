package org.meveo.api.rest.hierarchy.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.hierarchy.UserHierarchyLevelApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.hierarchy.UserHierarchyLevelRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Phu Bach
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UserHierarchyLevelRsImpl extends BaseRs implements UserHierarchyLevelRs {

    @Inject
    private UserHierarchyLevelApi userHierarchyLevelApi;

    @Override
    public ActionStatus create(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.create(postData, getCurrentUser());
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

    @Override
    public ActionStatus update(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.update(postData, getCurrentUser());
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

    @Override
    public UserHierarchyLevelResponseDto find(String hierarchyLevelCode) {
        UserHierarchyLevelResponseDto result = new UserHierarchyLevelResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setUserHierarchyLevel(userHierarchyLevelApi.find(hierarchyLevelCode, getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String hierarchyLevelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.remove(hierarchyLevelCode, getCurrentUser());
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
