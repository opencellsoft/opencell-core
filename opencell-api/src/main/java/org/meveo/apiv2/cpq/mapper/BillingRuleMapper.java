package org.meveo.apiv2.cpq.mapper;

import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.cpq.contract.BillingRule;

public class BillingRuleMapper extends ResourceMapper<BillingRuleDto, BillingRule> {

	@Override
	public BillingRuleDto toResource(BillingRule entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillingRule toEntity(BillingRuleDto billingRuleDto) {
		BillingRule billingRule = new BillingRule();
		
		billingRule.setPriority(billingRuleDto.getPriority());
		billingRule.setCriteriaEL(billingRuleDto.getCriteriaEL());
		billingRule.setInvoicedBACodeEL(billingRuleDto.getInvoicedBACodeEL());
		
		return billingRule;
	}

}
