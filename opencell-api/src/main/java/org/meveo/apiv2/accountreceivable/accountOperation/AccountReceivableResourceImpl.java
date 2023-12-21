package org.meveo.apiv2.accountreceivable.accountOperation;

import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.ok;
import static org.meveo.api.dto.ActionStatusEnum.FAIL;
import static org.meveo.api.MeveoApiErrorCodeEnum.DIFFERENT_CURRENCIES;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.apiv2.AcountReceivable.*;
import org.meveo.apiv2.accountreceivable.AccountOperationApiService;
import org.meveo.apiv2.accountreceivable.ChangeStatusDto;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.accounting.AccountingOperationAction;
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

@Interceptors({ WsRestApiInterceptor.class })
public class AccountReceivableResourceImpl implements AccountReceivableResource {

    @Inject
    protected ResourceBundle resourceMessages;
    
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
        Set<Long> aONotHaveTransactionDate = new HashSet();
        accountOperationsIds.stream()
                .map(aOId -> accountOperationService.findById(aOId))
                .filter(accountOperation -> accountOperation != null)
                .forEach(accountOperation -> {
                    Date accountingDate = accountOperationService.getAccountingDate(accountOperation);
                    if (accountingDate == null) {
                        aONotHaveTransactionDate.add(accountOperation.getId());
                        return;
                    }
                    String fiscalYear = String.valueOf(DateUtils.getYearFromDate(accountingDate));
                    AccountingPeriod accountingPeriod = accountingPeriodService.findByAccountingPeriodYear(fiscalYear);
                    if (accountingPeriod == null || accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED) {
                        aOWithClosedAccountingPeriod.add(accountOperation.getId());
                        return;
                    }
                    SubAccountingPeriod subAccountingPeriod = subAccountingPeriodService.findByAccountingPeriod(accountingPeriod, accountingDate);
                    if (subAccountingPeriod == null || subAccountingPeriod.getRegularUsersSubPeriodStatus() == SubAccountingPeriodStatusEnum.CLOSED) {
                        aOWithSubClosedAccountingPeriod.add(accountOperation.getId());
                        return;
                    }
                    accountOperation.setAccountingDate(accountingDate);
                    accountOperation.setStatus(AccountOperationStatus.POSTED);
                    accountOperationService.update(accountOperation);
                    notFoundAos.remove(accountOperation.getId());
                });

        if (!aONotHaveTransactionDate.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"no transaction date found for these Account operations: {" +
                    aONotHaveTransactionDate.stream()
                            .map(aLong -> aLong.toString())
                            .collect(Collectors.joining(", ")) + "} for the given fiscal year.\"}").build();
        }
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
        Set<Long> aONotHaveTransactionDate = new HashSet();
		accountOperationsIds.stream().map(aOId -> {
			AccountOperation ao = accountOperationService.findById(aOId);
			if (ao == null) {
				notFoundAos.add(aOId);
			}
			return ao;
		}).filter(accountOperation -> accountOperation != null).forEach(accountOperation -> {
            Date accountingDate = accountOperationService.getAccountingDate(accountOperation);
            if (accountingDate == null) {
                aONotHaveTransactionDate.add(accountOperation.getId());
                return;
            }
            String fiscalYear = String.valueOf(DateUtils.getYearFromDate(accountingDate));
            AccountingPeriod accountingPeriod = accountingPeriodService.findByAccountingPeriodYear(fiscalYear);
            if (accountingPeriod == null || (accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED &&
                    accountingPeriod.getAccountingOperationAction() != AccountingOperationAction.FORCE)) {
                aOWithClosedAccountingPeriod.add(accountOperation.getId());
                return;
            } else if (accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED) {
                accountingPeriod = accountingPeriodService.findOpenAccountingPeriod();
                if (accountingPeriod == null) {
                    aOWithClosedAccountingPeriod.add(accountOperation.getId());
                    return;
                }
                accountingDate = accountingPeriod.getStartDate();
            }

            accountOperation.setAccountingDate(accountingDate);
			accountOperationService.forceAccountOperation(accountOperation, accountingPeriod);
			accountOperationService.update(accountOperation);
	       });
        if (!aONotHaveTransactionDate.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity("{\"message\":\"no transaction date found for these Account operations: {" +
                    aONotHaveTransactionDate.stream()
                            .map(aLong -> aLong.toString())
                            .collect(Collectors.joining(", ")) + "} for the given fiscal year.\"}").build();
        }
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
        MatchingReturnObject result;
        try{
            result = accountOperationServiceApi.matchOperations(matchingAO.getAccountOperations());
        }
        catch(BusinessApiException e) {
            ActionStatus resultActionStatus = new ActionStatus();
            resultActionStatus.setStatus(FAIL);
            resultActionStatus.setMessage(e.getMessage());
            if (resourceMessages.getString("accountOperation.error.sameCurrency").equals(e.getMessage())) {
                resultActionStatus.setErrorCode(DIFFERENT_CURRENCIES);
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(resultActionStatus).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @Override
    public Response unMatchOperations(UnMatchingAccountOperation unMatchingAO) {
        if (unMatchingAO == null ||
                unMatchingAO.getAccountOperations() == null || unMatchingAO.getAccountOperations().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No accountOperations passed for unmatching").build();
        }

        // Check Business rules
        List<UnMatchingOperationRequestDto> unmatchDtos = accountOperationServiceApi.validateAndGetAOForUnmatching(unMatchingAO.getAccountOperations());

        // for each AO, call AccountOperationApi.unMatchingOperations (dto)
        Optional.ofNullable(unmatchDtos).orElse(Collections.emptyList())
                .forEach(unmatchDto -> {
                    // The dto can be created from AO.Id and AO.customerAccount.code
                    try {
                        accountOperationApi.unMatchingOperations(unmatchDto);
                    } catch (Exception e) {
                        throw new BusinessApiException(e.getMessage());
                    }

                });

        return Response.status(Response.Status.OK).build();

    }

    @Override
    public Response setLitigation(Long accountOperationId, LitigationInput litigationInput) {
        final Long accountOperationID = accountOperationServiceApi.setLitigation(accountOperationId, litigationInput);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"," +
                        "\"message\":\"Account operation successfully updated\"},\"id\":" + accountOperationID + "}")
                .build();
    }

    @Override
    public Response removeLitigation(Long accountOperationId, LitigationInput litigationInput) {
        final Long accountOperationID = accountOperationServiceApi.removeLitigation(accountOperationId, litigationInput);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"," +
                        "\"message\":\"litigation successfully removed\"},\"id\":" + accountOperationID + "}")
                .build();
    }
}
