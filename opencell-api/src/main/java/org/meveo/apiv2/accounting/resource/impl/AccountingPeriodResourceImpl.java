package org.meveo.apiv2.accounting.resource.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounting.ImmutableAccountingPeriod;
import org.meveo.apiv2.accounting.resource.AccountingPeriodResource;
import org.meveo.apiv2.accounting.service.AccountingPeriodApiService;
import org.meveo.apiv2.accounting.service.SubAccountingPeriodApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriodStatusEnum;

public class AccountingPeriodResourceImpl implements AccountingPeriodResource {

	@Inject
	private AccountingPeriodApiService accountingPeriodApiService;
	
	@Inject
	private SubAccountingPeriodApiService subAccountingPeriodApiService;

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
	public Response update(String fiscalYear, org.meveo.apiv2.accounting.AccountingPeriod accountingPeriodResource) {
		final AccountingPeriod accountingPeriod = accountingPeriodApiService.findByFiscalYear(fiscalYear).orElseThrow(NotFoundException::new);
		accountingPeriodApiService.update(accountingPeriodMapper.toEntity(accountingPeriod, accountingPeriodResource));
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, fiscalYear).build())
                .build();
	}
	@Override
	public Response generateNextAP() {
		final AccountingPeriod accountingPeriod = accountingPeriodApiService.generateNextAP().orElseThrow(NotFoundException::new);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, accountingPeriod.getAccountingPeriodYear()).build())
                .build();
	}
	@Override
	public Response updateAllUserStatus(String fiscalYear, String number, String status) {
		final SubAccountingPeriod subAccountingPeriod = subAccountingPeriodApiService.findByNumber(Integer.parseInt(number)).orElseThrow(NotFoundException::new);
		subAccountingPeriodApiService.updateSubAccountingAllUsersStatus(fiscalYear, status, subAccountingPeriod);
		subAccountingPeriodApiService.update(subAccountingPeriod);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, fiscalYear, number, status).build())
                .build();
	}
	
	@Override
	public Response updateRegularUserStatus(String fiscalYear, String number, String status) {
		final SubAccountingPeriod subAccountingPeriod = subAccountingPeriodApiService.findByNumber(Integer.parseInt(number)).orElseThrow(NotFoundException::new);
		subAccountingPeriodApiService.updateSubAccountingRegularUsersStatus(fiscalYear, status, subAccountingPeriod);
		subAccountingPeriodApiService.update(subAccountingPeriod);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, fiscalYear, number, status).build())
                .build();
	}
	
}