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

import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.StringUtils.isBlank;
import static org.meveo.service.payments.impl.PaymentRejectionCodeService.ENCODED_FILE_RESULT_LABEL;
import static org.meveo.service.payments.impl.PaymentRejectionCodeService.EXPORT_SIZE_RESULT_LABEL;
import static org.meveo.service.payments.impl.PaymentRejectionCodeService.FILE_PATH_RESULT_LABEL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.AccountOperationsDto;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentHistoryDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.payments.ClearingResponse;
import org.meveo.apiv2.payments.ImmutableClearingResponse;
import org.meveo.apiv2.payments.ImmutableRejectionCodesExportResult;
import org.meveo.apiv2.payments.ImmutableRejectionCodesImportResult;
import org.meveo.apiv2.payments.ImportRejectionCodeInput;
import org.meveo.apiv2.payments.PaymentGatewayInput;
import org.meveo.apiv2.payments.RejectionCode;
import org.meveo.apiv2.payments.RejectionCodesExportResult;
import org.meveo.apiv2.payments.RejectionCodesImportResult;
import org.meveo.apiv2.payments.resource.RejectionCodeMapper;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
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
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentRejectionCode;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.billing.impl.JournalService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.ImportRejectionCodeConfig;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentHistoryService;
import org.meveo.service.payments.impl.PaymentRejectionCodeService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.payments.impl.RejectionCodeImportResult;

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

	@Inject
	private JournalService journalService;

	@Inject
	private PaymentGatewayService paymentGatewayService;

	@Inject
	private PaymentRejectionCodeService rejectionCodeService;

	private static final String PAYMENT_GATEWAY_NOT_FOUND_ERROR_MESSAGE = "Payment gateway not found";
	private static final String PAYMENT_REJECTION_CODE_NOT_FOUND_ERROR_MESSAGE = "Payment rejection code not found";
	private final RejectionCodeMapper rejectionCodeMapper = new RejectionCodeMapper();

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

        if (isBlank(paymentDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (isBlank(paymentDto.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }
        if (isBlank(paymentDto.getReference())) {
            missingParameters.add("reference");
        }
        if (isBlank(paymentDto.getPaymentMethod())) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(paymentDto.getCustomerAccountCode());
        OCCTemplate occTemplate = oCCTemplateService.findByCode(paymentDto.getOccTemplateCode());
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + paymentDto.getOccTemplateCode());
        }
        if (!occTemplate.isManualCreationEnabled()) {
            throw new BusinessException(String.format("Creation is prohibited; occTemplate %s is not allowed for manual creation", paymentDto.getOccTemplateCode()));
        }

        Payment payment = new Payment();
		paymentService.calculateAmountsByTransactionCurrency(payment, customerAccount,
				paymentDto.getAmount(), paymentDto.getTransactionalCurrency(), payment.getTransactionDate());

		payment.setJournal(occTemplate.getJournal());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(isBlank(paymentDto.getDescription()) ? occTemplate.getDescription() : paymentDto.getDescription());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(paymentDto.getReference());
        payment.setDueDate(paymentDto.getDueDate() == null ? new Date() : paymentDto.getDueDate());
        payment.setTransactionDate(paymentDto.getTransactionDate() == null ? new Date() : paymentDto.getTransactionDate());
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
        payment.setBankCollectionDate(paymentDto.getBankCollectionDate());
		payment.setCollectionDate(paymentDto.getCollectionDate() == null ? paymentDto.getBankCollectionDate() : paymentDto.getCollectionDate());
		payment.setAccountingDate(new Date());
		accountOperationService.handleAccountingPeriods(payment);

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

		payment.setJournal(journalService.findByCode("BAN"));
        paymentService.create(payment);

		paymentHistoryService.addHistory(customerAccount,
				payment,
				null, paymentDto.getAmount().multiply(new BigDecimal(100)).longValue(),
				PaymentStatusEnum.ACCEPTED, null, null, payment.getReference(), null, null,
				null,null,paymentDto.getListAoIdsForMatching());
		if (paymentDto.isToMatching()) {
			matchPayment(paymentDto, customerAccount, payment);
        } else {
            log.info("no matching created ");
        }
        log.debug("payment created for amount:" + payment.getAmount());

        return payment.getId();

    }

	private ExchangeRate getExchangeRate(TradingCurrency tradingCurrency, TradingCurrency functionalCurrency, Date transactionDate) {
		Date exchangeDate = transactionDate != null ? transactionDate : new Date();
		ExchangeRate exchangeRate = tradingCurrency.getExchangeRate(exchangeDate);
		if (exchangeRate == null || exchangeRate.getExchangeRate() == null) {
			throw new EntityDoesNotExistsException("No valid exchange rate found for currency " + tradingCurrency.getCurrencyCode()
					+ " on " + exchangeDate);
		}
		return exchangeRate;
	}

	private void checkTransactionalCurrency(String transactionalcurrency, TradingCurrency tradingCurrency) {
		if (tradingCurrency == null || isBlank(tradingCurrency)) {
			throw new InvalidParameterException("Currency " + transactionalcurrency +
					" is not recorded a trading currency in Opencell. Only currencies declared as trading currencies can be used to record account operations.");
		}
	}


	private void matchPayment(PaymentDto paymentDto, CustomerAccount customerAccount, Payment payment)
			throws BusinessApiException, BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		List<Long> listReferenceToMatch = new ArrayList<>();
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
		List<AccountOperation> aosToPaid = new ArrayList<>();
		for(Long id : listReferenceToMatch ) {
			AccountOperation ao = accountOperationService.findById(id);
			if(ao == null) {
				 throw new BusinessApiException("Cannot find account operation with id:" + id );
			}
			aosToPaid.add(ao);
		}
		 Collections.sort(aosToPaid, Comparator.comparing(AccountOperation::getDueDate));
		if(checkAccountOperationCurrency(aosToPaid, paymentDto.getTransactionalCurrency())) {
			throw new BusinessApiException("Transaction currency is different from account operation currency");
		}
		for(AccountOperation ao :aosToPaid ) {
			if(BigDecimal.ZERO.compareTo(payment.getUnMatchingAmount()) == 0) {
				break;
			}
			List<Long> aosIdsToMatch = new ArrayList<>();
			aosIdsToMatch.add(ao.getId());
			aosIdsToMatch.add(payment.getId());
			matchingCodeService.matchOperations(null, customerAccount.getCode(), aosIdsToMatch, payment.getId(), MatchingTypeEnum.A);
		}
	}

	private boolean checkAccountOperationCurrency(List<AccountOperation> aosToPaid, String transactionalCurrency) {
		return aosToPaid.stream()
				.anyMatch(accountOperation -> ! accountOperation.getCode().endsWith("_SD") &&
						!accountOperation.getTransactionalCurrency().getCurrencyCode().equalsIgnoreCase(transactionalCurrency));
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

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
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
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
	public double getBalance(String customerAccountCode) throws BusinessException {

		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

		return customerAccountService.customerAccountBalanceDue(customerAccount, new Date()).doubleValue();
	}

	
	
	
	/**
	 * 
	 * @param sepaPaymentRequestDto
	 * @return
	 * @throws Exception
	 */
	public PaymentResponseDto payBySepa(PayByCardOrSepaDto sepaPaymentRequestDto)
			throws Exception {

		if (isBlank(sepaPaymentRequestDto.getCtsAmount())) {
			missingParameters.add("ctsAmount");
		}

		if (isBlank(sepaPaymentRequestDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}

		if (sepaPaymentRequestDto.isToMatch() && sepaPaymentRequestDto.getAoToPay() == null
				|| sepaPaymentRequestDto.getAoToPay().isEmpty()) {
			missingParameters.add("aoToPay");
		}

		handleMissingParameters();

		CustomerAccount customerAccount = customerAccountService
				.findByCode(sepaPaymentRequestDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class,
					sepaPaymentRequestDto.getCustomerAccountCode());
		}

		PaymentMethodEnum preferedMethod = customerAccount.getPreferredPaymentMethodType();
		if (preferedMethod != null && PaymentMethodEnum.DIRECTDEBIT != preferedMethod) {
			throw new BusinessApiException("Can not process payment as prefered payment method is " + preferedMethod);
		}

		return paymentService.payByMandat(customerAccount, sepaPaymentRequestDto.getCtsAmount(),
				sepaPaymentRequestDto.getAoToPay(), sepaPaymentRequestDto.isCreateAO(),
				sepaPaymentRequestDto.isToMatch(), null);
	}

	
	/**
	 * @param cardPaymentRequestDto card payment request
	 * @return payment by card response
	 * @throws Exception 
	 */
	public PaymentResponseDto payByCard(PayByCardOrSepaDto cardPaymentRequestDto)
			throws Exception {

		if (isBlank(cardPaymentRequestDto.getCtsAmount())) {
			missingParameters.add("ctsAmount");
		}

		if (isBlank(cardPaymentRequestDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		boolean useCard = false;

		// case card payment
		if (!isBlank(cardPaymentRequestDto.getCardNumber())) {
			useCard = true;
			if (isBlank(cardPaymentRequestDto.getCvv())) {
				missingParameters.add("cvv");
			}
			if (isBlank(cardPaymentRequestDto.getExpiryDate()) || cardPaymentRequestDto.getExpiryDate().length() != 4
					|| !org.apache.commons.lang3.StringUtils.isNumeric(cardPaymentRequestDto.getExpiryDate())) {

				missingParameters.add("expiryDate");
			}
			if (isBlank(cardPaymentRequestDto.getOwnerName())) {
				missingParameters.add("ownerName");
			}
			if (isBlank(cardPaymentRequestDto.getCardType())) {
				missingParameters.add("cardType");
			}
		}
		if (cardPaymentRequestDto.isToMatch() && cardPaymentRequestDto.getAoToPay() == null || cardPaymentRequestDto.getAoToPay().isEmpty()) {			
				missingParameters.add("aoToPay");			
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
		paymentHistoryDto.setAuditableEntity(paymentHistory);
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

	/**
	 * Create payment rejection code
	 *
	 * @param rejectionCode payment rejection code
	 * @return RejectionCode id
	 */
	public Long createPaymentRejectionCode(RejectionCode rejectionCode) {
		PaymentGateway paymentGateway =
				rejectionCode.getPaymentGateway() != null ? loadPaymentGateway(rejectionCode.getPaymentGateway()) : null;
		if (paymentGateway == null) {
			throw new NotFoundException(PAYMENT_GATEWAY_NOT_FOUND_ERROR_MESSAGE);
		}
		PaymentRejectionCode paymentRejectionCode = rejectionCodeMapper.toEntity(rejectionCode, paymentGateway);
		rejectionCodeService.create(paymentRejectionCode);
		return paymentRejectionCode.getId();

	}

	private PaymentGateway loadPaymentGateway(Resource paymentGatewayResource) {
		PaymentGateway paymentGateway;
		if (paymentGatewayResource.getId() != null) {
			paymentGateway = paymentGatewayService.findById(paymentGatewayResource.getId());
			if (paymentGateway == null && paymentGatewayResource.getCode() != null) {
				paymentGateway = paymentGatewayService.findByCode(paymentGatewayResource.getCode());
			}
		} else {
			paymentGateway = paymentGatewayService.findByCode(paymentGatewayResource.getCode());
		}
		return paymentGateway;
	}

	/**
	 * Update payment rejection code
	 *
	 * @param id       payment rejection code id
	 * @param resource payment rejection code
	 * @return RejectionCode updated result
	 */
	public RejectionCode updatePaymentRejectionCode(Long id, RejectionCode resource) {
		PaymentGateway paymentGateway = null;
		if (resource.getPaymentGateway() != null) {
			paymentGateway = loadPaymentGateway(resource.getPaymentGateway());
			if (paymentGateway == null) {
				throw new NotFoundException(PAYMENT_GATEWAY_NOT_FOUND_ERROR_MESSAGE);
			}
		}
		PaymentRejectionCode rejectionCodeToUpdate = ofNullable(rejectionCodeService.findById(id))
				.orElseThrow(() -> new NotFoundException(PAYMENT_REJECTION_CODE_NOT_FOUND_ERROR_MESSAGE));
		ofNullable(resource.getCode()).ifPresent(rejectionCodeToUpdate::setCode);
		ofNullable(resource.getDescription()).ifPresent(rejectionCodeToUpdate::setDescription);
		ofNullable(resource.getDescriptionI18n()).ifPresent(rejectionCodeToUpdate::setDescriptionI18n);
		ofNullable(paymentGateway).ifPresent(rejectionCodeToUpdate::setPaymentGateway);

		return rejectionCodeMapper.toResource(rejectionCodeService.update(rejectionCodeToUpdate));
	}

	/**
	 * Delete rejection code
	 *
	 * @param id payment rejection code id
	 */
	public void removeRejectionCode(Long id) {
		PaymentRejectionCode rejectionCode = ofNullable(rejectionCodeService.findById(id))
				.orElseThrow(() -> new NotFoundException(PAYMENT_REJECTION_CODE_NOT_FOUND_ERROR_MESSAGE));
		rejectionCodeService.remove(rejectionCode);
	}

	/**
	 * Clear rejectionCodes by gateway
	 *
	 * @param paymentGatewayInput payment gateway
	 */
	public ClearingResponse clearAll(PaymentGatewayInput paymentGatewayInput) {
		PaymentGateway paymentGateway = null;
		if (paymentGatewayInput != null && paymentGatewayInput.getPaymentGateway() != null) {
			paymentGateway = ofNullable(loadPaymentGateway(paymentGatewayInput.getPaymentGateway()))
					.orElseThrow(() -> new NotFoundException(PAYMENT_GATEWAY_NOT_FOUND_ERROR_MESSAGE));
		}
		return buildResponse(rejectionCodeService.clearAll(paymentGateway), paymentGateway);
	}

	private ClearingResponse buildResponse(int clearedCodesCount, PaymentGateway paymentGateway) {
		ImmutableClearingResponse.Builder builder = ImmutableClearingResponse
				.builder()
				.status("SUCCESS")
				.clearedCodesCount(clearedCodesCount);
		if(paymentGateway != null) {
			builder.associatedPaymentGatewayCode(paymentGateway.getCode());
		}
		if(clearedCodesCount == 0) {
			return builder
					.massage("No rejection code found to clear")
					.build();
		} else {
			return builder
					.massage("Rejection codes successfully cleared")
					.build();
		}
	}

	/**
	 * Export rejection codes by payment gateway
	 *
	 * @param paymentGatewayResource payment gateway
	 * @return RejectionCodesExportResult
	 */
	public RejectionCodesExportResult export(PaymentGatewayInput paymentGatewayResource) {
		PaymentGateway paymentGateway = null;
		if (paymentGatewayResource != null && paymentGatewayResource.getPaymentGateway() != null) {
			paymentGateway = ofNullable(loadPaymentGateway(paymentGatewayResource.getPaymentGateway()))
					.orElseThrow(() -> new NotFoundException(PAYMENT_GATEWAY_NOT_FOUND_ERROR_MESSAGE));
		}
		Map<String, Object> exportResult = rejectionCodeService.export(paymentGateway);
		return ImmutableRejectionCodesExportResult.builder()
				.exportSize((Integer) exportResult.get(EXPORT_SIZE_RESULT_LABEL))
				.fileFullPath((String) exportResult.get(FILE_PATH_RESULT_LABEL))
				.encodedFile((String) exportResult.get(ENCODED_FILE_RESULT_LABEL))
				.build();
	}

	/**
	 * Import rejection codes by payment gateway
	 *
	 * @param importRejectionCodeInput Import data
	 * @return RejectionCodesExportResult
	 */
	public RejectionCodesImportResult importRejectionCodes(ImportRejectionCodeInput importRejectionCodeInput) {
		if (isBlank(importRejectionCodeInput.getBase64csv())) {
			throw new BusinessApiException("Encoded file should not be null or empty");
		}
		ImportRejectionCodeConfig config =
				new ImportRejectionCodeConfig(importRejectionCodeInput.getBase64csv(),
						importRejectionCodeInput.getIgnoreLanguageErrors(),
						importRejectionCodeInput.getMode());
		RejectionCodeImportResult importResult = rejectionCodeService.importRejectionCodes(config);
		return ImmutableRejectionCodesImportResult.builder()
				.linesCount(importResult.getLineToImportCount())
				.successfullyImportedCodes(importResult.getSuccessCount())
				.errorCount(importResult.getErrorCount())
				.errors(importResult.getErrors())
				.importedFile(importRejectionCodeInput.getBase64csv())
				.importMode(importRejectionCodeInput.getMode())
				.build();
	}
}
