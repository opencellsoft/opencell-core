package org.meveo.apiv2.accountreceivable;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;

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
					.collect(Collectors.partitioningBy(ao -> AccountOperationStatus.POSTED.equals(ao.getStatus())));
			accountOperationService.updateStatusInNewTransaction(statusGroups.get(true), status);
			if (!CollectionUtils.isEmpty(statusGroups.get(false))) {
				throw new ConflictException("The status of following account operations can not be updated: "
						+ statusGroups.get(false).stream().map(ao -> ao.getId() + ", ").reduce("", String::concat));
			}
		} else {
			throw new ConflictException("not possible to change accountOperation status to '" + status + "'");
		}
	}

	public MatchingReturnObject matchOperations(Map<Integer, Long> accountOperations) {
		// Check existence of all passed accountOperation
		List<Long> aoIds = new ArrayList<>(accountOperations.values());
		List<AccountOperation> aos = accountOperationService.findByIds(aoIds);

		if (aoIds.size() != aos.size()) {
			throw new ElementNotFoundException("One or more AccountOperations passed for matching are not found");
		}

		// Check that all AOs have the same customer account except for orphan AOs. if not throw an exception.
		Set<Long> customerIds = aos.stream()
				.map(AccountOperation::getCustomerAccount).map(CustomerAccount::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		if (customerIds.size() > 1) {
			throw new BusinessException("AccountOperations passed for matching are linked to different CustomerAccount");
		}

		// Can not match AOs of  type SecurityDeposit(CRD_SD/DEB_SD/REF_SD)
		Set<String> aoCodes = aos.stream()
				.map(AccountOperation::getCode)
				//.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<String> unExpectedAoCodes = Stream.of("CRD_SD", "DEB_SD", "REF_SD")
				.collect(Collectors.toSet());

		if (!Collections.disjoint(unExpectedAoCodes, aoCodes)) {
			throw new BusinessException("AccountOperations passed for matching contains one of unexpected codes ["
					+ unExpectedAoCodes + "]");
		}

		// Update orphans AO by setting the same customerAccount
		Optional<AccountOperation> ao = aos.stream()
				.filter(accountOperation -> accountOperation.getCustomerAccount() != null)
				.findAny();
//
//		CustomerAccount customer = ao.map(AccountOperation::getCustomerAccount)
//				.orElseThrow(() -> new BusinessException("No CustomerAccount for matching"));
		CustomerAccount customer = customerAccountService.findById(ao.map(AccountOperation::getCustomerAccount)
				.orElseThrow(() -> new BusinessException("No CustomerAccount for matching")).getId());

		Optional.of(aos.stream().filter(accountOperation -> accountOperation.getCustomerAccount() == null)
						.collect(Collectors.toList())).orElse(Collections.emptyList())
				.forEach(accountOperation -> {
					accountOperation.setCustomerAccount(customer);
					accountOperationService.update(accountOperation);
				});

		// Sort request
		Map<Integer, Long> sortedAos = accountOperations.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		List<Long> toMatchAoIds = new ArrayList<>(sortedAos.values());

		try {
			return matchingCodeService.matchOperations(customer.getId(), customer.getCode(),
					toMatchAoIds, toMatchAoIds.get(toMatchAoIds.size() - 1));
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

}
