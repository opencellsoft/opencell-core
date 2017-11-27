package org.meveo.api.rest.hierarchy.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
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
            userHierarchyLevelApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public UserHierarchyLevelResponseDto find(String hierarchyLevelCode) {
        UserHierarchyLevelResponseDto result = new UserHierarchyLevelResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setUserHierarchyLevel(userHierarchyLevelApi.find(hierarchyLevelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String hierarchyLevelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.remove(hierarchyLevelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            userHierarchyLevelApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public UserHierarchyLevelsDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();

        try {
            result = userHierarchyLevelApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UserHierarchyLevelsDto listPost(PagingAndFiltering pagingAndFiltering) {

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();

        try {
            result = userHierarchyLevelApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}