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
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.DunningPlan;
import org.meveo.service.payments.impl.CreditCategoryService;
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
		Provider provider = currentUser.getProvider();
		DunningPlan existedDunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), provider);
		if(existedDunningPlan!=null){
			throw new EntityAlreadyExistsException(DunningPlan.class, dunningPlanDto.getCode());
		}
		// check if creditCategory cat exists
		CreditCategory creditCategory = creditCategoryService.findByCode(dunningPlanDto.getCreditCategory(), provider);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceCategory.class, dunningPlanDto.getCreditCategory());
		}
		DunningPlan dunningPlan=new DunningPlan();
		dunningPlan.setCode(dunningPlanDto.getCode());
		dunningPlan.setDescription(dunningPlanDto.getDescription());
		dunningPlan.setPaymentMethod(dunningPlanDto.getPaymentMethod());
		dunningPlan.setStatus(dunningPlanDto.getStatus()); 
		dunningPlan.setCreditCategory(creditCategory);
		dunningPlanService.create(dunningPlan, currentUser);

	}
	public void update(DunningPlanDto dunningPlanDto,User currentUser) throws MeveoApiException,BusinessException{
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
		Provider provider=currentUser.getProvider();
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanDto.getCode(), provider);
		if (dunningPlan == null) {
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanDto.getCode());
		} 
		CreditCategory creditCategory = creditCategoryService.findByCode(dunningPlanDto.getCreditCategory(), provider);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(InvoiceCategory.class, dunningPlanDto.getCreditCategory());
		}
		dunningPlan.setDescription(dunningPlanDto.getDescription());
		dunningPlan.setPaymentMethod(dunningPlanDto.getPaymentMethod());
		dunningPlan.setStatus(dunningPlanDto.getStatus());
		dunningPlan.setCreditCategory(creditCategory);
		dunningPlanService.update(dunningPlan, currentUser); 
	}
	public DunningPlanDto find(String dunningPlanCode,Provider provider) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("dunningPlanCode");
		}
		handleMissingParameters();
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, provider);
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		return new DunningPlanDto(dunningPlan);
	}
	public void remove(String dunningPlanCode,Provider provider) throws MeveoApiException{
		if(StringUtils.isEmpty(dunningPlanCode)){
			missingParameters.add("code");
		}
		handleMissingParameters();
		DunningPlan dunningPlan=dunningPlanService.findByCode(dunningPlanCode, provider);
		if(dunningPlan==null){
			throw new EntityDoesNotExistsException(DunningPlan.class, dunningPlanCode);
		}
		dunningPlanService.remove(dunningPlan); 
		missingParameters.add("code");
		handleMissingParameters(); 
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


