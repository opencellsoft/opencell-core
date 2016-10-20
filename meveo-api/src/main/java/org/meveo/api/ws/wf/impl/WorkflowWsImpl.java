package org.meveo.api.ws.wf.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowHistoryResponseDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.wf.WorkflowApi;
import org.meveo.api.ws.impl.BaseWs;
import org.meveo.api.ws.wf.WorkflowWs;

@WebService(serviceName = "WorkflowWs", endpointInterface = "org.meveo.api.ws.wf.WorkflowWs")
@Interceptors({ WsRestApiInterceptor.class })
public class WorkflowWsImpl extends BaseWs implements WorkflowWs {

	 @Inject
	 private WorkflowApi workflowApi;
	 
	    @Override
	    public ActionStatus create(WorkflowDto workflowDto) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	workflowApi.create(workflowDto, getCurrentUser());
	        } catch (Exception e) {
	        	super.processException(e, result);
	        }
	        return result;
	    }

	    @Override
	    public ActionStatus update(WorkflowDto workflowDto) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	workflowApi.update(workflowDto, getCurrentUser());
	        } catch (Exception e) {
	        	super.processException(e, result);
	        }

	        return result;
	    }

	    @Override
	    public ActionStatus remove(String workflowCode) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	workflowApi.remove(workflowCode, getCurrentUser());
	        } catch (Exception e) {
	        	super.processException(e, result);
	        }
	        return result;
	    }

		@Override
		public ActionStatus createOrUpdate(WorkflowDto workflowDto) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
			try {
				workflowApi.createOrUpdate(workflowDto, getCurrentUser());
			} catch (Exception e) {
	        	super.processException(e, result);
	        }			
			return result;
		}

		@Override
		public WorkflowResponseDto find(String workflowCode) {
			WorkflowResponseDto workflowResponseDto = new WorkflowResponseDto();
			try {
            workflowResponseDto.setWorkflow(workflowApi.find(workflowCode, getCurrentUser()));
			} catch (Exception e) {
	        	super.processException(e, workflowResponseDto.getActionStatus());
	        }				
			return workflowResponseDto;
		}
		
		@Override
		public WorkflowsResponseDto list() {
			WorkflowsResponseDto workflowsResponseDto = new WorkflowsResponseDto();
			try {
            workflowsResponseDto.setWorkflows(workflowApi.list(getCurrentUser()));
			} catch (Exception e) {
	        	super.processException(e, workflowsResponseDto.getActionStatus());
	        }
			
			return workflowsResponseDto;
		}
		
		@Override
		public ActionStatus execute(String baseEntityName, String entityInstanceCode, String workflowCode) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
			try {
				workflowApi.execute( baseEntityName,  entityInstanceCode,  workflowCode,getCurrentUser() );
			} catch (Exception e) {
	        	super.processException(e, result);
	        }			
			return result;
		}

		@Override
		public WorkflowsResponseDto findByEntity(String baseEntityName) {
			WorkflowsResponseDto workflowsResponseDto = new WorkflowsResponseDto();
			try {
            workflowsResponseDto.setWorkflows(workflowApi.findByEntity(baseEntityName, getCurrentUser()));
			} catch (Exception e) {
	        	super.processException(e, workflowsResponseDto.getActionStatus());
	        }			
			return workflowsResponseDto;
		}

		@Override
		public WorkflowHistoryResponseDto findHistory( String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {
			WorkflowHistoryResponseDto workflowHistoryResponseDto = new WorkflowHistoryResponseDto();
			try {
				workflowHistoryResponseDto.setWorkflowHistories(workflowApi.findHistory(entityInstanceCode,  workflowCode,  fromStatus,  toStatus,getCurrentUser()));
			} catch (Exception e) {
	        	super.processException(e, workflowHistoryResponseDto.getActionStatus());
	        }			
			return workflowHistoryResponseDto;
		}
}