package org.meveo.api.wf;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.wf.WorkflowService;
import org.meveo.service.wf.WFTransitionService;

@Stateless
public class WorkflowApi extends BaseApi {

	@Inject
	private WorkflowService workflowService;

	@Inject
	private WFTransitionApi wfTransitionApi;

    @Inject
    private WFTransitionService wfTransitionService;
	
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
		validateDto(workflowDto, false);
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider());
		if(workflow != null) {
			throw new EntityAlreadyExistsException(Workflow.class, workflowDto.getCode());
		}		
		workflow = fromDTO(workflowDto, null);
		workflowService.create(workflow, currentUser);		
		if(workflowDto.getListWFTransitionDto() != null &&  !workflowDto.getListWFTransitionDto().isEmpty()){
            int priority = 1;
		    for(WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()){
                wfTransitionDto.setPriority(priority);
		    	wfTransitionApi.create(workflow, wfTransitionDto, currentUser);
                priority++;
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
	public void update(WorkflowDto workflowDto, User currentUser) throws EntityDoesNotExistsException, BusinessException, MissingParameterException,
               EntityAlreadyExistsException, BusinessApiException {
		validateDto(workflowDto, true);
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider(), Arrays.asList("transitions"));
		if (workflow == null) {
			throw new EntityDoesNotExistsException(Workflow.class, workflowDto.getCode());
		} 

        if (workflowDto.getWfType()!= null && !workflow.getWfType().equals(workflowDto.getWfType())) {
            throw new BusinessApiException();
        }

		workflow = fromDTO(workflowDto, workflow);
		workflowService.update(workflow, currentUser);
        List<WFTransition> listUpdate = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workflowDto.getListWFTransitionDto())) {
            for (WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()) {
                if (wfTransitionDto.getUuid() != null) {
                    WFTransition wfTransition = wfTransitionApi.findTransitionByUUID(wfTransitionDto.getUuid(), currentUser);
                    if (wfTransition != null) {
                        listUpdate.add(wfTransition);
                    }
                }
            }
        }

        List<WFTransition> currentWfTransitions = workflow.getTransitions();
        if (CollectionUtils.isNotEmpty(currentWfTransitions)) {
            currentWfTransitions.removeAll(listUpdate);
            if (CollectionUtils.isNotEmpty(currentWfTransitions)) {
                for (WFTransition wfTransition : currentWfTransitions) {
                    wfTransitionService.remove(wfTransition);
                }
            }
        }
		if (workflowDto.getListWFTransitionDto() != null &&  !workflowDto.getListWFTransitionDto().isEmpty()) {
            int priority = 1;
		    for (WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()) {
                wfTransitionDto.setPriority(priority);
		    	wfTransitionApi.createOrUpdate(workflow, wfTransitionDto, currentUser);
                priority++;

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
	public List<WorkflowDto> list(User currentUser){
        List<WorkflowDto> result = new ArrayList<>();
		List<Workflow> workflows =  workflowService.list(currentUser.getProvider());
		if (workflows != null){
			for(Workflow workflow : workflows){
                result.add(new WorkflowDto(workflow));
			}
		}
		return result;
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
	public void createOrUpdate(WorkflowDto workflowDto, User currentUser) throws EntityAlreadyExistsException, BusinessException,
            EntityDoesNotExistsException, MissingParameterException, BusinessApiException {
		Workflow workflow = workflowService.findByCode(workflowDto.getCode(), currentUser.getProvider());
		if(workflow == null){
			create(workflowDto, currentUser);
		}else{
			update(workflowDto, currentUser);
		}
	}

    protected Workflow fromDTO(WorkflowDto dto, Workflow workflowToUpdate) {
        Workflow workflow = new Workflow();
        if (workflowToUpdate != null) {
            workflow = workflowToUpdate;
        } else {
            workflow.setWfType(dto.getWfType());
        }

        workflow.setCode(dto.getCode());
        workflow.setDescription(dto.getDescription());
        workflow.setEnableHistory(dto.getEnableHistory());
        workflow.setExportLot(dto.getExportLot());

        return workflow;
    }
	
	/**
	 * 
	 * @param workflowDto
	 * @throws MissingParameterException
	 */
	public void validateDto(WorkflowDto workflowDto, boolean isUpdate) throws MissingParameterException{
		if (StringUtils.isBlank(workflowDto.getCode())) {
			missingParameters.add("code");
		}
		if (!isUpdate && StringUtils.isBlank(workflowDto.getWfType())) {
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


