package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.DunningPlanService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:12:55 AM
 *
 */
@Stateless
public class ActionPlanItemApi extends BaseApi {

	@Inject
	private DunningPlanService dunningPlanService;
	
	@Inject
	private ActionPlanItemService actionPlanItemService;

	
	public void create(ActionPlanItemDto actionPlanItemDto, User currentUser) throws MeveoApiException, BusinessException {
		if (actionPlanItemDto.getDunningLevel() == null) {
			missingParameters.add("DunningLevel");
		}
		if (actionPlanItemDto.getActionType() == null) {
			missingParameters.add("ActionType");
		}
		if (actionPlanItemDto.getItemOrder() == null) {
			missingParameters.add("ItemOrder");
		}
		if (actionPlanItemDto.getThresholdAmount() == null) {
			missingParameters.add("ThresholdAmount");
		}
		if (actionPlanItemDto.getChargeAmount() == null) {
			missingParameters.add("ChargeAmount");
		}
		if (actionPlanItemDto.getDunningPlan() == null) {
			missingParameters.add("DunningPlan");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(actionPlanItemDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, actionPlanItemDto.getDunningPlan());
		}
		
		ActionPlanItem existedActionPlanItem = actionPlanItemService.getActionPlanItem(actionPlanItemDto.getItemOrder(), actionPlanItemDto.getDunningLevel(), dunningPlan);
		
		if(existedActionPlanItem != null){
			throw new EntityAlreadyExistsException(ActionPlanItem.class.getName() + "with dunningPlan=" + actionPlanItemDto.getDunningPlan() +
					" and itemOrder =" + actionPlanItemDto.getItemOrder() + 
					" and dunningLevel =" + actionPlanItemDto.getDunningLevel());
		}
		
		actionPlanItemService.create(fromDto(actionPlanItemDto, null, dunningPlan), currentUser);

	}

	public void update(ActionPlanItemDto actionPlanItemDto, User currentUser) throws MeveoApiException,BusinessException{
		if (actionPlanItemDto.getDunningLevel() == null) {
			missingParameters.add("DunningLevel");
		}
		if (actionPlanItemDto.getActionType() == null) {
			missingParameters.add("ActionType");
		}
		if (actionPlanItemDto.getItemOrder() == null) {
			missingParameters.add("ItemOrder");
		}
		if (actionPlanItemDto.getThresholdAmount() == null) {
			missingParameters.add("ThresholdAmount");
		}
		if (actionPlanItemDto.getChargeAmount() == null) {
			missingParameters.add("ChargeAmount");
		}
		if (actionPlanItemDto.getDunningPlan() == null) {
			missingParameters.add("DunningPlan");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(actionPlanItemDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, actionPlanItemDto.getDunningPlan());
		}
		
		ActionPlanItem existedActionPlanItem = actionPlanItemService.getActionPlanItem(actionPlanItemDto.getItemOrder(), actionPlanItemDto.getDunningLevel(), dunningPlan);
		
		if (existedActionPlanItem == null) {
			throw new EntityDoesNotExistsException(ActionPlanItem.class.getName() + "with dunningPlan=" + actionPlanItemDto.getDunningPlan() +
					" and itemOrder =" + actionPlanItemDto.getItemOrder() + 
					" and dunningLevel =" + actionPlanItemDto.getDunningLevel());
		} 
		
		actionPlanItemService.update(fromDto(actionPlanItemDto, existedActionPlanItem, dunningPlan), currentUser);
	}

	public ActionPlanItemDto find(String dunningPlanCode, String dunningLevel, Integer itemOrder, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("dunningPlanCode");
		}
		if(dunningLevel == null){
			missingParameters.add("dunningLevel");
		}
		if(itemOrder == null){
			missingParameters.add("itemOrder");
		}
		
		handleMissingParameters();
		
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		
		ActionPlanItem actionPlanItem = actionPlanItemService.getActionPlanItem(itemOrder, DunningLevelEnum.valueOf(dunningLevel), dunningPlan);
		if (actionPlanItem == null) {
			throw new EntityDoesNotExistsException(ActionPlanItem.class.getName() + "with dunningPlan=" + dunningPlan +
					" and itemOrder =" + itemOrder + 
					" and dunningLevel =" + dunningLevel);
		} 
		
		return new ActionPlanItemDto(actionPlanItem);
	}

	public void remove(String dunningPlanCode, Integer itemOrder, String dunningLevel, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("code");
			handleMissingParameters();
		}
		
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		
		ActionPlanItem actionPlanItem = actionPlanItemService.getActionPlanItem(itemOrder, DunningLevelEnum.valueOf(dunningLevel), dunningPlan);
		if (actionPlanItem == null) {
			throw new EntityDoesNotExistsException(ActionPlanItem.class.getName() + "with dunningPlan=" + dunningPlan +
					" and itemOrder =" + itemOrder + 
					" and dunningLevel =" + dunningLevel);
		} 
		
		actionPlanItemService.remove(actionPlanItem); 
	}

	public void createOrUpdate(ActionPlanItemDto actionPlanItemDto, User currentUser) throws MeveoApiException,BusinessException{
		DunningPlan dunningPlan = dunningPlanService.findByCode(actionPlanItemDto.getDunningPlan(), currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, actionPlanItemDto.getDunningPlan());
		}
		ActionPlanItem actionPlanItem = actionPlanItemService.getActionPlanItem(actionPlanItemDto.getItemOrder(), actionPlanItemDto.getDunningLevel(), dunningPlan);
		if(actionPlanItem == null){
			create(actionPlanItemDto,currentUser);
		}else{
			update(actionPlanItemDto,currentUser);
		}
	}
	
	public static ActionPlanItem fromDto(ActionPlanItemDto actionPlanItemDto, ActionPlanItem apiToUpdate, DunningPlan dunningPlan) {
		ActionPlanItem actionPlanItem = new ActionPlanItem();
		if(apiToUpdate != null) {
			actionPlanItem = apiToUpdate;
		}
		
		actionPlanItem.setConditionEl(actionPlanItemDto.getConditionEl());
		actionPlanItem.setActionType(actionPlanItemDto.getActionType());
		actionPlanItem.setChargeAmount(actionPlanItemDto.getChargeAmount());
		actionPlanItem.setDunningLevel(actionPlanItemDto.getDunningLevel());
		actionPlanItem.setDunningPlan(dunningPlan);
		actionPlanItem.setItemOrder(actionPlanItemDto.getItemOrder());
		actionPlanItem.setThresholdAmount(actionPlanItemDto.getThresholdAmount());
		
		return actionPlanItem;
	}

}


