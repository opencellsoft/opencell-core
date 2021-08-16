package org.meveo.apiv2.accounting.resource.impl;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounting.ImmutableAccountingPeriod;
import org.meveo.apiv2.accounting.resource.AccountingPeriodResource;
import org.meveo.apiv2.accounting.service.AccountingPeriodApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.accounting.AccountingPeriod;

public class AccountingPeriodResourceImpl implements AccountingPeriodResource {

	@Inject
	private AccountingPeriodApiService accountingPeriodApiService;

	private static final AccountingPeriodMapper accountingPeriodMapper = new AccountingPeriodMapper();

	@Override
	public Response create(org.meveo.apiv2.accounting.AccountingPeriod input) {
		AccountingPeriod accountingPeriodEntity = accountingPeriodApiService.create(accountingPeriodMapper.toEntity(input), input.getUseSubAccountingPeriods());
		return Response.created(LinkGenerator
				.getUriBuilderFromResource(AccountingPeriodResource.class, accountingPeriodEntity.getId()).build())
				.entity(toResourceAccountingPeriodWithLink(accountingPeriodMapper.toResource(accountingPeriodEntity)))
				.build();
	}
	private ImmutableAccountingPeriod toResourceAccountingPeriodWithLink(org.meveo.apiv2.accounting.AccountingPeriod accountingPeriod) {
		return ImmutableAccountingPeriod.copyOf(accountingPeriod)
				.withLinks(new LinkGenerator.SelfLinkGenerator(AccountingPeriodResource.class).withId(accountingPeriod.getId())
						.withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
	}
	
	@Override
	public Response update(Long id, org.meveo.apiv2.accounting.AccountingPeriod accountingPeriodResource) {
		final AccountingPeriod accountingPeriod = accountingPeriodApiService.findById(id).orElseThrow(NotFoundException::new);
		accountingPeriodApiService.update(id, accountingPeriodMapper.toEntity(accountingPeriod, accountingPeriodResource));
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, id).build())
                .build();
	}

}