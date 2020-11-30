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

package org.meveo.api.rest.generic.wf.impl;

import static org.meveo.api.MeveoApiErrorCodeEnum.CONDITION_FALSE;
import static org.meveo.api.dto.ActionStatusEnum.FAIL;
import static org.meveo.api.dto.ActionStatusEnum.SUCCESS;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowResponseDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowsResponseDto;
import org.meveo.api.dto.response.generic.wf.WorkflowInsHistoryResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.generic.wf.GenericWorkflowApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.generic.wf.GenericWorkflowRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class GenericWorkflowRsImpl extends BaseRs implements GenericWorkflowRs {

    @Inject
    private GenericWorkflowApi genericWorkflowApi;

    @Override
    public ActionStatus create(GenericWorkflowDto genericWorkflowDto) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            genericWorkflowApi.create(genericWorkflowDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(GenericWorkflowDto genericWorkflowDto) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            genericWorkflowApi.update(genericWorkflowDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String workflowCode) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            genericWorkflowApi.remove(workflowCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(GenericWorkflowDto genericWorkflowDto) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            genericWorkflowApi.createOrUpdate(genericWorkflowDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public GenericWorkflowResponseDto find(String workflowCode) {
        GenericWorkflowResponseDto result = new GenericWorkflowResponseDto();
        try {
            result.setGenericWorkflow(genericWorkflowApi.find(workflowCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public GenericWorkflowsResponseDto list() {
        GenericWorkflowsResponseDto result = new GenericWorkflowsResponseDto();
        try {
            result.setWorkflows(genericWorkflowApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus execute(String baseEntityName, String entityInstanceCode, String workflowCode) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            genericWorkflowApi.execute(baseEntityName, entityInstanceCode, workflowCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public GenericWorkflowsResponseDto findByEntity(String baseEntityName) {
        GenericWorkflowsResponseDto result = new GenericWorkflowsResponseDto();
        try {
            result.setWorkflows(genericWorkflowApi.findByEntity(baseEntityName));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public WorkflowInsHistoryResponseDto findHistory(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {
        WorkflowInsHistoryResponseDto result = new WorkflowInsHistoryResponseDto();
        try {
            result.setWorkflowInsHistories(genericWorkflowApi.findHistory(entityInstanceCode, workflowCode, fromStatus, toStatus));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            genericWorkflowApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            genericWorkflowApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus executeTransition(String baseEntityName, String entityInstanceCode, String workflowCode,
                                          String transitionUUID, boolean ignoreConditionEL) {
        ActionStatus response = new ActionStatus();
        try {
            response = genericWorkflowApi.executeTransition(baseEntityName, entityInstanceCode, workflowCode, transitionUUID, ignoreConditionEL);
        } catch (Exception exception) {
            response.setStatus(FAIL);
            if (exception instanceof MeveoApiException && ((MeveoApiException) exception).getErrorCode().equals(CONDITION_FALSE)) {
                processException(exception, response);
            }
            processException(new MeveoApiException(exception), response);
        }
        return response;
    }
}