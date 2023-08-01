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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.payment.RefundDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.ImmutableBasicInvoice;
import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.billing.ImmutableInvoiceLinesInput;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.payments.impl.RefundService;
import org.meveo.util.ApplicationProvider;

import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;

/**
 *  @author Edward P. Legaspi
 *  @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class RefundApi extends BaseApi {

    @Inject
    private RefundService refundService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceApiService invoiceApiService;

    @Inject
    private AccountOperationApi accountOperationApi;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * @param refundDto refund object which encapsulates the input data sent by client
     * @return the id of payment if created successful otherwise null
     * @throws NoAllOperationUnmatchedException no all operation unmatched exception
     * @throws UnbalanceAmountException unbalance amount exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public Long createRefund(RefundDto refundDto) throws NoAllOperationUnmatchedException, UnbalanceAmountException, BusinessException, MeveoApiException {
        log.info("create payment for amount:" + refundDto.getAmount() + " paymentMethodEnum:" + refundDto.getPaymentMethod() + " isToMatching:" + refundDto.isToMatching()
                + "  customerAccount:" + refundDto.getCustomerAccountCode() + "...");

        if (StringUtils.isBlank(refundDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (StringUtils.isBlank(refundDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(refundDto.getReference())) {
            missingParameters.add("reference");
        }
        if (StringUtils.isBlank(refundDto.getPaymentMethod())) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();
        CustomerAccount customerAccount = customerAccountService.findByCode(refundDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + refundDto.getCustomerAccountCode());
        }

        String occTemplateCode = refundDto.getOccTemplateCode();
        if (StringUtils.isBlank(occTemplateCode)) {
            if(PaymentMethodEnum.CASH.equals(refundDto.getPaymentMethod())){
                occTemplateCode = paramBeanFactory.getInstance().getProperty("occ.refund.cash", "REF_CASH");
            }else if(PaymentMethodEnum.CHECK.equals(refundDto.getPaymentMethod())){
                occTemplateCode = paramBeanFactory.getInstance().getProperty("occ.refund.check", "REF_CHK");
            }
        }
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }

        Refund refund = new Refund();
        refund.setPaymentMethod(refundDto.getPaymentMethod());
        refund.setAmount(refundDto.getAmount());
        refund.setUnMatchingAmount(refundDto.getAmount());
        refund.setMatchingAmount(BigDecimal.ZERO);
        refund.setTransactionalMatchingAmount(BigDecimal.ZERO);
        refund.setAccountingCode(occTemplate.getAccountingCode());
        refund.setCode(occTemplate.getCode());
        refund.setDescription(StringUtils.isBlank(refundDto.getDescription()) ? occTemplate.getDescription() : refundDto.getDescription());
        refund.setTransactionCategory(occTemplate.getOccCategory());
        refund.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        refund.setCustomerAccount(customerAccount);
        refund.setReference(refundDto.getReference());
        refund.setDueDate(refundDto.getDueDate());
        refund.setTransactionDate(refundDto.getTransactionDate());
        refund.setMatchingStatus(MatchingStatusEnum.O);

        if (customerAccount.getTradingCurrency() != null && customerAccount.getTradingCurrency().getCurrentRate() != null) {
            refund.setTransactionalAmount(refund.getAmount().multiply(customerAccount.getTradingCurrency().getCurrentRate()));
            refund.setTransactionalUnMatchingAmount(refund.getAmount().multiply(customerAccount.getTradingCurrency().getCurrentRate()));
            refund.setTransactionalCurrency(customerAccount.getTradingCurrency());
        }

        // populate customFields
        try {
            populateCustomFields(refundDto.getCustomFields(), refund, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        refundService.create(refund);

        if (refundDto.isToMatching() && !refundDto.isManualRefund()) {
            matchRefunds(refundDto, customerAccount, refund);
        }

        if(refundDto.isManualRefund()) {
            // Check method allowed
            if (CollectionUtils.isEmpty(appProvider.getAllowedManualRefundMethods())) {
                throw new BusinessApiException("No Payment methods allowed for manual refund, please update the Payment Settings");
            }

            if(!appProvider.getAllowedManualRefundMethods().contains(refundDto.getPaymentMethod())) {
                throw new BusinessApiException("The Payment method '" + refundDto.getPaymentMethod() + "' is not allowed for manual refund");
            }

            // Check  dto content
            if (CollectionUtils.isEmpty(refundDto.getListAoIdsForMatching())) {
                throw new BusinessApiException("No AccountOperation specified for refund");
            }

            if (refundDto.getListAoIdsForMatching().size() > 1) {
                throw new BusinessApiException("Only one AccountOperation shall be specified for refund");
            }

            // Get RecordedInvoice AO
            Invoice initialInvoice = null;
            AccountOperation aoPay = accountOperationService.findById(refundDto.getListAoIdsForMatching().get(0));

            if(!(aoPay instanceof RecordedInvoice)) {
                if (!(aoPay instanceof Payment)) {
                    throw new BusinessApiException("The selected AccountOperation is not a Payment");
                }

                Payment payment = (Payment) aoPay;

                if (payment.getAmount().compareTo(refund.getAmount()) < 0) {
                    throw new BusinessApiException("Refund amount is greater than the Payment amount");
                }

                if (CollectionUtils.isEmpty(payment.getMatchingAmounts())) {
                    throw new BusinessApiException("No matched Invoice for Payment");
                }

                // Check that the refund amount are not exceed with the initial payment
                refundService.checkExceededCreatedRefundOnPayment(payment, refund.getAmount());

                for (MatchingAmount payMatchingAmount : payment.getMatchingAmounts()) {
                    for (MatchingAmount matchingAmount : payMatchingAmount.getMatchingCode().getMatchingAmounts()) {
                        if (matchingAmount.getAccountOperation() instanceof RecordedInvoice) {
                            initialInvoice = ((RecordedInvoice) matchingAmount.getAccountOperation()).getInvoice();
                            break;
                        }
                    }
                }

                if (initialInvoice == null) {
                    throw new BusinessApiException("No linked invoice for Payment");
                }

                // Create new Invoice Credit Note
                Invoice invoiceCreditNote = createInvoiceCreditNote(refund, initialInvoice);

                // Create new AO_ADJ_REF (CREDIT), with umatchingAmount = refund Amount (from payload)
                AccountOperationDto aoAdjRefDto = new AccountOperationDto();
                aoAdjRefDto.setAmount(refund.getTransactionalAmount());
                aoAdjRefDto.setAmountWithoutTax(refund.getTransactionalAmountWithoutTax());
                aoAdjRefDto.setTaxAmount(refund.getTaxAmount());
                aoAdjRefDto.setCode("ADJ_REF");
                aoAdjRefDto.setCustomerAccount(refund.getCustomerAccount().getCode());
                aoAdjRefDto.setTransactionCategory(OperationCategoryEnum.CREDIT);
                aoAdjRefDto.setType("I");
                TradingCurrency payTradingCurrency = tradingCurrencyService.refreshOrRetrieve(payment.getTransactionalCurrency());
                aoAdjRefDto.setTransactionalCurrency(payTradingCurrency != null ? payTradingCurrency.getCurrencyCode() : null);

                Long aoAdjRefId = accountOperationApi.create(aoAdjRefDto);

                // Add link between Invoice Credit Note and Initial Invoice
                initialInvoice.setAdjustedInvoice(invoiceCreditNote);
                invoiceService.update(initialInvoice);

                // Link AO with Invoice
                RecordedInvoice aoInvoiceCreditNote = (RecordedInvoice) accountOperationService.findById(aoAdjRefId);
                aoInvoiceCreditNote.setInvoice(invoiceCreditNote);
                accountOperationService.update(aoInvoiceCreditNote);

                // Link invoice with AO
                invoiceCreditNote.setRecordedInvoice(aoInvoiceCreditNote);
                invoiceService.update(invoiceCreditNote);

                // Add link between AO_ADJ_REF (new) and Payment (existing)
                refund.setRefundedPayment(payment);
                refundService.update(refund);

                try {
                    // Match AO_Invoice with AO_ADJ_REF
                    matchingCodeService.matchOperations(null, customerAccount.getCode(),
                            List.of(aoAdjRefId, refund.getId()), null, MatchingTypeEnum.A);
                } catch (Exception e) {
                    throw new BusinessApiException(e.getMessage());
                }

            } else {
                log.info("AccountOperation is RecordedInvoice type, no need to create Invoice and do manual adjustment");
            }
        }
        log.debug("refund created for amount:" + refund.getAmount());

        return refund.getId();
    }

    private Invoice createInvoiceCreditNote(Refund refund, Invoice initialInvoice) {
        InvoiceType invType = invoiceTypeService.findByCode("ADJ_REF");
        // Create new Invoice Credit Note
        org.meveo.apiv2.billing.BasicInvoice adjInvoice = ImmutableBasicInvoice.builder()
                .invoiceTypeCode(invType.getCode()) // Default InvoiceCreated for a Refund must be ADJ_REF
                .billingAccountCode(initialInvoice.getBillingAccount().getCode())
                .invoiceDate(new Date())
                .amountWithTax(refund.getAmountWithoutTax())
                .build();
        Invoice adjustmentInvoice = invoiceService.createBasicInvoice(adjInvoice);

        // Create Invoice Line
        org.meveo.apiv2.billing.InvoiceLine invoiceLineResource = ImmutableInvoiceLine.builder()
                .accountingArticleCode("ART-STD")
                .amountTax(BigDecimal.ZERO)
                .amountWithoutTax(refund.getAmount())
                .unitPrice(refund.getAmount())
                .invoiceId(adjustmentInvoice.getId())
                .label(invType.getDescription())
                .quantity(BigDecimal.ONE)
                .build();

        org.meveo.apiv2.billing.InvoiceLinesInput input = ImmutableInvoiceLinesInput.builder()
                .addInvoiceLines(invoiceLineResource)
                .build();

        invoiceApiService.createLines(adjustmentInvoice, input);

        // Validate ADJ Invoice
        adjustmentInvoice.setStatus(VALIDATED);
        serviceSingleton.assignInvoiceNumber(adjustmentInvoice, true);

        return adjustmentInvoice;
    }

    private void matchRefunds(RefundDto refundDto, CustomerAccount customerAccount, Refund refund)
			throws BusinessApiException, BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		List<Long> listReferenceToMatch = new ArrayList<Long>();
		if (refundDto.getListAoIdsForMatching()!=null && !refundDto.getListAoIdsForMatching().isEmpty() ) {
			listReferenceToMatch.addAll(refundDto.getListAoIdsForMatching());
		} else if (refundDto.getListOCCReferenceforMatching() != null) {
		    for (String Reference: refundDto.getListOCCReferenceforMatching()) {
		        List<RecordedInvoice> accountOperationToMatch = recordedInvoiceService.getRecordedInvoice(Reference);
		        if (accountOperationToMatch == null || accountOperationToMatch.isEmpty()) {
		            throw new BusinessApiException("Cannot find account operation with reference:" + Reference );
		        } else if (accountOperationToMatch.size() > 1) {
		            throw new BusinessApiException("More than one account operation with reference:" + Reference +". Please use ListAoIdsForMatching instead of ListOCCReferenceforMatching");
		        }
		        listReferenceToMatch.add(accountOperationToMatch.get(0).getId());
		    }
		}
		listReferenceToMatch.add(refund.getId());
		matchingCodeService.matchOperations(null, customerAccount.getCode(), listReferenceToMatch, null, MatchingTypeEnum.A);
	}

    public List<RefundDto> getRefundList(String customerAccountCode) throws Exception {
        List<RefundDto> result = new ArrayList<>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        List<AccountOperation> ops = customerAccount.getAccountOperations();
        for (AccountOperation op : ops) {
            if (op instanceof Refund) {
                Refund refund = (Refund) op;
                RefundDto refundDto = new RefundDto();
                refundDto.setType(refund.getType());
                refundDto.setAmount(refund.getAmount());
                refundDto.setDueDate(refund.getDueDate());
                refundDto.setOccTemplateCode(refund.getCode());
                refundDto.setPaymentMethod(refund.getPaymentMethod());
                refundDto.setReference(refund.getReference());
                refundDto.setTransactionDate(refund.getTransactionDate());
                refundDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(op, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                result.add(refundDto);
            }
        }
        return result;
    }

    public PaymentResponseDto refundByCard(PayByCardOrSepaDto cardPaymentRequestDto)
            throws Exception {
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

        PaymentResponseDto doPaymentResponseDto = null;
        if (useCard) {

            doPaymentResponseDto = paymentService.refundByCard(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getCardNumber(),
                cardPaymentRequestDto.getOwnerName(), cardPaymentRequestDto.getCvv(), cardPaymentRequestDto.getExpiryDate(), cardPaymentRequestDto.getCardType(),
                cardPaymentRequestDto.getAoToPay(), cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        } else {
        	
        	 PaymentMethodEnum preferedMethod = customerAccount.getPreferredPaymentMethodType();
             if (preferedMethod != null && PaymentMethodEnum.CARD != preferedMethod) {
                 throw new BusinessApiException("Can not process payment as prefered payment method is " + preferedMethod);
             }
            doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getAoToPay(),
                cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        }

        return doPaymentResponseDto;
    }

}