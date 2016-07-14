package org.meveo.api.wf;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFTransitionService;


@Stateless
public class WFActionApi extends BaseApi {

	@Inject
	private  WFActionService  wfActionService;
	
	@Inject
	private  WFTransitionService  wfTransitionService;
	
	/**
	 * 	
	 * @param wfActionDto
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws EntityAlreadyExistsException
	 * @throws BusinessException
	 */
	public void create(WFActionDto wfActionDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException {
		validateDto(wfActionDto);
		WFTransition  wfTransition = wfTransitionService.findWFTransition(wfActionDto.getWfTransitionDto().getFromStatus(), wfActionDto.getWfTransitionDto().getToStatus(), 
				wfActionDto.getWfTransitionDto().getWorkflowCode(), currentUser.getProvider());
				
		WFAction wfAction = wfActionService.findByPriorityAndTransition(wfTransition, wfActionDto.getPriority(), currentUser.getProvider());
		
		if(wfAction != null){
			throw new EntityAlreadyExistsException(WFAction.class.getName() + "with priority=" + wfActionDto.getPriority() +" and wfTransition =" + wfActionDto.getWfTransitionDto());
		}
		
		wfAction = wfActionDto.fromDto(wfAction);
		wfAction.setWfTransition(wfTransition);
		wfActionService.create(wfAction, currentUser);
	}
 
	/**
	 * 
	 * @param wfActionDto
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws BusinessException
	 */
	public void update(WFActionDto wfActionDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		validateDto(wfActionDto);
		WFTransition  wfTransition = wfTransitionService.findWFTransition(wfActionDto.getWfTransitionDto().getFromStatus(), wfActionDto.getWfTransitionDto().getToStatus(), 
				wfActionDto.getWfTransitionDto().getWorkflowCode(), currentUser.getProvider());
		
		WFAction wfAction = wfActionService.findByPriorityAndTransition(wfTransition, wfActionDto.getPriority(), currentUser.getProvider());
		
		if(wfAction == null){
			throw new EntityDoesNotExistsException(WFAction.class.getName() + "with priority=" + wfActionDto.getPriority() +" and wfTransition =" + wfActionDto.getWfTransitionDto());
		}
		
		wfAction = wfActionDto.fromDto(wfAction);
		wfAction.setWfTransition(wfTransition);
		wfActionService.create(wfAction, currentUser);
	}
	
	/**
	 * 
	 * @param wfActionDto
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws EntityAlreadyExistsException
	 * @throws BusinessException
	 */
	public void createOrUpdate(WFActionDto wfActionDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException {		
		try {
			find(wfActionDto.getWfTransitionDto(), wfActionDto.getPriority(), currentUser);		
		} catch (EntityDoesNotExistsException e) {
			create(wfActionDto, currentUser);
		}
		update(wfActionDto, currentUser);
	}

	/**
	 * 
	 * @param wfTransitionDto
	 * @param priority
	 * @param currentUser
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
	public WFActionDto find(WFTransitionDto wfTransitionDto,Integer priority , User currentUser) throws MissingParameterException, EntityDoesNotExistsException {
		WFActionDto wfActionDto = new WFActionDto();
		wfActionDto.setPriority(priority);
		wfActionDto.setWfTransitionDto(wfTransitionDto);
		validateDto(wfActionDto);
		WFTransition  wfTransition = wfTransitionService.findWFTransition(wfActionDto.getWfTransitionDto().getFromStatus(), wfActionDto.getWfTransitionDto().getToStatus(), 
				wfActionDto.getWfTransitionDto().getWorkflowCode(), currentUser.getProvider());
		
		WFAction wfAction = wfActionService.findByPriorityAndTransition(wfTransition, wfActionDto.getPriority(), currentUser.getProvider());
		
		if(wfAction == null){
			throw new EntityDoesNotExistsException(WFAction.class.getName() + "with priority=" + wfActionDto.getPriority() +" and wfTransition =" + wfActionDto.getWfTransitionDto());
		}		
		wfActionDto = new WFActionDto(wfAction);
		return wfActionDto;
	}

	/**
	 * 
	 * @param wfTransitionDto
	 * @param priority
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
	public void remove(WFTransitionDto wfTransitionDto,Integer priority , User currentUser) throws MissingParameterException, EntityDoesNotExistsException {
		WFActionDto wfActionDto = new WFActionDto();
		wfActionDto.setPriority(priority);
		wfActionDto.setWfTransitionDto(wfTransitionDto);
		validateDto(wfActionDto);
		WFTransition  wfTransition = wfTransitionService.findWFTransition(wfActionDto.getWfTransitionDto().getFromStatus(), wfActionDto.getWfTransitionDto().getToStatus(), 
				wfActionDto.getWfTransitionDto().getWorkflowCode(), currentUser.getProvider());
		
		WFAction wfAction = wfActionService.findByPriorityAndTransition(wfTransition, wfActionDto.getPriority(), currentUser.getProvider());
		
		if(wfAction == null){
			throw new EntityDoesNotExistsException(WFAction.class.getName() + "with priority=" + wfActionDto.getPriority() +" and wfTransition =" + wfActionDto.getWfTransitionDto());
		}		
		wfActionService.remove(wfAction);
	}
	
    /**
     * 
     * @param wfActionDto
     * @throws MissingParameterException
     */
	public void validateDto(WFActionDto wfActionDto) throws MissingParameterException{
		if (StringUtils.isBlank(wfActionDto.getActionEl())) {
			missingParameters.add("actionEl");
		}
		if (StringUtils.isBlank(wfActionDto.getPriority())) {
			missingParameters.add("priority");
		}
		if (wfActionDto.getWfTransitionDto() == null) {			
			missingParameters.add("WFTransitionDto");
			handleMissingParameters();	
		}	
		
		if (StringUtils.isBlank(wfActionDto.getWfTransitionDto().getFromStatus())) {
			missingParameters.add("WFTransition.fromStatus");
		}
		
		if (StringUtils.isBlank(wfActionDto.getWfTransitionDto().getToStatus())) {
			missingParameters.add("WFTransition.toStatus");
		}
		
		if (StringUtils.isBlank(wfActionDto.getWfTransitionDto().getWorkflowCode())) {
			missingParameters.add("WorkflowDto.code");
		}
		
		handleMissingParameters();	
	}
	
}


