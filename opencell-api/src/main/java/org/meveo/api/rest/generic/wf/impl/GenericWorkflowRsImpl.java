package org.meveo.api.rest.generic.wf.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowResponseDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowsResponseDto;
import org.meveo.api.dto.response.generic.wf.WorkflowInsHistoryResponseDto;
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
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            genericWorkflowApi.create(genericWorkflowDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(GenericWorkflowDto genericWorkflowDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            genericWorkflowApi.update(genericWorkflowDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String workflowCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            genericWorkflowApi.remove(workflowCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(GenericWorkflowDto genericWorkflowDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
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
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
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
}