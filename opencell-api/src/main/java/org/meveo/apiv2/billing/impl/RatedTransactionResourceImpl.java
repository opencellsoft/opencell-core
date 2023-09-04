package org.meveo.apiv2.billing.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.apiv2.billing.DuplicateRTDto;
import org.meveo.apiv2.billing.RatedTransactionInput;
import org.meveo.apiv2.billing.resource.RatedTransactionResource;
import org.meveo.apiv2.billing.service.RatedTransactionApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;

public class RatedTransactionResourceImpl implements RatedTransactionResource {

	@Inject
	private RatedTransactionApiService ratedTransactionApiService;

	private final RatedTransactionMapper mapper = new RatedTransactionMapper();
	
	@Override
	public Response createRatedTransaction(RatedTransactionInput input) {
		RatedTransaction ratedTransactionEntity = ratedTransactionApiService.create(input);
		final URI result = LinkGenerator
				.getUriBuilderFromResource(RatedTransactionResource.class, ratedTransactionEntity.getId()).build();
		return Response.created(result).entity(ratedTransactionEntity.getId())
				.build();
	}

	@Override
	public Response updateRatedTransaction(Long id, RatedTransactionInput input) {
		final RatedTransaction ratedTransaction = findRatedTransactionEligibleToUpdate(id);
		ratedTransactionApiService.update(ratedTransaction, input.getDescription(), input.getUnitAmountWithoutTax(), input.getQuantity(), input.getParameter1(),
				input.getParameter2(), input.getParameter3(), input.getParameterExtra());

		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(RatedTransactionResource.class, id).build())
				.build();
	}
	
	private RatedTransaction findRatedTransactionEligibleToUpdate(Long id) {
		RatedTransaction ratedTransaction = ratedTransactionApiService.findById(id).orElseThrow(NotFoundException::new);
		if(!RatedTransactionStatusEnum.OPEN.equals(ratedTransaction.getStatus())) {
			throw new ActionForbiddenException("Can only edit ratedTransaction in statuses OPEN. current ratedTransaction status is :"+ratedTransaction.getStatus().name()) ;
		}
		return ratedTransaction;
	}
	
	@Override
	public Response cancel(Long id) {
		findRatedTransactionEligibleToUpdate(id);
		ratedTransactionApiService.cancelRatedTransaction(id);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(RatedTransactionResource.class, id).build())
				.build();
	}

	@Override
	public Response find(String code, Request request) {
		RatedTransaction ratedTransaction = ratedTransactionApiService.findByCode(code)
				.orElseThrow(NotFoundException::new);
		return Response.ok().entity(mapper.toResource(ratedTransaction)).build();
	}

	@Override
	public Response duplication(DuplicateRTDto duplicateRTDto) {
		return Response.ok().entity(ratedTransactionApiService.duplication(duplicateRTDto.getFilters(), duplicateRTDto.getMode(), duplicateRTDto.getNegateAmount(), duplicateRTDto.getReturnRts())).build();
	}
}