/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.AccountOperationsDto;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentHistoryDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentHistoryService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @author melyoussoufi
 * @lastModifiedVersion 10.0
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class PaymentApi extends BaseApi {

    private static final String DEFAULT_SORT_ORDER_ID = "id";
    
    @Inject
    private PaymentService paymentService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    /**
     * @param paymentDto payment object which encapsulates the input data sent by client
     * @return the id of payment if created successful otherwise null
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException balance amount exception
     * @throws BusinessException business exception
     * @throws MeveoApiException opencell api exception
     */
    public Long createPayment(PaymentDto paymentDto) throws NoAllOperationUnmatchedException, UnbalanceAmountException, BusinessException, MeveoApiException {
        log.info("create payment for amount:" + paymentDto.getAmount() + " paymentMethodEnum:" + paymentDto.getPaymentMethod() + " isToMatching:" + paymentDto.isToMatching()
                + "  customerAccount:" + paymentDto.getCustomerAccountCode() + "...");

        if (StringUtils.isBlank(paymentDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (StringUtils.isBlank(paymentDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(paymentDto.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }
        if (StringUtils.isBlank(paymentDto.getReference())) {
            missingParameters.add("reference");
        }
        if (StringUtils.isBlank(paymentDto.getPaymentMethod())) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();
        CustomerAccount customerAccount = customerAccountService.findByCode(paymentDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + paymentDto.getCustomerAccountCode());
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(paymentDto.getOccTemplateCode());
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + paymentDto.getOccTemplateCode());
        }

        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setAmount(paymentDto.getAmount());
        payment.setUnMatchingAmount(paymentDto.getAmount());
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(StringUtils.isBlank(paymentDto.getDescription()) ? occTemplate.getDescription() : paymentDto.getDescription());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(paymentDto.getReference());
        payment.setDueDate(paymentDto.getDueDate() == null ? new Date()  : paymentDto.getDueDate());
        payment.setTransactionDate(paymentDto.getTransactionDate() == null ? new Date()  : paymentDto.getTransactionDate());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setPaymentOrder(paymentDto.getPaymentOrder());
        payment.setFees(paymentDto.getFees());
        payment.setComment(paymentDto.getComment());
        payment.setBankLot(paymentDto.getBankLot());
        payment.setPaymentInfo(paymentDto.getPaymentInfo());
        payment.setPaymentInfo1(paymentDto.getPaymentInfo1());
        payment.setPaymentInfo2(paymentDto.getPaymentInfo2());
        payment.setPaymentInfo3(paymentDto.getPaymentInfo3());
        payment.setPaymentInfo4(paymentDto.getPaymentInfo4());
        payment.setPaymentInfo5(paymentDto.getPaymentInfo5());
        payment.setPaymentInfo6(paymentDto.getPaymentInfo6());

        // populate customFields
        try {
            populateCustomFields(paymentDto.getCustomFields(), payment, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        paymentService.create(payment);

        if (paymentDto.isToMatching()) {
            matchPayment(paymentDto, customerAccount, payment);
            paymentHistoryService.addHistory(customerAccount,
            		payment,
    				null, paymentDto.getAmount().multiply(new BigDecimal(100)).longValue(),
    				PaymentStatusEnum.ACCEPTED, null, null, null, null,
    				null,null,paymentDto.getListAoIdsForMatching());            
        } else {
            log.info("no matching created ");
        }
        log.debug("payment created for amount:" + payment.getAmount());

        return payment.getId();

    }

	private void matchPayment(PaymentDto paymentDto, CustomerAccount customerAccount, Payment payment)
			throws BusinessApiException, BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		List<Long> listReferenceToMatch = new ArrayList<Long>();
		if (paymentDto.getListAoIdsForMatching()!=null && !paymentDto.getListAoIdsForMatching().isEmpty() ) {
			listReferenceToMatch.addAll(paymentDto.getListAoIdsForMatching());
		} else if (paymentDto.getListOCCReferenceforMatching() != null) {
		    for (String Reference: paymentDto.getListOCCReferenceforMatching()) {
		        List<RecordedInvoice> accountOperationToMatch = recordedInvoiceService.getRecordedInvoice(Reference);
		        if (accountOperationToMatch == null || accountOperationToMatch.isEmpty()) {
		            throw new BusinessApiException("Cannot find account operation with reference:" + Reference );
		        } else if (accountOperationToMatch.size() > 1) {
		            throw new BusinessApiException("More than one account operation with reference:" + Reference +". Please use ListAoIdsForMatching instead of ListOCCReferenceforMatching");
		        }
		        listReferenceToMatch.add(accountOperationToMatch.get(0).getId());
		    }
		}
		List<AccountOperation> aosToPaid = new ArrayList<AccountOperation>();
		for(Long id : listReferenceToMatch ) {
			AccountOperation ao = accountOperationService.findById(id);
			if(ao == null) {
				 throw new BusinessApiException("Cannot find account operation with id:" + id );
			}
			aosToPaid.add(ao);
		}
		 Collections.sort(aosToPaid, Comparator.comparing(AccountOperation::getDueDate));

		for(AccountOperation ao :aosToPaid ) {			
			if(BigDecimal.ZERO.compareTo(payment.getUnMatchingAmount()) == 0) {
				break;
			}
			List<Long> aosIdsToMatch = new ArrayList<Long>();
			aosIdsToMatch.add(ao.getId());
			aosIdsToMatch.add(payment.getId());
			matchingCodeService.matchOperations(null, customerAccount.getCode(), aosIdsToMatch, null, MatchingTypeEnum.A);			
		}
	}


	/**
	 * Get payment list by customer account code
	 * 
	 * @param customerAccountCode customer account code
	 * @param pagingAndFiltering
	 * @return list of payment dto
	 * @throws Exception exception.
	 * @author akadid abdelmounaim
	 * @lastModifiedVersion 5.0
	 */
	public CustomerPaymentsResponse getPaymentList(String customerAccountCode, PagingAndFiltering pagingAndFiltering) throws Exception {

		CustomerPaymentsResponse result = new CustomerPaymentsResponse();
		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
		}

		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}
		PaginationConfiguration paginationConfiguration = preparePaginationConfiguration(pagingAndFiltering, customerAccount);

		List<AccountOperation> ops = accountOperationService.list(paginationConfiguration);
		Long total = accountOperationService.count(paginationConfiguration);

		// Remove the filters added by preparePaginationConfiguration function
		pagingAndFiltering.getFilters().remove("type_class");
		pagingAndFiltering.getFilters().remove("customerAccount");

		pagingAndFiltering.setTotalNumberOfRecords(total.intValue());
		result.setPaging(pagingAndFiltering);

		for (AccountOperation op : ops) {
			if (op instanceof Payment) {
				Payment p = (Payment) op;
				PaymentDto paymentDto = new PaymentDto();
				paymentDto.setType(p.getType());
				paymentDto.setAmount(p.getAmount());
				paymentDto.setDueDate(p.getDueDate());
				paymentDto.setOccTemplateCode(p.getCode());
				paymentDto.setPaymentMethod(p.getPaymentMethod());
				paymentDto.setReference(p.getReference());
				paymentDto.setTransactionDate(p.getTransactionDate());
				paymentDto.setPaymentOrder(p.getPaymentOrder());
				paymentDto.setFees(p.getFees());
				paymentDto.setComment(p.getComment());
				paymentDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(op, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
				if (p instanceof AutomatedPayment) {
					AutomatedPayment ap = (AutomatedPayment) p;
					paymentDto.setBankCollectionDate(ap.getBankCollectionDate());
					paymentDto.setBankLot(ap.getBankLot());
					paymentDto.setDepositDate(ap.getDepositDate());
				}
				result.addPaymentDto(paymentDto);
			} else if (op instanceof OtherCreditAndCharge) {
				OtherCreditAndCharge occ = (OtherCreditAndCharge) op;
				PaymentDto paymentDto = new PaymentDto();
				paymentDto.setType(occ.getType());
				paymentDto.setDescription(op.getDescription());
				paymentDto.setAmount(occ.getAmount());
				paymentDto.setDueDate(occ.getDueDate());
				paymentDto.setOccTemplateCode(occ.getCode());
				paymentDto.setReference(occ.getReference());
				paymentDto.setTransactionDate(occ.getTransactionDate());
				result.addPaymentDto(paymentDto);
			}
		}
		return result;
	}

	/**
	 * Prepare paginationConfiguration to get only Payment and OtherCreditAndCharge
	 * operations related to the customerAccount
	 * 
	 * @param pagingAndFiltering
	 * @param customerAccount
	 * @return
	 * @throws Exception
	 */
	private PaginationConfiguration preparePaginationConfiguration(PagingAndFiltering pagingAndFiltering, CustomerAccount customerAccount) throws Exception {

		PaginationConfiguration paginationConfiguration = toPaginationConfiguration(DEFAULT_SORT_ORDER_ID, SortOrder.ASCENDING, null, pagingAndFiltering, AccountOperation.class);

		List<String> classFilter = Arrays.asList("org.meveo.model.payments.Payment", "org.meveo.model.payments.AutomatedPayment", "org.meveo.model.payments.OtherCreditAndCharge");
		pagingAndFiltering.addFilter("customerAccount", customerAccount);
		pagingAndFiltering.addFilter("type_class", classFilter);

		return paginationConfiguration;
	}

	/**
	 * @param customerAccountCode customer account code
	 * @return balance for customer account
	 * @throws BusinessException business exception
	 */
	public double getBalance(String customerAccountCode) throws BusinessException {

		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

		return customerAccountService.customerAccountBalanceDue(customerAccount, new Date()).doubleValue();
	}

	/**
	 * @param cardPaymentRequestDto card payment request
	 * @return payment by card response
	 * @throws BusinessException                business exception
	 * @throws NoAllOperationUnmatchedException no all operation matched exception
	 * @throws UnbalanceAmountException         balance exception
	 * @throws MeveoApiException                opencell's api exception
	 */
	public PaymentResponseDto payByCard(PayByCardDto cardPaymentRequestDto)
			throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, MeveoApiException {

		if (StringUtils.isBlank(cardPaymentRequestDto.getCtsAmount())) {
			missingParameters.add("ctsAmount");
		}

		if (StringUtils.isBlank(cardPaymentRequestDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		boolean useCard = false;

		// case card payment
		if (!StringUtils.isBlank(cardPaymentRequestDto.getCardNumber())) {
			useCard = true;
			if (StringUtils.isBlank(cardPaymentRequestDto.getCvv())) {
				missingParameters.add("cvv");
			}
			if (StringUtils.isBlank(cardPaymentRequestDto.getExpiryDate()) || cardPaymentRequestDto.getExpiryDate().length() != 4
					|| !org.apache.commons.lang3.StringUtils.isNumeric(cardPaymentRequestDto.getExpiryDate())) {

				missingParameters.add("expiryDate");
			}
			if (StringUtils.isBlank(cardPaymentRequestDto.getOwnerName())) {
				missingParameters.add("ownerName");
			}
			if (StringUtils.isBlank(cardPaymentRequestDto.getCardType())) {
				missingParameters.add("cardType");
			}
		}
		if (cardPaymentRequestDto.isToMatch()) {
			if (cardPaymentRequestDto.getAoToPay() == null || cardPaymentRequestDto.getAoToPay().isEmpty()) {
				missingParameters.add("aoToPay");
			}
		}

		handleMissingParameters();

		CustomerAccount customerAccount = customerAccountService.findByCode(cardPaymentRequestDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, cardPaymentRequestDto.getCustomerAccountCode());
		}

		PaymentMethodEnum preferedMethod = customerAccount.getPreferredPaymentMethodType();
		if (preferedMethod != null && PaymentMethodEnum.CARD != preferedMethod) {
			throw new BusinessApiException("Can not process payment as prefered payment method is " + preferedMethod);
		}

		PaymentResponseDto doPaymentResponseDto = null;
		if (useCard) {

			doPaymentResponseDto = paymentService.payByCard(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getCardNumber(),
					cardPaymentRequestDto.getOwnerName(), cardPaymentRequestDto.getCvv(), cardPaymentRequestDto.getExpiryDate(), cardPaymentRequestDto.getCardType(),
					cardPaymentRequestDto.getAoToPay(), cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
		} else {
			doPaymentResponseDto = paymentService.payByCardToken(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getAoToPay(),
					cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
		}

		return doPaymentResponseDto;
	}

	/**
	 * List payment histories matching filtering and query criteria
	 * 
	 * @param pagingAndFiltering Paging and filtering criteria.
	 * @return A list of payment history
	 * @throws InvalidParameterException invalid parameter exception
	 */
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "paymentHistories", itemPropertiesToFilter = {
			@FilterProperty(property = "sellerCode", entityClass = Seller.class, allowAccessIfNull = false),
			@FilterProperty(property = "customerAccountCode", entityClass = CustomerAccount.class, allowAccessIfNull = false),
			@FilterProperty(property = "customerCode", entityClass = Customer.class, allowAccessIfNull = false) })
	public PaymentHistoriesDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
		PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, Arrays.asList("payment", "refund"), pagingAndFiltering,
				PaymentHistory.class);
		Long totalCount = paymentHistoryService.count(paginationConfig);
		PaymentHistoriesDto paymentHistoriesDto = new PaymentHistoriesDto();
		paymentHistoriesDto.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		paymentHistoriesDto.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		if (totalCount > 0) {
			List<PaymentHistory> paymentHistories = paymentHistoryService.list(paginationConfig);
			for (PaymentHistory paymentHistory : paymentHistories) {
				paymentHistoriesDto.getPaymentHistories().add(fromEntity(paymentHistory));
			}
		}
		return paymentHistoriesDto;
	}

	/**
	 * Return list AO matched with a payment or refund
	 * 
	 * @param paymentOrRefund
	 * @return list AO matched
	 */
	private List<AccountOperationDto> getAosPaidByPayment(AccountOperation paymentOrRefund) {
		List<AccountOperationDto> result = new ArrayList<AccountOperationDto>();
		if (paymentOrRefund == null) {
			return result;
		}
		if (paymentOrRefund.getMatchingAmounts() != null && !paymentOrRefund.getMatchingAmounts().isEmpty()) {
			for (MatchingAmount ma : paymentOrRefund.getMatchingAmounts().get(0).getMatchingCode().getMatchingAmounts()) {
				if (ma.getAccountOperation().getTransactionCategory() != paymentOrRefund.getTransactionCategory()) {
					result.add(new AccountOperationDto(ma.getAccountOperation(),
							entityToDtoConverter.getCustomFieldsDTO(ma.getAccountOperation(), CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
				}
			}
		}
		return result;
	}

	public PaymentHistoryDto fromEntity(PaymentHistory paymentHistory) {
		return fromEntity(paymentHistory, true);
	}

	/**
	 * Build paymentHistory dto from entity
	 * 
	 * @param paymentHistory payment History
	 * @return PaymentHistoryDto
	 */
	public PaymentHistoryDto fromEntity(PaymentHistory paymentHistory, boolean isIncludedAoToPay) {
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setAuditable(paymentHistory);
		paymentHistoryDto.setCustomerAccountCode(paymentHistory.getCustomerAccountCode());
		paymentHistoryDto.setCustomerAccountName(paymentHistory.getCustomerAccountName());
		paymentHistoryDto.setSellerCode(paymentHistory.getSellerCode());
		paymentHistoryDto.setCustomerCode(paymentHistory.getCustomerCode());
		paymentHistoryDto.setAmountCts(paymentHistory.getAmountCts());
		paymentHistoryDto.setAsyncStatus(paymentHistory.getAsyncStatus());
		paymentHistoryDto.setErrorCode(paymentHistory.getErrorCode());
		paymentHistoryDto.setErrorMessage(paymentHistory.getErrorMessage());
		paymentHistoryDto.setErrorType(paymentHistory.getErrorType());
		paymentHistoryDto.setExternalPaymentId(paymentHistory.getExternalPaymentId());
		paymentHistoryDto.setOperationCategory(paymentHistory.getOperationCategory());
		paymentHistoryDto.setOperationDate(paymentHistory.getOperationDate());
		paymentHistoryDto.setPaymentGatewayCode(paymentHistory.getPaymentGatewayCode());
		paymentHistoryDto.setPaymentMethodName(paymentHistory.getPaymentMethodName());
		paymentHistoryDto.setPaymentMethodType(paymentHistory.getPaymentMethodType());
		if (paymentHistory.getRefund() != null) {
			paymentHistoryDto.setRefund(new AccountOperationDto(paymentHistory.getRefund(),
					entityToDtoConverter.getCustomFieldsDTO(paymentHistory.getRefund(), CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
		}
		if (paymentHistory.getPayment() != null) {
			paymentHistoryDto.setPayment(new AccountOperationDto(paymentHistory.getPayment(),
					entityToDtoConverter.getCustomFieldsDTO(paymentHistory.getPayment(), CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
		}
		paymentHistoryDto.setSyncStatus(paymentHistory.getSyncStatus());
		paymentHistoryDto.setStatus(paymentHistory.getStatus());
		paymentHistoryDto.setLastUpdateDate(paymentHistory.getLastUpdateDate());
		if (isIncludedAoToPay) {
			AccountOperationsDto accountOperationsDto = new AccountOperationsDto();
			// Backward compatibility
			if (paymentHistory.getListAoPaid() == null || paymentHistory.getListAoPaid().isEmpty()) {
				accountOperationsDto.setAccountOperation(getAosPaidByPayment(paymentHistory.getRefund() == null ? paymentHistory.getPayment() : paymentHistory.getRefund()));

			} else {
				for (AccountOperation ao : paymentHistory.getListAoPaid()) {
					accountOperationsDto.getAccountOperation()
							.add(new AccountOperationDto(ao, entityToDtoConverter.getCustomFieldsDTO(ao, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
				}
			}
			paymentHistoryDto.setListAoPaid(accountOperationsDto);
		}

		return paymentHistoryDto;
    }
}