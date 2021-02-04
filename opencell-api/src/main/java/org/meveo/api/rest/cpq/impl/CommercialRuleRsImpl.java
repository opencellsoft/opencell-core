package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.CommercialRuleApi;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialRuleDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialRulesResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductVersionsResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.CommercialRuleRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Mbarek-Ay
 **/

public class CommercialRuleRsImpl extends BaseRs implements CommercialRuleRs {

	@Inject
	private CommercialRuleApi commercialRuleApi;

	@Override
	public Response create(CommercialRuleHeaderDTO commercialRuleDTO) {
		GetCommercialRuleDtoResponse result = new GetCommercialRuleDtoResponse();
		try {
			result.setId(commercialRuleApi.create(commercialRuleDTO).getId());
			return Response.ok(result).build();
		} catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}


	@Override
	public Response update(CommercialRuleHeaderDTO commercialRuleDTO) {
		GetCommercialRuleDtoResponse result = new GetCommercialRuleDtoResponse();
		try {
			commercialRuleApi.update(commercialRuleDTO);
			return Response.ok(result).build();
		} catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response delete(String code) {
		GetCommercialRuleDtoResponse result = new GetCommercialRuleDtoResponse();
		try {
			commercialRuleApi.remove(code);
			return Response.ok(result).build();
		} catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response findByCode(String code) {
		GetCommercialRuleDtoResponse result = new GetCommercialRuleDtoResponse();
		try {
			result=commercialRuleApi.findByCode(code);
			return Response.ok(result).build();
		} catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	} 
	
	@Override
	public Response list(PagingAndFiltering pagingAndFiltering) {
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		try {  
			result = commercialRuleApi.list(pagingAndFiltering);
			return Response.ok(result).build(); 
		} catch (MeveoApiException e) { 
			return errorResponse(e, result.getActionStatus());
		} 
	}

	@Override
	public Response findProductRules(String offerCode, String productCode, Integer productVersion) {
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		try { 
			result=commercialRuleApi.findProductRules(offerCode, productCode, productVersion);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		} 
	}

	@Override
	public Response findAttributeRules(String productCode, String attributeCode) {
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		try { 
			result=commercialRuleApi.findAttributeRules(productCode, attributeCode);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		} 
	}

	@Override
	public Response findTagRules(String tagCode) {
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		try { 
			result=commercialRuleApi.findTagRules(tagCode);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		} 
	}
	
	@Override
	public Response findOfferRules(String offerCode) {
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		try { 
			result=commercialRuleApi.findOfferRules(offerCode);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		} 
	}

	 
}
