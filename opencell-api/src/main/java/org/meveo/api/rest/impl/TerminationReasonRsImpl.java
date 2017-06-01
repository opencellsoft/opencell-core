package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.TerminationReasonApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.GetTerminationReasonResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.TerminationReasonRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TerminationReasonRsImpl extends BaseRs implements TerminationReasonRs {

    @Inject
    private TerminationReasonApi terminationReasonApi;

    @Override
    public ActionStatus create(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTerminationReasonResponse find(String code) {
        GetTerminationReasonResponse result = new GetTerminationReasonResponse();

        try {
            result.getTerminationReason().add(terminationReasonApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetTerminationReasonResponse list() {
        GetTerminationReasonResponse result = new GetTerminationReasonResponse();

        try {
            result.getTerminationReason().addAll(terminationReasonApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
