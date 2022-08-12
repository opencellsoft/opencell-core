package org.meveo.apiv2.accounting.resource.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.accounting.ImmutableAccountingPeriod;
import org.meveo.apiv2.accounting.resource.AccountingPeriodResource;
import org.meveo.apiv2.accounting.service.AccountingPeriodApiService;
import org.meveo.apiv2.accounting.service.SubAccountingPeriodApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;

public class AccountingPeriodResourceImpl implements AccountingPeriodResource {

	@Inject
	private AccountingPeriodApiService accountingPeriodApiService;
	
	@Inject
	private SubAccountingPeriodApiService subAccountingPeriodApiService;

	private static final AccountingPeriodMapper accountingPeriodMapper = new AccountingPeriodMapper();

	@Override
	public Response create(org.meveo.apiv2.accounting.AccountingPeriod input) {
		checkRequiredParameters(input);
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
		checkRequiredParameters(accountingPeriodResource);
		final AccountingPeriod accountingPeriod = accountingPeriodApiService.findByFiscalYear(fiscalYear).orElseThrow(NotFoundException::new);
		AccountingPeriod newValue = accountingPeriodMapper.toEntity(accountingPeriodResource);
		accountingPeriodApiService.update(accountingPeriod, newValue);
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
	public Response updateAllUserStatus(String fiscalYear, String number, String status, String reason) {
		final SubAccountingPeriod subAccountingPeriod = subAccountingPeriodApiService.findByNumber(Integer.parseInt(number), fiscalYear).orElseThrow(NotFoundException::new);
		subAccountingPeriodApiService.updateSubAccountingAllUsersStatus(fiscalYear, status, subAccountingPeriod, reason);
		subAccountingPeriodApiService.update(subAccountingPeriod);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, fiscalYear, number, status).build())
                .build();
	}
	
	@Override
	public Response updateRegularUserStatus(String fiscalYear, String number, String status, String reason) {
		final SubAccountingPeriod subAccountingPeriod = subAccountingPeriodApiService.findByNumber(Integer.parseInt(number), fiscalYear).orElseThrow(NotFoundException::new);
		subAccountingPeriodApiService.updateSubAccountingRegularUsersStatus(fiscalYear, status, subAccountingPeriod, reason);
		subAccountingPeriodApiService.update(subAccountingPeriod);
		return Response.ok().entity(LinkGenerator.getUriBuilderFromResource(AccountingPeriodResource.class, fiscalYear, number, status).build())
                .build();
	}

	// Those checks are deprecated, regarding to the need of this issue : https://opencellsoft.atlassian.net/browse/INTRD-8245
	private void checkRequiredParameters(org.meveo.apiv2.accounting.AccountingPeriod entity) {
		if (entity.getUseSubAccountingPeriods() != null && entity.getUseSubAccountingPeriods()) {
			List<String> missingParameters = new ArrayList<>();
			if (entity.getRegularUserLockOption() == null) {
				missingParameters.add("regularUserLockOption");
			}
			if (entity.getAccountingOperationAction() == null) {
				missingParameters.add("accountingOperationAction");
			}
			if (!missingParameters.isEmpty()) {
				throw new MissingParameterException(missingParameters);
			}
		}
	}

}