package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.dto.payment.DunningPlansDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.DunningPlanService;
import org.meveo.service.payments.impl.DunningPlanTransitionService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:12:55 AM
 *
 */
@Stateless
public class DunningPlanApi extends BaseApi {

	@Inject
	private DunningPlanService dunningPlanService;
	
	@Inject
	private DunningPlanTransitionService dunningPlanTransitionService;
	
	@Inject
	private ActionPlanItemService actionPlanItemService;
	
	@Inject
	private CreditCategoryService creditCategoryService;
	
	public void create(DunningPlanDto dunningPlanDto, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(dunningPlanDto.getCode())) {
			missingParameters.add("code");
		}
		if (dunningPlanDto.getCreditCategory() == null) {
			missingParameters.add("creditCategory");
		}
		if (dunningPlanDto.getPaymentMethod() == null) {
			missingParameters.add("paymentMethod");
		} 
		if (dunningPlanDto.getStatus() == null) {
			missingParameters.add("status");
		} 
		handleMissingParameters();
		DunningPlan existedDunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
		if(existedDunningPlan!=null){
			throw new EntityAlreadyExistsException(DunningPlan.class, dunningPlanDto.getCode());
		}
		// check if creditCategory cat exists
		CreditCategory creditCategory = creditCategoryService.findByCode(dunningPlanDto.getCreditCategory(), currentUser.getProvider());
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceCategory.class, dunningPlanDto.getCreditCategory());
		}
		DunningPlan dunningPlan = fromDto(dunningPlanDto, null, creditCategory);
		dunningPlanService.create(dunningPlan, currentUser);
		
		if(CollectionUtils.isNotEmpty(dunningPlanDto.getDunningPlanTransition())) {
			List<DunningPlanTransition> transitions = new ArrayList<>();
			for(DunningPlanTransitionDto dptDto : dunningPlanDto.getDunningPlanTransition()){
				DunningPlanTransition trs = DunningPlanTransitionApi.fromDto(dptDto, null, dunningPlan);
				dunningPlanTransitionService.create(trs, currentUser);
				transitions.add(trs);
			}
			dunningPlan.setTransitions(transitions);
		}
		
		if(CollectionUtils.isNotEmpty(dunningPlanDto.getActionPlanItem())) {
			List<ActionPlanItem> items = new ArrayList<>();
			for(ActionPlanItemDto apiDto : dunningPlanDto.getActionPlanItem()){
				ActionPlanItem action = ActionPlanItemApi.fromDto(apiDto, null, dunningPlan);
				actionPlanItemService.create(action, currentUser);
				items.add(action);
			}
			dunningPlan.setActions(items);
		}
		
		dunningPlanService.update(dunningPlan, currentUser);

	}

	public void update(DunningPlanDto dunningPlanDto, User currentUser) throws MeveoApiException,BusinessException{
		if (dunningPlanDto.getCreditCategory() == null) {
			missingParameters.add("creditCategory");
		}
		if (dunningPlanDto.getPaymentMethod() == null) {
			missingParameters.add("paymentMethod");
		} 
		if (dunningPlanDto.getStatus() == null) {
			missingParameters.add("status");
		} 
		handleMissingParameters();
		DunningPlan dunningPlan = dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
		if (dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanDto.getCode());
		} 
		CreditCategory creditCategory = creditCategoryService.findByCode(dunningPlanDto.getCreditCategory(), currentUser.getProvider());
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceCategory.class, dunningPlanDto.getCreditCategory());
		}

		dunningPlan = fromDto(dunningPlanDto, dunningPlan, creditCategory);
		dunningPlanService.update(dunningPlan, currentUser); 
	}

	public DunningPlanDto find(String dunningPlanCode, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("dunningPlanCode");
			handleMissingParameters();
		}

		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		return new DunningPlanDto(dunningPlan);
	}

	public void remove(String dunningPlanCode, User currentUser) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("code");
			handleMissingParameters();
		}
		
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, currentUser.getProvider());
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		dunningPlanService.remove(dunningPlan); 
	}

	public DunningPlansDto list(User currentUser) throws MeveoApiException{
		DunningPlansDto result=new DunningPlansDto();
		List<DunningPlan> dunningPlans=dunningPlanService.list(currentUser.getProvider());
		if(dunningPlans!=null){
			for(DunningPlan dunningPlan:dunningPlans){
				result.getDunningPlans().add(new DunningPlanDto(dunningPlan));
			}
		}
		return result;
	}

	public void createOrUpdate(DunningPlanDto dunningPlanDto, User currentUser) throws MeveoApiException,BusinessException{
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
		if(dunningPlan==null){
			create(dunningPlanDto,currentUser);
		}else{
			update(dunningPlanDto,currentUser);
		}
	}
	
	public static DunningPlan fromDto(DunningPlanDto dunningPlanDto, DunningPlan dpToUpdate, CreditCategory creditCategory) {
		DunningPlan dunningPlan = new DunningPlan();
		if(dpToUpdate != null) {
			dunningPlan = dpToUpdate;
		}
		
		dunningPlan.setCode(dunningPlanDto.getCode());
		dunningPlan.setDescription(dunningPlanDto.getDescription());
		dunningPlan.setPaymentMethod(dunningPlanDto.getPaymentMethod());
		dunningPlan.setCreditCategory(creditCategory);
		dunningPlan.setStatus(dunningPlanDto.getStatus()); 
		
		return dunningPlan;
	}

}


