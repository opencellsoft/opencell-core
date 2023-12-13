package org.meveo.apiv2.accountreceivable;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.meveo.model.payments.AccountOperationStatus.EXPORTED;
import static org.meveo.model.payments.AccountOperationStatus.POSTED;

import java.math.BigDecimal;
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
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.AcountReceivable.AccountOperationAndSequence;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.apiv2.AcountReceivable.LitigationInput;
import org.meveo.apiv2.AcountReceivable.UnMatchingAccountOperationDetail;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.payments.impl.*;
import org.meveo.service.securityDeposit.impl.SecurityDepositTransactionService;

public class AccountOperationApiService implements ApiService<AccountOperation> {

    @Inject
    protected ResourceBundle resourceMessages;
    
	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private MatchingCodeService matchingCodeService;

	@Inject
	private PaymentPlanService paymentPlanService;

	@Inject
	private SecurityDepositTransactionService securityDepositTransactionService;

	@Inject
	private RecordedInvoiceService recordedInvoiceService;

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
			accountOperationService.updateStatusInNewTransaction(statusGroups.get(true), status, null);
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
			if (accountOperation.getStatus() == EXPORTED) {
				accountOperation.setStatus(POSTED);
				// In this case, OperationNumber shall be incremented (https://opencellsoft.atlassian.net/browse/INTRD-7017)
				accountOperationService.fillOperationNumber(accountOperation);
			}

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
		List<AccountOperation> aos = new ArrayList<>();
		aoIds.forEach(aoId -> aos.add(accountOperationService.findById(aoId)));

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

		Set<String> unExpectedAoCodes = Stream.of("CRD_SD", "REF_SD")
				.collect(Collectors.toSet());

		if (!Collections.disjoint(unExpectedAoCodes, aoCodes)) {
			throw new BusinessApiException("Matching action is failed : AccountOperations passed for matching contains one of unexpected codes "
					+ unExpectedAoCodes);
		}

		// Update orphans AO by setting the same customerAccount
		org.meveo.model.payments.CustomerAccount customer = customerAccountService.findById(customerIds.get(0));
		if (customer == null) {
			throw new BusinessApiException("Matching action is failed : No CustomerAccount found with id " + customerIds.get(0) + " for matching");
		}
		
		aos.stream().forEach(accountOperation -> {
	          // check amount to match
	        Optional<AccountOperationAndSequence> accountOperationAndSequenceOptional = accountOperations.stream().filter(aoas -> aoas.getId().equals(accountOperation.getId())).findFirst();
			if (accountOperationAndSequenceOptional.isPresent()) {
				if (accountOperationAndSequenceOptional.get().getAmountToMatch() != null) {
					BigDecimal amountToMatch = accountOperationAndSequenceOptional.get().getAmountToMatch();
					Integer sequence = accountOperationAndSequenceOptional.get().getSequence();
					if (amountToMatch != null) {
						if (amountToMatch.compareTo(BigDecimal.ZERO) <= 0) {
							throw new BusinessApiException("The amount to match must be greater than 0");
						} else if (amountToMatch.compareTo(accountOperation.getUnMatchingAmount()) == 0) {
							throw new BusinessApiException("The amount to match must be less than : " + accountOperation.getUnMatchingAmount().doubleValue() + " for sequence : " + sequence);
						}
						accountOperation.setAmountForUnmatching(amountToMatch);
					}
				} else {
					accountOperation.setAmountForUnmatching(accountOperation.getUnMatchingAmount());
				}
			}
		});
		
		Optional.of(aos.stream().filter(accountOperation -> accountOperation.getCustomerAccount() == null)
						.collect(Collectors.toList())).orElse(Collections.emptyList())
				.forEach(accountOperation -> {
					accountOperation.setCustomerAccount(customer);
					// change status of orphan AO after CA assignement : new requirement added as bug https://opencellsoft.atlassian.net/browse/INTRD-8217
					accountOperation.setStatus(POSTED);
					accountOperationService.update(accountOperation);
				});

		try {
			// First AO is Credit, and shall be add with DEBIT to do unitary matching
			Long creditAoId = aos.stream().filter(ao -> OperationCategoryEnum.CREDIT == ao.getTransactionCategory()).findFirst()
					.orElseThrow(() -> new BusinessApiException("No credit AO passed for matching")).getId();

			MatchingReturnObject matchingResult = new MatchingReturnObject();
			List<PartialMatchingOccToSelect> partialMatchingOcc = new ArrayList<>();
			matchingResult.setPartialMatchingOcc(partialMatchingOcc);
			if (CollectionUtils.isNotEmpty(aos)) {
			    TradingCurrency theFirstTradingCurrency = aos.get(0).getTransactionalCurrency();
			    for (AccountOperation accountOperation : aos) {
			        if (!theFirstTradingCurrency.getId().equals(accountOperation.getTransactionalCurrency().getId())) {
	                    throw new BusinessApiException(resourceMessages.getString("accountOperation.error.sameCurrency"));
	                }
	                if (accountOperation.getId().equals(creditAoId)) {
	                    // process only DEBIT AO
	                    continue;
	                }
	                MatchingReturnObject unitaryResult = matchingCodeService.matchOperations(customer.getId(), customer.getCode(),
	                        List.of(creditAoId, accountOperation.getId()), accountOperation.getId(), accountOperation.getAmountForUnmatching());

	                if (matchingResult.getPartialMatchingOcc() != null) {
	                    partialMatchingOcc.addAll(matchingResult.getPartialMatchingOcc());
	                }

	                matchingResult.setOk(unitaryResult.isOk());
	            }
			}

			if (partialMatchingOcc.isEmpty()) {
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

			// update PaymentPlan
			List<Long> debitAos = new ArrayList<>(aoIds);
			debitAos.remove(creditAoId);
			paymentPlanService.toComplete(debitAos);

			return matchingResult;

		} catch (Exception e) {
			throw new BusinessApiException(e.getMessage());
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

		// Check if AO is already used at SecurityDepositTransaction : Can not unMatch AO used by the SecurityDeposit
		// Unitary check
		aoIds.forEach(id -> {
			List<String> securityDepositCodes = securityDepositTransactionService.getSecurityDepositCodesByAoIds(id);
			if (securityDepositCodes != null && !securityDepositCodes.isEmpty()) {
				throw new BusinessApiException("Unmatching action is failed : Cannot unmatch AO used by the SecurityDeposit codes: "
						+ new HashSet<>(securityDepositCodes));
			}
		});

		List<UnMatchingOperationRequestDto> toUnmatch = new ArrayList<>(aos.size());

		aos.forEach(ao -> {
			UnMatchingOperationRequestDto unM = new UnMatchingOperationRequestDto();
			unM.setAccountOperationId(ao.getId());

			UnMatchingAccountOperationDetail unMatchingAccountOperationDetail = accountOperations.stream()
					.filter(aoRequest -> ao.getId().equals(aoRequest.getId()))
					.findAny()
					.orElse(null);

			if (unMatchingAccountOperationDetail != null) {
				unM.setMatchingAmountIds(unMatchingAccountOperationDetail.getMatchingAmountIds());
			}
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

	/**
	 * @param accountOperationId recordedInvoice id.
	 * @param litigationInput litigation input.
	 * @return id of the updated recordedInvoice
	 */
	public Long setLitigation(Long accountOperationId, LitigationInput litigationInput) {
		RecordedInvoice recordedInvoice = ofNullable(recordedInvoiceService.findById(accountOperationId))
				.orElseThrow(() -> new NotFoundException("Account operation does not exits"));
		try {
			return recordedInvoiceService.setLitigation(recordedInvoice, litigationInput.getLitigationReason()).getId();
		} catch (BusinessException exception) {
			throw new BusinessApiException(exception.getMessage());
		}
	}
}
