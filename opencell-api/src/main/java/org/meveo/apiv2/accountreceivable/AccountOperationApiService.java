package org.meveo.apiv2.accountreceivable;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.meveo.model.payments.AccountOperationStatus.POSTED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.AcountReceivable.AccountOperationAndSequence;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.apiv2.AcountReceivable.UnMatchingAccountOperationDetail;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.service.payments.impl.*;

public class AccountOperationApiService implements ApiService<AccountOperation> {

	@Inject
	private org.meveo.service.payments.impl.AccountOperationService accountOperationService;

	@Inject
	private MatchingCodeService matchingCodeService;
	@Inject
	private CustomerAccountService customerAccountService;
	

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


	public MatchingReturnObject matchOperations(List<AccountOperationAndSequence> accountOperations) {
		// Get and Sort AccountOperation by sequence (sort used for send order in matchingOperation service)
		List<Long> aoIds = accountOperations.stream()
				.sorted(Comparator.comparing(AccountOperationAndSequence::getSequence))
				.map(AccountOperationAndSequence::getId)
				.collect(Collectors.toList());

		// Check existence of all passed accountOperation
		List<AccountOperation> aos = accountOperationService.findByIds(aoIds);

		if (aoIds.size() != aos.size()) {
			throw new EntityDoesNotExistsException("One or more AccountOperations passed for matching are not found");
		}

		// Check that all AOs have the same customer account except for orphan AOs. if not throw an exception.
		List<Long> customerIds = aos.stream()
				.map(AccountOperation::getCustomerAccount)
				.filter(Objects::nonNull)
				.map(org.meveo.model.payments.CustomerAccount::getId)
				.collect(Collectors.toList());

		if (new HashSet<>(customerIds).size() > 1) {
			throw new BusinessApiException("Matching action is failed : AccountOperations passed for matching are linked to different CustomerAccount");
		}

		// Can not match AOs of  type SecurityDeposit(CRD_SD/DEB_SD/REF_SD)
		Set<String> aoCodes = aos.stream()
				.map(AccountOperation::getCode)
				.collect(Collectors.toSet());

		Set<String> unExpectedAoCodes = Stream.of("CRD_SD", "DEB_SD", "REF_SD")
				.collect(Collectors.toSet());

		if (!Collections.disjoint(unExpectedAoCodes, aoCodes)) {
			throw new BusinessApiException("Matching action is failed : AccountOperations passed for matching contains one of unexpected codes ["
					+ unExpectedAoCodes + "]");
		}

		// Update orphans AO by setting the same customerAccount
		org.meveo.model.payments.CustomerAccount customer = customerAccountService.findById(customerIds.get(0));
		if (customer == null) {
			throw new BusinessApiException("Matching action is failed : No CustomerAccount found with id " + customerIds.get(0) + " for matching");
		}

		Optional.of(aos.stream().filter(accountOperation -> accountOperation.getCustomerAccount() == null)
						.collect(Collectors.toList())).orElse(Collections.emptyList())
				.forEach(accountOperation -> {
					accountOperation.setCustomerAccount(customer);
					accountOperationService.update(accountOperation);
				});

		try {
			MatchingReturnObject matchingResult = matchingCodeService.matchOperations(customer.getId(), customer.getCode(),
					aoIds, aoIds.get(aoIds.size() - 1));

			if (matchingResult.getPartialMatchingOcc() == null || matchingResult.getPartialMatchingOcc().isEmpty()) {
				// Reload AO to get updated MatchingStatus
				List<AccountOperation> aoPartially = accountOperationService.findByIds(aoIds).stream()
						.filter(accountOperation -> accountOperation.getMatchingStatus() == MatchingStatusEnum.P)
						.collect(Collectors.toList());

				if (!aoPartially.isEmpty()) {
					PartialMatchingOccToSelect p = new PartialMatchingOccToSelect();
					p.setAccountOperation(aoPartially.get(0));
					p.setPartialMatchingAllowed(true);
					matchingResult.getPartialMatchingOcc().add(p);
				}
			}

			return matchingResult;

		} catch (Exception e) {
			throw new BusinessApiException("Matching action is failed : " + e.getMessage());
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
			throw new EntityDoesNotExistsException("One or more AccountOperations passed for unmatching are not found");
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
