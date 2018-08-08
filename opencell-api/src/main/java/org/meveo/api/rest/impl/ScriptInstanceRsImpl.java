package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.ScriptInstanceRs;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ScriptInstanceRsImpl extends BaseRs implements ScriptInstanceRs {

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Override
    public ScriptInstanceReponseDto create(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.createWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ScriptInstanceReponseDto update(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.updateWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String scriptInstanceCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            scriptInstanceApi.remove(scriptInstanceCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetScriptInstanceResponseDto find(String scriptInstanceCode) {
        GetScriptInstanceResponseDto result = new GetScriptInstanceResponseDto();
        try {
            result.setScriptInstance(scriptInstanceApi.find(scriptInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public Response execute(String scriptInstanceCode) {
        Response.ResponseBuilder responseBuilder = null;
        Map<String, Object> result = null;
        Map<String, Object> context = new HashMap<String, Object>();

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (!"scriptInstanceCode".equalsIgnoreCase(name)) {
                context.put(name, httpServletRequest.getParameter(name));
            }
        }

        try {
                result = scriptInstanceApi.execute(scriptInstanceCode, context);
                responseBuilder = Response.ok();
                responseBuilder.entity(result);
        } catch (MeveoApiException e) {
            log.error(e.getLocalizedMessage());
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
            responseBuilder.entity(e.getLocalizedMessage());
        } catch (BusinessException e) {
            log.error("Failed to execute a script {}", scriptInstanceCode , e);
        }
        Response response = responseBuilder.build();
        return response;
    }


    @Override
    public ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.createOrUpdateWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptInstanceApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptInstanceApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}