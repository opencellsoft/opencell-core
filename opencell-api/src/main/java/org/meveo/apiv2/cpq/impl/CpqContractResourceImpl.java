package org.meveo.apiv2.cpq.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.apiv2.cpq.resource.CpqContractResource;
import org.meveo.apiv2.cpq.service.CpqContractApiService;
import org.meveo.service.cpq.ContractService;

@Stateless
public class CpqContractResourceImpl implements CpqContractResource {

	@Inject
	private CpqContractApiService contractApiService;
	
	@Override
	public Response createBillingRule(String contractCode, BillingRuleDto billingRuleDto) {
		
		try {
			contractApiService.createBillingRule(contractCode, billingRuleDto);
		} catch (Exception e) {
			
		}
		
		return null;
	}

	@Override
	public Response updateBillingRule(@NotNull String contractCode, @NotNull String billingRuleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteBillingRule(@NotNull String contractCode, @NotNull String billingRuleId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
