/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
@Deprecated
public class ScriptInstanceWsImpl extends BaseWs implements ScriptInstanceWs {

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

    @Override
    public ActionStatus execute(String code) {

        ActionStatus result = new ActionStatus();

        try {
            scriptInstanceApi.execute(code, null);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }
}