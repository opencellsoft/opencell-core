package org.meveo.api.billing;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.billing.BillingRunDto;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.util.MeveoParamBean;

@Stateless
public class InvoicingApi extends BaseApi {


	@Inject
	BillingRunService billingRunService;

	@Inject
	BillingCycleService billingCycleService;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;


	public long createBillingRun(CreateBillingRunDto createBillingRunDto,User currentUser) throws BusinessApiException, MissingParameterException, EntityDoesNotExistsException {

		String allowManyInvoicing = paramBean.getProperty("billingRun.allowManyInvoicing", "true");
		boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
		Provider provider = currentUser.getProvider();
		if (billingRunService.isActiveBillingRunsExist(provider) && !isAllowed) {
			throw new BusinessApiException("error.invoicing.alreadyLunched");			
		}

		if (StringUtils.isBlank(createBillingRunDto.getBillingCycleCode())) {
			missingParameters.add("billingCycleCode");
		}
		if (StringUtils.isBlank(createBillingRunDto.getBillingRunTypeEnum())) {
			missingParameters.add("billingRunType");
		}
		if(! missingParameters.isEmpty()){
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		BillingCycle billingCycleInput = billingCycleService.findByBillingCycleCode(createBillingRunDto.getBillingCycleCode(), provider);
		if (billingCycleInput == null) {
			throw new EntityDoesNotExistsException(BillingCycle.class, createBillingRunDto.getBillingCycleCode());
		}
		BillingRun billingRunEntity = new BillingRun();
		billingRunEntity.setBillingCycle(billingCycleInput);
		billingRunEntity.setProcessType(BillingProcessTypesEnum.valueOf(createBillingRunDto.getBillingRunTypeEnum().toString()));
		billingRunEntity.setStartDate(createBillingRunDto.getStartDate());
		billingRunEntity.setEndDate(createBillingRunDto.getEndDate());
		billingRunEntity.setProcessDate(new Date());
		billingRunEntity.setInvoiceDate(createBillingRunDto.getInvoiceDate());
		if(createBillingRunDto.getInvoiceDate() == null){
			if (billingCycleInput.getInvoiceDateProductionDelay() != null) {
				billingRunEntity.setInvoiceDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), billingCycleInput.getInvoiceDateProductionDelay()));
			} else {
				billingRunEntity.setInvoiceDate(billingRunEntity.getProcessDate());
			}
		}
		billingRunEntity.setLastTransactionDate(createBillingRunDto.getLastTransactionDate());	
		if(createBillingRunDto.getInvoiceDate() == null){
			if (billingCycleInput.getTransactionDateDelay() != null) {
				billingRunEntity.setLastTransactionDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), billingCycleInput.getTransactionDateDelay()));
			} else {
				billingRunEntity.setLastTransactionDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), 1));
			}
		}
		billingRunEntity.setStatus(BillingRunStatusEnum.NEW);
		billingRunEntity.setProvider(provider);
		billingRunService.create(billingRunEntity, currentUser, provider);
		return billingRunEntity.getId();
	}
	
	
	public BillingRunDto getBillingRunInfo(Long billingRunId, User currentUser) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException{
		BillingRun billingRunEntity   = getBillingRun(billingRunId, currentUser);
		
		BillingRunDto billingRunDtoResult = new BillingRunDto();
		billingRunDtoResult.setFromEntity(billingRunEntity);
		return billingRunDtoResult;
	}
	 
	public BillingAccountsDto getBillingAccountListInRun(Long billingRunId, User currentUser) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException{
		BillingRun billingRunEntity   = getBillingRun(billingRunId, currentUser);
		BillingAccountsDto billingAccountsDtoResult = new BillingAccountsDto();
		List<BillingAccount> baEntities = billingRunEntity.getBillableBillingAccounts();
		if(baEntities != null && !baEntities.isEmpty()){
			for(BillingAccount baEntity : baEntities){
				billingAccountsDtoResult.getBillingAccount().add(new BillingAccountDto(baEntity));
			}
		}
		return billingAccountsDtoResult;
	}
	
	
	
	private BillingRun getBillingRun(Long billingRunId, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException{
		if(billingRunId == null || billingRunId.longValue() ==0){
			missingParameters.add("billingRunId");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
			
		if(billingRunId.longValue() <= 0){
			throw new BusinessApiException("The billingRunId should be a positive value");
		}
		
		BillingRun billingRunEntity = billingRunService.findById(billingRunId, currentUser.getProvider());
		if(billingRunEntity == null){
			throw new EntityDoesNotExistsException(BillingRun.class, billingRunId);
		}
		return billingRunEntity;
	}
	
	
	public PreInvoicingReportsDTO getPreInvoicingReport(Long billingRunId, User currentUser) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException, BusinessException{
		BillingRun billingRun  = getBillingRun( billingRunId, currentUser);
		if( !BillingRunStatusEnum.WAITING.equals(billingRun.getStatus()) ){
			throw new BusinessApiException("BillingRun is not at the WAITING status");
		}
		PreInvoicingReportsDTO preInvoicingReportsDTO = billingRunService.generatePreInvoicingReports(billingRun);
		return preInvoicingReportsDTO;
	}
	
	public PostInvoicingReportsDTO getPostInvoicingReport(Long billingRunId, User currentUser) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException, BusinessException{
		BillingRun billingRun  = getBillingRun( billingRunId, currentUser);
		if( !BillingRunStatusEnum.TERMINATED.equals(billingRun.getStatus()) ){
			throw new BusinessApiException("BillingRun is not at the TERMINATED status");
		}
		PostInvoicingReportsDTO postInvoicingReportsDTO = billingRunService.generatePostInvoicingReports(billingRun);
		return postInvoicingReportsDTO;
	}
	
	public void validateBillingRun(Long billingRunId, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException{
		BillingRun billingRun  = getBillingRun( billingRunId, currentUser);
		billingRunService.validate(billingRun, currentUser);
	}
	
	public void cancelBillingRun(Long billingRunId, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException{
		BillingRun billingRun  = getBillingRun( billingRunId, currentUser);
		if( BillingRunStatusEnum.CONFIRMED.equals(billingRun.getStatus()) ){
			throw new BusinessApiException("Cannot cancel a confirmed billingRun");
		}
		if(BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus())){
			throw new BusinessApiException("Cannot cancel a validated billingRun");
		}
		billingRunService.cancel(billingRun);
		billingRunService.cleanBillingRun(billingRun);
	}
	
}
