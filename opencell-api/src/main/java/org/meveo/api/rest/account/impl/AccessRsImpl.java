package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.AccessApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.AccessRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccessRsImpl extends BaseRs implements AccessRs {

    @Inject
    private AccessApi accessApi;

    @Override
    public ActionStatus create(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetAccessResponseDto find(String accessCode, String subscriptionCode) {
        GetAccessResponseDto result = new GetAccessResponseDto();

        try {
            result.setAccess(accessApi.find(accessCode, subscriptionCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String accessCode, String subscriptionCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.remove(accessCode, subscriptionCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccessesResponseDto listBySubscription(String subscriptionCode) {
        AccessesResponseDto result = new AccessesResponseDto();

        try {
            result.setAccesses(accessApi.listBySubscription(subscriptionCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String accessCode, String subscriptionCode) {

        ActionStatus result = new ActionStatus();

        try {
            accessApi.enableOrDisable(accessCode, subscriptionCode, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String accessCode, String subscriptionCode) {

        ActionStatus result = new ActionStatus();

        try {
            accessApi.enableOrDisable(accessCode, subscriptionCode, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
