package org.meveo.api.rest.wf.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowHistoryResponseDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.wf.WorkflowRs;
import org.meveo.api.wf.WorkflowApi;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class WorkflowRsImpl extends BaseRs implements WorkflowRs {

    @Inject
    private WorkflowApi workflowApi;

    @Override
    public ActionStatus create(WorkflowDto workflowDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            workflowApi.create(workflowDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(WorkflowDto workflowDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            workflowApi.update(workflowDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String workflowCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            workflowApi.remove(workflowCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(WorkflowDto workflowDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            workflowApi.createOrUpdate(workflowDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public WorkflowResponseDto find(String workflowCode) {
        WorkflowResponseDto result = new WorkflowResponseDto();
        try {
            result.setWorkflow(workflowApi.find(workflowCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public WorkflowsResponseDto list() {
        WorkflowsResponseDto result = new WorkflowsResponseDto();
        try {
            result.setWorkflows(workflowApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus execute(String baseEntityName, String entityInstanceCode, String workflowCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            workflowApi.execute(baseEntityName, entityInstanceCode, workflowCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public WorkflowsResponseDto findByEntity(String baseEntityName) {
        WorkflowsResponseDto result = new WorkflowsResponseDto();
        try {
            result.setWorkflows(workflowApi.findByEntity(baseEntityName));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public WorkflowHistoryResponseDto findHistory(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {
        WorkflowHistoryResponseDto result = new WorkflowHistoryResponseDto();
        try {
            result.setWorkflowHistories(workflowApi.findHistory(entityInstanceCode, workflowCode, fromStatus, toStatus));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }
}
