package org.meveo.api.payment;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.payment.DunningPlansDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DunningPlan;
import org.meveo.service.payments.impl.DunningPlanService;

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
	
	public void create(DunningPlanDto dunningPlanDto, User currentUser) throws MeveoApiException, BusinessException {
		if(StringUtils.isNotEmpty(dunningPlanDto.getCode())){
			DunningPlan existedDunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
			if(existedDunningPlan!=null){
				throw new EntityAlreadyExistsException(DunningPlan.class, dunningPlanDto.getCode());
			}
			DunningPlan dunningPlan=new DunningPlan();
			dunningPlan.setCode(dunningPlanDto.getCode());
			dunningPlan.setDescription(dunningPlanDto.getDescription());
			dunningPlan.setPaymentMethod(dunningPlanDto.getPaymentMethod());
			dunningPlan.setStatus(dunningPlanDto.getStatus());
			dunningPlanService.create(dunningPlan, currentUser);
		}else{
			missingParameters.add("code");
			handleMissingParameters();
		}
	}
	public void update(DunningPlanDto dunningPlanDto,User currentUser) throws MeveoApiException,BusinessException{
		if(StringUtils.isNotEmpty(dunningPlanDto.getCode())){
			DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
			if(dunningPlan==null){
				throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanDto.getCode());
			}
			dunningPlan.setDescription(dunningPlanDto.getDescription());
			dunningPlan.setPaymentMethod(dunningPlanDto.getPaymentMethod());
			dunningPlan.setStatus(dunningPlanDto.getStatus());
			dunningPlanService.update(dunningPlan, currentUser);
		}else{
			missingParameters.add("code");
			handleMissingParameters();
		}
	}
	public DunningPlanDto find(String code,Provider provider) throws MeveoApiException{
		if(StringUtils.isEmpty(code)){
			missingParameters.add("code");
		}
		handleMissingParameters();
		DunningPlan dunningPlan=dunningPlanService.findByCode(code, provider);
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, code);
		}
		return new DunningPlanDto(dunningPlan);
	}
	public void remove(String code,Provider provider) throws MeveoApiException{
		if(StringUtils.isNoneEmpty(code)){
			DunningPlan dunningPlan=dunningPlanService.findByCode(code, provider);
			if(dunningPlan==null){
				throw new EntityDoesNotExistsException(DunningPlan.class, code);
			}
			dunningPlanService.remove(dunningPlan);
		}else{
			missingParameters.add("code");
			handleMissingParameters();
		}
	}
	public DunningPlansDto list(Provider provider) throws MeveoApiException{
		DunningPlansDto result=new DunningPlansDto();
		List<DunningPlan> dunningPlans=dunningPlanService.list(provider);
		if(dunningPlans!=null){
			for(DunningPlan dunningPlan:dunningPlans){
				result.getDunningPlans().add(new DunningPlanDto(dunningPlan));
			}
		}
		return result;
	}
	public void createOrUpdate(DunningPlanDto dunningPlanDto,User currentUser) throws MeveoApiException,BusinessException{
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), currentUser.getProvider());
		if(dunningPlan==null){
			create(dunningPlanDto,currentUser);
		}else{
			update(dunningPlanDto,currentUser);
		}
	}
}


