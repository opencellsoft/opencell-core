package org.meveo.apiv2.cpq.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.service.cpq.BillingRuleService;
import org.meveo.service.cpq.ContractService;

@Stateless
public class CpqContractApiService {
	
	@Inject
	private ContractService contractService;

	@Inject
	private BillingRuleService billingRuleService;

	@TransactionAttribute
	public BillingRule createBillingRule(String contractCode, BillingRuleDto billingRuleDto) {

		checkBillingRulesRedirectionIsEnabled();
		// Check mandatory fields
		handleMissingParameters(contractCode, billingRuleDto);

		// Check contract
		Contract contract = this.contractService.findByCode(contractCode);
		if(contract == null) {
			throw new EntityDoesNotExistsException("Contract doesn't exists");
		}

		BillingRule billingRule = new BillingRule();
		
		billingRule.setContract(contract);
		billingRule.setPriority(billingRuleDto.getPriority());
		billingRule.setCriteriaEL(billingRuleDto.getCriteriaEL());
		billingRule.setInvoicedBACodeEL(billingRuleDto.getInvoicedBACodeEL());

		billingRuleService.create(billingRule);
		
		return billingRule;

	}

	@TransactionAttribute
	public BillingRule updateBillingRule(String contractCode, Long idBillingRule, BillingRuleDto billingRuleDto) {

		checkBillingRulesRedirectionIsEnabled();
		// Check mandatory fields
		handleMissingParameters(contractCode, billingRuleDto);

		// Check contract
		Contract contract = this.contractService.findByCode(contractCode);
		if(contract == null) {
			throw new EntityDoesNotExistsException("Contract doesn't exists");
		}

		BillingRule billingRule = this.billingRuleService.findById(idBillingRule);
		if(billingRule == null) {
			throw new EntityDoesNotExistsException("BillingRule doesn't exists");
		}

		billingRule.setPriority(billingRuleDto.getPriority());
		billingRule.setCriteriaEL(billingRuleDto.getCriteriaEL());
		billingRule.setInvoicedBACodeEL(billingRuleDto.getInvoicedBACodeEL());

		billingRuleService.update(billingRule);
		
		return billingRule;

	}

	private void checkBillingRulesRedirectionIsEnabled() {
		if (!billingRuleService.isBillingRedirectionRulesEnabled()) {
			throw new BusinessException("Feature disabled in application settings");
		}
	}

	@TransactionAttribute
	public void deleteBillingRule(String contractCode, Long idBillingRule) {

		checkBillingRulesRedirectionIsEnabled();

		// Check contract
		Contract contract = this.contractService.findByCode(contractCode);
		if(contract == null) {
			throw new EntityDoesNotExistsException("Contract doesn't exists");
		}

		BillingRule billingRule = this.billingRuleService.findById(idBillingRule);
		if(billingRule == null) {
			throw new EntityDoesNotExistsException("BillingRule doesn't exists");
		}

		billingRuleService.remove(billingRule);
	}

	/**
	 * Check mandatory fields : contractCode, criteriaEL, invoicedBACodeEL
	 * 
	 * @param contractCode
	 * @param billingRuleDto
	 */
	private void handleMissingParameters(String contractCode, BillingRuleDto billingRuleDto) {
		List<String> fields = new ArrayList<>();
		if(StringUtils.isBlank(contractCode)) {
			fields.add("contractCode");
		}

		if(StringUtils.isBlank(billingRuleDto.getCriteriaEL())) {
			fields.add("criteriaEL");
		}

		if(StringUtils.isBlank(billingRuleDto.getInvoicedBACodeEL())) {
			fields.add("invoicedBACodeEL");
		}

		if(!fields.isEmpty()) {
			throw new MissingParameterException(fields);
		}
	}

}
