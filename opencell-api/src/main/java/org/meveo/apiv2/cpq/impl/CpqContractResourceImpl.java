package org.meveo.apiv2.cpq.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.apiv2.cpq.resource.CpqContractResource;
import org.meveo.apiv2.cpq.service.CpqContractApiService;
import org.meveo.apiv2.models.ImmutableApiException;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.cpq.contract.BillingRule;

@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class CpqContractResourceImpl implements CpqContractResource {

	@Inject
	private CpqContractApiService contractApiService;
	
	@Override
	public Response createBillingRule(String contractCode, BillingRuleDto billingRuleDto) {
		
		try {
			BillingRule createdBR = contractApiService.createBillingRule(contractCode, billingRuleDto);
			
			ActionStatus responseStatus = new ActionStatus();
			responseStatus.setStatus(ActionStatusEnum.SUCCESS);
			responseStatus.setEntityId(createdBR.getId());

			return Response.ok(responseStatus).build();
		} catch (MissingParameterException e) {
			ImmutableApiException exceptionResponse = ImmutableApiException.builder()
																            .status(Response.Status.PRECONDITION_FAILED)
																            .details(e.getMessage())
																            .build();
			return Response.status(Response.Status.PRECONDITION_FAILED).entity(exceptionResponse)
	                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
	                .build();
		}
	}

	@Override
	public Response updateBillingRule(@NotNull String contractCode, @NotNull Long billingRuleId, BillingRuleDto billingRuleDto) {
		try {
			BillingRule createdBR = contractApiService.updateBillingRule(contractCode, billingRuleId, billingRuleDto);
			
			ActionStatus responseStatus = new ActionStatus();
			responseStatus.setStatus(ActionStatusEnum.SUCCESS);
			responseStatus.setEntityId(createdBR.getId());

			return Response.ok(responseStatus).build();
		} catch (MissingParameterException e) {
			ImmutableApiException exceptionResponse = ImmutableApiException.builder()
																            .status(Response.Status.PRECONDITION_FAILED)
																            .details(e.getMessage())
																            .build();
			return Response.status(Response.Status.PRECONDITION_FAILED).entity(exceptionResponse)
	                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
	                .build();
		}
	}

	@Override
	public Response deleteBillingRule(@NotNull String contractCode, @NotNull Long billingRuleId) {
		contractApiService.deleteBillingRule(contractCode, billingRuleId);
		
		ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		
		return Response.ok(responseStatus).build(); 
	}
	
}
