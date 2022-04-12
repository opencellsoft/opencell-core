package org.meveo.apiv2.accountreceivable;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.meveo.model.payments.AccountOperationStatus.POSTED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.apiv2.AcountReceivable.UnMatchingAccountOperationDetail;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.service.payments.impl.*;
import org.meveo.service.securityDeposit.impl.SecurityDepositTransactionService;

public class AccountOperationApiService implements ApiService<AccountOperation> {

	@Inject
	private org.meveo.service.payments.impl.AccountOperationService accountOperationService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private SecurityDepositTransactionService securityDepositTransactionService;

	@Override
	public List<AccountOperation> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getCount(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountOperation> findById(Long id) {
		return ofNullable(accountOperationService.findById(id));
	}

	@Override
	public AccountOperation create(AccountOperation baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountOperation> update(Long id, AccountOperation baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountOperation> patch(Long id, AccountOperation baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountOperation> delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountOperation> findByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void changeStatus(ChangeStatusDto changeStatusDto) {
		List<Long> ids = changeStatusDto.getAccountOperations();
		AccountOperationStatus status = changeStatusDto.getStatus();
		ids = new ArrayList<>(new TreeSet<>(ids));// removeDuplicates

		List<AccountOperation> accountOperations = accountOperationService.findByIds(ids);
		if (accountOperations == null || ids.size() > accountOperations.size()) {
			List<Long> dbIds = accountOperations.stream().map(ao -> ao.getId()).collect(Collectors.toList());
			List<Long> wrongIds = ids.stream().filter(id -> !dbIds.contains(id)).collect(Collectors.toList());
			throw new EntityDoesNotExistsException("AccountOperation", wrongIds);
		}
		if (AccountOperationStatus.EXPORTED.equals(status)) {
			Map<Boolean, List<AccountOperation>> statusGroups = accountOperations.stream()
					.collect(Collectors.partitioningBy(ao -> POSTED.equals(ao.getStatus())));
			accountOperationService.updateStatusInNewTransaction(statusGroups.get(true), status);
			if (!CollectionUtils.isEmpty(statusGroups.get(false))) {
				throw new ConflictException("The status of following account operations can not be updated: "
						+ statusGroups.get(false).stream().map(ao -> ao.getId() + ", ").reduce("", String::concat));
			}
		} else {
			throw new ConflictException("not possible to change accountOperation status to '" + status + "'");
		}
	}

	public Optional<AccountOperation> assignAccountOperation(Long accountOperationId,
																					 CustomerAccount customerAccountInput) {
		AccountOperation accountOperation = accountOperationService.findById(accountOperationId);
		if(accountOperation == null) {
			throw new NotFoundException("Account operation does not exits");
		} else {
			org.meveo.model.payments.CustomerAccount customerAccount = getCustomerAccount(customerAccountInput);
			if(customerAccount == null) {
				throw new NotFoundException("Customer account does not exits");
			}
			accountOperation.setCustomerAccount(customerAccount);
			accountOperation.setStatus(POSTED);
			try {
				accountOperationService.update(accountOperation);
			} catch (Exception exception) {
				throw new BadRequestException(exception.getMessage());
			}
			return of(accountOperation);
		}
	}

	public List<UnMatchingOperationRequestDto> validateAndGetAOForUnmatching(List<UnMatchingAccountOperationDetail> accountOperations){
		// Get AccountOperation
		List<Long> aoIds = accountOperations.stream()
				.map(UnMatchingAccountOperationDetail::getId)
				.collect(Collectors.toList());

		// Check existence of all passed accountOperation
		List<AccountOperation> aos = accountOperationService.findByIds(aoIds);

		if (aoIds.size() != aos.size()) {
			throw new ElementNotFoundException("One or more AccountOperations passed for unmatching are not found");
		}

		// Check if AO is already used at SecurityDepositTransaction : Can not unMatch AOs of type SecurityDeposit
		if (securityDepositTransactionService.checkExistanceByAoIds(aoIds)) {
			throw new BusinessApiException("Can not unMatch AOs of type SecurityDeposit");
		}

		List<UnMatchingOperationRequestDto> toUnmatch = new ArrayList<>(aos.size());

		aos.forEach(ao -> {
			UnMatchingOperationRequestDto unM = new UnMatchingOperationRequestDto();
			unM.setAccountOperationId(ao.getId());
			unM.setCustomerAccountCode(customerAccountService.findById(ao.getCustomerAccount().getId()).getCode());

			toUnmatch.add(unM);
		});

		return toUnmatch;

	}

	private org.meveo.model.payments.CustomerAccount getCustomerAccount(CustomerAccount customerAccountInput) {
		org.meveo.model.payments.CustomerAccount customerAccount = null;
		if(customerAccountInput.getId() != null) {
			customerAccount = customerAccountService.findById(customerAccountInput.getId());
		}
		if(customerAccountInput.getCode() != null && customerAccount == null) {
			customerAccount = customerAccountService.findByCode(customerAccountInput.getCode());
		}
		return customerAccount;
	}
}
