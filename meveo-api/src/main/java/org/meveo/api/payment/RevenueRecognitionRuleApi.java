package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.RevenueRecognitionRuleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.RevenueRecognitionRule;
import org.meveo.model.scripts.RevenueRecognitionScript;
import org.meveo.service.payments.impl.RevenueRecognitionRuleService;
import org.meveo.service.script.revenue.RevenueRecognitionScriptService;

@Stateless
public class RevenueRecognitionRuleApi extends BaseApi {
	
	@Inject
	RevenueRecognitionRuleService revenueRecognitionRuleService;
	
	@Inject
	RevenueRecognitionScriptService revenueRecognitionScriptService;
	
	public void create(RevenueRecognitionRuleDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScriptCode())) {
			missingParameters.add("scriptCode");
		}
        handleMissingParameters();

		if (revenueRecognitionRuleService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(RevenueRecognitionRule.class, postData.getCode());
		}

		RevenueRecognitionRule rrr = new RevenueRecognitionRule();
		rrr.setCode(postData.getCode());
		
		RevenueRecognitionScript scriptInstance = 
				revenueRecognitionScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionScript.class, postData.getScriptCode());
		}
		rrr.setScript(scriptInstance);
		
		rrr.setDescription(postData.getDescription());
		rrr.setDisabled(postData.isDisabled());
		rrr.setProvider(currentUser.getProvider());
		rrr.setStartDelay(postData.getStartDelay());
		rrr.setStartUnit(postData.getStartUnit());
		rrr.setStartEvent(postData.getStartEvent());
		rrr.setStopDelay(postData.getStopDelay());
		rrr.setStopUnit(postData.getStopUnit());
		rrr.setStopEvent(postData.getStopEvent());
		revenueRecognitionRuleService.create(rrr, currentUser);
	}
	
	public void update(RevenueRecognitionRuleDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScriptCode())) {
			missingParameters.add("scriptCode");
		}
        handleMissingParameters();

		RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(postData.getCode(), currentUser.getProvider());
		if (rrr == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionRule.class, postData.getCode());
		}

		rrr.setCode(postData.getCode());
		
		RevenueRecognitionScript scriptInstance = 
				revenueRecognitionScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionScript.class, postData.getScriptCode());
		}
		rrr.setScript(scriptInstance);
		
		rrr.setDescription(postData.getDescription());
		rrr.setDisabled(postData.isDisabled());
		rrr.setProvider(currentUser.getProvider());
		rrr.setStartDelay(postData.getStartDelay());
		rrr.setStartUnit(postData.getStartUnit());
		rrr.setStartEvent(postData.getStartEvent());
		rrr.setStopDelay(postData.getStopDelay());
		rrr.setStopUnit(postData.getStopUnit());
		rrr.setStopEvent(postData.getStopEvent());
		revenueRecognitionRuleService.create(rrr, currentUser);
	}
	
	public RevenueRecognitionRuleDto find(String revenueRecognitionRuleCode, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(revenueRecognitionRuleCode)) {
			missingParameters.add("revenueRecognitionRuleCode");
		}
        handleMissingParameters();

		RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(revenueRecognitionRuleCode, currentUser.getProvider());
		if (rrr == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionRule.class, revenueRecognitionRuleCode);
		}
		RevenueRecognitionRuleDto result = new RevenueRecognitionRuleDto(rrr);
		return result;
	}
	
	public void remove(String revenueRecognitionRuleCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(revenueRecognitionRuleCode)) {
			missingParameters.add("revenueRecognitionRuleCode");
		}

		handleMissingParameters();
		RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(revenueRecognitionRuleCode, provider);
		if (rrr == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionRule.class,revenueRecognitionRuleCode);
		}
	
		revenueRecognitionRuleService.remove(rrr);
	}

	public void createOrUpdate(RevenueRecognitionRuleDto postData, User currentUser) throws MeveoApiException, BusinessException {
		RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(postData.getCode(), currentUser.getProvider());
		if (rrr == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}

	public List<RevenueRecognitionRuleDto> list(User currentUser) {
		List<RevenueRecognitionRuleDto> result = new ArrayList<RevenueRecognitionRuleDto>();
		List<RevenueRecognitionRule>rules= revenueRecognitionRuleService.list(currentUser.getProvider());
		for(RevenueRecognitionRule rule:rules){
			result.add(new RevenueRecognitionRuleDto(rule));
		}
		return result;
	}
	

}
