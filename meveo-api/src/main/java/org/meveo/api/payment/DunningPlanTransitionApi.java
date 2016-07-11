package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.payments.impl.DunningPlanService;
import org.meveo.service.payments.impl.DunningPlanTransitionService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:12:55 AM
 *
 */
@Stateless
public class DunningPlanTransitionApi extends BaseApi {

	@Inject
	private DunningPlanService dunningPlanService;
	
	@Inject
	private DunningPlanTransitionService dunningPlanTransitionService;

	
	public void create(DunningPlanTransitionDto dunningPlanTransitionDto, User currentUser) throws MeveoApiException, BusinessException {
		if (dunningPlanTransitionDto.getDunningPlan() == null) {
			missingParameters.add("DunningPlan");
		}
		if (dunningPlanTransitionDto.getDunningLevelFrom() == null) {
			missingParameters.add("DunningLevelFrom");
		}
		if (dunningPlanTransitionDto.getDunningLevelTo() == null) {
			missingParameters.add("DunningLevelTo");
		}
		if (dunningPlanTransitionDto.getThresholdAmount() == null) {
			missingParameters.add("ThresholdAmount");
		}
		if (dunningPlanTransitionDto.getDelayBeforeProcess() == null) {
			missingParameters.add("DelayBeforeProcess");
		}
		if (dunningPlanTransitionDto.getWaitDuration() == null) {
			missingParameters.add("WaitDuration");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanTransitionDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanTransitionDto.getDunningPlan());
		}
		
		DunningPlanTransition existedDunningPlanTransition = dunningPlanTransitionService.getDunningPlanTransition(dunningPlanTransitionDto.getDunningLevelFrom(), 
				dunningPlanTransitionDto.getDunningLevelTo(), dunningPlan);
		
		if(existedDunningPlanTransition != null){
			throw new EntityAlreadyExistsException(DunningPlanTransition.class.getName() + "with dunningPlan=" + dunningPlanTransitionDto.getDunningPlan() +
					" and dunningLevelFrom =" + dunningPlanTransitionDto.getDunningLevelFrom() + 
					" and dunningLevelTo =" + dunningPlanTransitionDto.getDunningLevelTo());
		}
		
		dunningPlanTransitionService.create(fromDto(dunningPlanTransitionDto, null, dunningPlan), currentUser);

	}

	public void update(DunningPlanTransitionDto dunningPlanTransitionDto, User currentUser) throws MeveoApiException,BusinessException{
		if (dunningPlanTransitionDto.getDunningPlan() == null) {
			missingParameters.add("DunningPlan");
		}
		if (dunningPlanTransitionDto.getDunningLevelFrom() == null) {
			missingParameters.add("DunningLevelFrom");
		}
		if (dunningPlanTransitionDto.getDunningLevelTo() == null) {
			missingParameters.add("DunningLevelTo");
		}
		if (dunningPlanTransitionDto.getThresholdAmount() == null) {
			missingParameters.add("ThresholdAmount");
		}
		if (dunningPlanTransitionDto.getDelayBeforeProcess() == null) {
			missingParameters.add("DelayBeforeProcess");
		}
		if (dunningPlanTransitionDto.getWaitDuration() == null) {
			missingParameters.add("WaitDuration");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanTransitionDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanTransitionDto.getDunningPlan());
		}
		
		DunningPlanTransition dunningPlanTransition = dunningPlanTransitionService.getDunningPlanTransition(dunningPlanTransitionDto.getDunningLevelFrom(), 
				dunningPlanTransitionDto.getDunningLevelTo(), dunningPlan);
				
		if (dunningPlanTransition == null) {
			throw new EntityDoesNotExistsException(DunningPlanTransition.class.getName() + "with dunningPlan=" + dunningPlanTransitionDto.getDunningPlan() +
					" and dunningLevelFrom =" + dunningPlanTransitionDto.getDunningLevelFrom() + 
					" and dunningLevelTo =" + dunningPlanTransitionDto.getDunningLevelTo());
		} 
		
		dunningPlanTransitionService.update(fromDto(dunningPlanTransitionDto, dunningPlanTransition, dunningPlan), currentUser);
	}

	public DunningPlanTransitionDto find(String dunningPlanCode, DunningLevelEnum dunningLevelFrom, DunningLevelEnum dunningLevelTo, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("dunningPlanCode");
		}
		if(dunningLevelFrom == null){
			missingParameters.add("dunningLevelFrom");
		}
		if(dunningLevelTo == null){
			missingParameters.add("dunningLevelTo");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		
		DunningPlanTransition dunningPlanTransition = dunningPlanTransitionService.getDunningPlanTransition(dunningLevelFrom, dunningLevelTo, dunningPlan);
		if(dunningPlanTransition == null){
			throw new EntityDoesNotExistsException(DunningPlanTransition.class.getName() + "with dunningPlan=" + dunningPlanCode +
					" and dunningLevelFrom =" + dunningLevelFrom + 
					" and dunningLevelTo =" + dunningLevelTo);
		}
		
		return new DunningPlanTransitionDto(dunningPlanTransition);
	}

	public void remove(String dunningPlanCode, DunningLevelEnum dunningLevelFrom, DunningLevelEnum dunningLevelTo, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("code");
			handleMissingParameters();
		}
		
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		
		DunningPlanTransition dunningPlanTransition = dunningPlanTransitionService.getDunningPlanTransition(dunningLevelFrom, dunningLevelTo, dunningPlan);
		if(dunningPlanTransition == null){
			throw new EntityDoesNotExistsException(DunningPlanTransition.class.getName() + "with dunningPlan=" + dunningPlanCode +
					" and dunningLevelFrom =" + dunningLevelFrom + 
					" and dunningLevelTo =" + dunningLevelTo);
		}
		
		dunningPlanTransitionService.remove(dunningPlanTransition); 
	}

	public void createOrUpdate(DunningPlanTransitionDto dunningPlanTransitionDto, User currentUser) throws MeveoApiException,BusinessException{
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanTransitionDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanTransitionDto.getDunningPlan());
		}
		
		DunningPlanTransition dunningPlanTransition = dunningPlanTransitionService.getDunningPlanTransition(dunningPlanTransitionDto.getDunningLevelFrom(), 
				dunningPlanTransitionDto.getDunningLevelTo(), dunningPlan);
		if(dunningPlanTransition == null){
			create(dunningPlanTransitionDto,currentUser);
		}else{
			update(dunningPlanTransitionDto,currentUser);
		}
	}
	
	public static DunningPlanTransition fromDto(DunningPlanTransitionDto dunningPlanTransitionDto, DunningPlanTransition dptToUpdate, DunningPlan dunningPlan) {
		DunningPlanTransition dunningPlanTransition = new DunningPlanTransition();
		if(dptToUpdate != null) {
			dunningPlanTransition = dptToUpdate;
		}
		
		dunningPlanTransition.setConditionEl(dunningPlanTransitionDto.getConditionEl());
		dunningPlanTransition.setDelayBeforeProcess(dunningPlanTransitionDto.getDelayBeforeProcess());
		dunningPlanTransition.setDunningLevelFrom(dunningPlanTransitionDto.getDunningLevelFrom());
		dunningPlanTransition.setDunningLevelTo(dunningPlanTransitionDto.getDunningLevelTo());
		dunningPlanTransition.setDunningPlan(dunningPlan);
		dunningPlanTransition.setThresholdAmount(dunningPlanTransitionDto.getThresholdAmount());
		dunningPlanTransition.setWaitDuration(dunningPlanTransitionDto.getWaitDuration());
		
		return dunningPlanTransition;
	}

}


