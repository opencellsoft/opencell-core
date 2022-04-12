package org.meveo.apiv2.accountreceivable;

import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.apiv2.AcountReceivable.CustomerAccountInput;
import org.meveo.apiv2.AcountReceivable.MatchingAccountOperation;
import org.meveo.apiv2.AcountReceivable.UnMatchingAccountOperation;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodStatusEnum;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriodStatusEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.accounting.impl.AccountingPeriodService;
import org.meveo.service.accounting.impl.SubAccountingPeriodService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;

public class AccountReceivableResourceImpl implements AccountReceivableResource {
    @Inject
    private AccountOperationService accountOperationService;
    @Inject
    private AccountingPeriodService accountingPeriodService;
    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;

	@Inject
	private AccountOperationApiService accountOperationServiceApi;
	
	@Inject
    private AccountOperationApi accountOperationApi;

    @Override
    public Response post(Map<String, Set<Long>> accountOperations) {
        Set<Long> accountOperationsIds = accountOperations.getOrDefault("accountOperations", Collections.EMPTY_SET);
        Set<Long> notFoundAos = new HashSet(accountOperationsIds);
        Set<Long> aOWithClosedAccountingPeriod = new HashSet();
        Set<Long> aOWithSubClosedAccountingPeriod = new HashSet();
        accountOperationsIds.stream()
                .map(aOId -> accountOperationService.findById(aOId))
                .filter(accountOperation -> accountOperation != null)
                .forEach(accountOperation -> {
                    String fiscalYear = String.valueOf(DateUtils.getYearFromDate(accountOperation.getAccountingDate()));
                    AccountingPeriod accountingPeriod = accountingPeriodService.findByAccountingPeriodYear(fiscalYear);
                    if (accountingPeriod == null || accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED) {
                        aOWithClosedAccountingPeriod.add(accountOperation.getId());
                        return;
                    }
                    SubAccountingPeriod subAccountingPeriod = subAccountingPeriodService.findByAccountingPeriod(accountingPeriod, accountOperation.getAccountingDate());
                    if (subAccountingPeriod == null || subAccountingPeriod.getRegularUsersSubPeriodStatus() == SubAccountingPeriodStatusEnum.CLOSED) {
                        aOWithSubClosedAccountingPeriod.add(accountOperation.getId());
                        return;
                    }
                    accountOperation.setAccountingDate(accountOperation.getTransactionDate());
                    accountOperation.setStatus(AccountOperationStatus.POSTED);
                    accountOperationService.update(accountOperation);
                    notFoundAos.remove(accountOperation.getId());
                });

        if(!aOWithSubClosedAccountingPeriod.isEmpty()){
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"no open sub accounting period found for these Account operations: {" +
                    aOWithSubClosedAccountingPeriod.stream()
                    .map(aLong -> aLong.toString())
                    .collect(Collectors.joining(", "))+"} for the given fiscal year.\"}").build();
        }
        if(!aOWithClosedAccountingPeriod.isEmpty()){
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"no open accounting period found for these Account operations: {" +
                    aOWithClosedAccountingPeriod.stream()
                            .map(aLong -> aLong.toString())
                            .collect(Collectors.joining(", "))+"} for the given fiscal year.\"}").build();
        }
        if(!notFoundAos.isEmpty()){
            throw new EntityDoesNotExistsException("{\"message\":\"Following account operations does not exist : {" +
                    notFoundAos.stream()
                    .map(aLong -> aLong.toString())
                    .collect(Collectors.joining(", "))+"}.\"}");
        }
        return Response.ok().build();
    }

    @Override
    public Response forcePosting(Map<String, Set<Long>> accountOperations) {
        Set<Long> accountOperationsIds = accountOperations.getOrDefault("accountOperations", Collections.EMPTY_SET);
        Set<Long> notFoundAos = new HashSet<>();
        Set<Long> aOWithClosedAccountingPeriod = new HashSet<>();
		accountOperationsIds.stream().map(aOId -> {
			AccountOperation ao = accountOperationService.findById(aOId);
			if (ao == null) {
				notFoundAos.add(aOId);
			}
			return ao;
		}).filter(accountOperation -> accountOperation != null).forEach(accountOperation -> {
			String fiscalYear = String.valueOf(DateUtils.getYearFromDate(accountOperation.getAccountingDate()));
			AccountingPeriod accountingPeriod = accountingPeriodService.findByAccountingPeriodYear(fiscalYear);
			if (accountingPeriod == null || accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED) {
				aOWithClosedAccountingPeriod.add(accountOperation.getId());
				return;
			}
			accountOperationService.forceAccountOperation(accountOperation, accountingPeriod);
			accountOperationService.update(accountOperation);
	       });
        if(!aOWithClosedAccountingPeriod.isEmpty()){
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"no open accounting period found for these Account operations: {" +
                    aOWithClosedAccountingPeriod.stream()
                            .map(aLong -> aLong.toString())
                            .collect(Collectors.joining(", "))+"} for the given fiscal year.\"}").build();
        }
        if(!notFoundAos.isEmpty()){
            throw new EntityDoesNotExistsException("{\"message\":\"Following account operations does not exist :","{" + notFoundAos.stream()
                    .map(aLong -> aLong.toString())
                    .collect(Collectors.joining(", "))+"}.\"}");
        }
        return Response.ok().build();
    }

	@Override
	public Response markExported(ChangeStatusDto changeStatusDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountOperationServiceApi.changeStatus(changeStatusDto);
			return Response.ok(result).build();
		} catch (EntityDoesNotExistsException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).entity(result).build();
		} catch (ConflictException e) {
			result.setStatus(ActionStatusEnum.WARNING);
			result.setMessage(e.getMessage());
			return Response.status(Response.Status.CONFLICT).entity(result).build();
		}
	}

    @Override
    public Response assignAccountOperation(Long accountOperationId, CustomerAccountInput customerAccount) {
        if(customerAccount.getCustomerAccount() == null || (customerAccount.getCustomerAccount().getId() == null
                && customerAccount.getCustomerAccount().getCode() == null)) {
            return Response.status(PRECONDITION_FAILED)
                    .entity("{\"actionStatus\":{\"status\":\"FAILED\",\"message\":\"Missing customer account parameters\"}}")
                    .build();
        }
        return Response.ok()
                .entity(accountOperationServiceApi.assignAccountOperation(accountOperationId, customerAccount.getCustomerAccount()).get())
                .build();
    }

    @Override
    public Response matchOperations(MatchingAccountOperation matchingAO) {
        if (matchingAO == null ||
                matchingAO.getAccountOperations() == null || matchingAO.getAccountOperations().isEmpty()) {
        	return Response.status(Response.Status.BAD_REQUEST).entity("No accountOperations with sequence passed for matching").build();
        }
        try {
            MatchingReturnObject result = accountOperationServiceApi.matchOperations(matchingAO.getAccountOperations());
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (ElementNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Entity does not exist : " + e.getMessage()).build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Matching action is failed : " + e.getMessage()).build();
        }
    }
    
    @Override
    public Response unMatchOperations(UnMatchingAccountOperation unMatchingAO) {
        if (unMatchingAO == null ||
                unMatchingAO.getAccountOperations() == null || unMatchingAO.getAccountOperations().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No accountOperations passed for unmatching").build();
        }
        try {
            // Check Business rules
            List<UnMatchingOperationRequestDto> unmatchDtos = accountOperationServiceApi.validateAndGetAOForUnmatching(unMatchingAO.getAccountOperations());
            // for each AO, call AccountOperationApi.unMatchingOperations (dto)
            Optional.ofNullable(unmatchDtos).orElse(Collections.emptyList())
                    .forEach(unmatchDto -> {
                        // The dto can be created from AO.Id and AO.customerAccount.code
                        try {
                            accountOperationApi.unMatchingOperations(unmatchDto);
                        } catch (Exception e) {
                            throw new BusinessApiException(e);
                        }
                    });
            return Response.status(Response.Status.OK).build();
        } catch (ElementNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Entity does not exist : " + e.getMessage()).build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Matching action is failed : " + e.getMessage()).build();
        }
    }
}
