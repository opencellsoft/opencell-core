package org.meveo.api.wf;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.payment.WorkflowsDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.wf.Workflow;
import org.meveo.service.wf.WorkflowService;



@Stateless
public class WorkflowApi extends BaseApi {

	@Inject
	private WorkflowService workflowService;

	@Inject
	private WFTransitionApi wfTransitionApi;
	
	/**
	 * 
	 * @param workflowDto
	 * @param currentUser
	 * @throws EntityAlreadyExistsException
	 * @throws BusinessException
	 * @throws MissingParameterException 
	 * @throws EntityDoesNotExistsException 
	 */
	public void create(WorkflowDto workflowDto, User currentUser) throws EntityAlreadyExistsException, BusinessException, MissingParameterException, EntityDoesNotExistsException  {
		validateDto(workflowDto);
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider());
		if(workflow != null) {
			throw new EntityAlreadyExistsException(Workflow.class, workflowDto.getCode());
		}		
		workflow = workflowDto.fromDto(workflow);
		workflowService.create(workflow, currentUser);		
		if(workflowDto.getListWFTransitionDto() != null &&  !workflowDto.getListWFTransitionDto().isEmpty()){
		    for(WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()){
		    	wfTransitionDto.setWorkflowCode(workflowDto.getCode());
		    	wfTransitionApi.create(wfTransitionDto, currentUser);
		    }
		}		
	}

	/**
	 * 
	 * @param workflowDto
	 * @param currentUser
	 * @throws EntityDoesNotExistsException 
	 * @throws MeveoApiException
	 * @throws BusinessException
	 * @throws MissingParameterException 
	 * @throws EntityAlreadyExistsException 
	 */
	public void update(WorkflowDto workflowDto, User currentUser) throws EntityDoesNotExistsException, BusinessException, MissingParameterException, EntityAlreadyExistsException {
		validateDto(workflowDto);
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider());
		if (workflow == null) {
			throw new EntityDoesNotExistsException(Workflow.class, workflowDto.getCode());
		} 

		workflow = workflowDto.fromDto(workflow);
		workflowService.update(workflow, currentUser);		
		if(workflowDto.getListWFTransitionDto() != null &&  !workflowDto.getListWFTransitionDto().isEmpty()){
		    for(WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()){
		    	wfTransitionDto.setWorkflowCode(workflowDto.getCode());
		    	wfTransitionApi.createOrUpdate(wfTransitionDto, currentUser);
		    }
		}
	}

	/**
	 * 
	 * @param workflowCode
	 * @param currentUser
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
	public WorkflowDto find(String workflowCode, User currentUser) throws MissingParameterException, EntityDoesNotExistsException{
		return new WorkflowDto(find(workflowCode, currentUser.getProvider()));
	}

	/**
	 * 
	 * @param workflowCode
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
	public void remove(String workflowCode, User currentUser) throws MissingParameterException, EntityDoesNotExistsException {
		workflowService.remove(find(workflowCode, currentUser.getProvider())); 
	}

	/**
	 * 
	 * @param currentUser
	 * @return
     *
	 */
	public WorkflowsDto list(User currentUser){
		WorkflowsDto workflowsDto=new WorkflowsDto();
		List<Workflow> workflows =  workflowService.list(currentUser.getProvider());
		if(workflows != null){
			for(Workflow workflow : workflows){
				workflowsDto.getListWorkflowDto().add(new WorkflowDto(workflow));
			}
		}
		return workflowsDto;
	}

	/**
	 * 
	 * @param workflowDto
	 * @param currentUser
	 * @throws EntityAlreadyExistsException
	 * @throws BusinessException
	 * @throws EntityDoesNotExistsException
	 * @throws MissingParameterException 
	 */
	public void createOrUpdate(WorkflowDto workflowDto, User currentUser) throws EntityAlreadyExistsException, BusinessException, EntityDoesNotExistsException, MissingParameterException{
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider());
		if(workflow == null){
			create(workflowDto,currentUser);
		}else{
			update(workflowDto,currentUser);
		}
	}
	
	/**
	 * 
	 * @param workflowDto
	 * @throws MissingParameterException
	 */
	public void validateDto(WorkflowDto workflowDto) throws MissingParameterException{
		if (StringUtils.isBlank(workflowDto.getCode())) {
			missingParameters.add("code");
		}
		if (workflowDto.getStatus() == null) {
			missingParameters.add("status");
		} 
		if (StringUtils.isBlank(workflowDto.getWfType())) {
			missingParameters.add("WFType");
		} 		
		handleMissingParameters();
	}
	
	private Workflow find(String workflowCode,Provider provider) throws MissingParameterException, EntityDoesNotExistsException{
		if(StringUtils.isBlank(workflowCode)){
			missingParameters.add("workflowCode");
			handleMissingParameters();
		}
		Workflow workflow = workflowService.findByCode(workflowCode, provider);
		if(workflow == null){
			throw new EntityDoesNotExistsException(Workflow.class, workflowCode);
		}
		return workflow;
	}
}


