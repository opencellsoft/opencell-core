package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.meveo.api.UserApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.UserRs;

/**
 * @author Mohamed Hamidi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UserRsImpl extends BaseRs implements UserRs {

    @Inject
    private UserApi userApi;

    @Override
    public ActionStatus create(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(@PathParam("username") String username) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.remove(username);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUserResponse find(@QueryParam("username") String username) {
        GetUserResponse result = new GetUserResponse();

        try {
            result.setUser(userApi.find(httpServletRequest, username));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    public UsersDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        UsersDto result = new UsersDto();

        try {
            result = userApi.list(httpServletRequest, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UsersDto listPost(PagingAndFiltering pagingAndFiltering) {

        UsersDto result = new UsersDto();

        try {
            result = userApi.list(httpServletRequest, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
    @Override
    public ActionStatus createExternalUser(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage(userApi.createExternalUser(httpServletRequest, postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateExternalUser(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.updateExternalUser(httpServletRequest, postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus deleteExternalUser(String username) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.deleteExternalUser(httpServletRequest, username);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
}
