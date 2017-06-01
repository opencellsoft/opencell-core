package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.ScriptInstanceWs;

@WebService(serviceName = "ScriptInstanceWs", endpointInterface = "org.meveo.api.ws.ScriptInstanceWs")
@Interceptors({ WsRestApiInterceptor.class })
public class ScriptInstanceWsImpl extends BaseWs implements ScriptInstanceWs {

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Override
    public ScriptInstanceReponseDto create(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.create(postData));
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
            result.setCompilationErrors(scriptInstanceApi.update(postData));
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
            scriptInstanceApi.removeScriptInstance(scriptInstanceCode);

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

}
