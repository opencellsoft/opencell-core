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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.PaymentException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedRefund;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorEnum;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.RejectedType;
import org.meveo.service.base.PersistenceService;

/**
 * Payment service implementation.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @author Said Ramli
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class PaymentService extends PersistenceService<Payment> {

    /** The payment method service. */
    @Inject
    private PaymentMethodService paymentMethodService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;

    /** The gateway payment factory. */
    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The payment gateway service. */
    @Inject
    private PaymentGatewayService paymentGatewayService;

    /** The payment history service. */
    @Inject
    private PaymentHistoryService paymentHistoryService;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    /** The refund service. */
    @Inject
    private RefundService refundService;


    @MeveoAudit
    @Override
    public void create(Payment entity) throws BusinessException {
        super.create(entity);
        if (entity.getId() != null && entity.getPaymentMethod().isSimple()) {
            PaymentMethod paymentMethod  = getPaymentMethod(entity);
            paymentHistoryService.addHistory(entity.getCustomerAccount(), entity, null, entity.getAmount().multiply(new BigDecimal(100)).longValue(), PaymentStatusEnum.ACCEPTED, null, null, null, OperationCategoryEnum.CREDIT, null, paymentMethod,null);
        }
    }

    /**
     * Return the a Method payment corresponds to method payment type
     * @param payment the payment
     * @return A method payment.
     */
    private PaymentMethod getPaymentMethod(Payment payment) {
        if (payment == null || payment.getCustomerAccount() == null || payment.getCustomerAccount().getPaymentMethods() == null ) {
            return null;
        }
        List<PaymentMethod> paymentMethods = payment.getCustomerAccount().getPaymentMethods().stream().filter(paymentMethod -> {
            return paymentMethod.getPaymentType().equals(payment.getPaymentMethod());
        }).collect(Collectors.toList());
        return (paymentMethods != null && !paymentMethods.isEmpty()) ? paymentMethods.get(0) : null;
    }

    /**
     * Pay by card token. An existing and preferred card payment method will be used. If currently preferred card payment method is not valid, a new currently valid card payment
     * will be used (and marked as preferred).
     * 
     * @param customerAccount Customer account
     * @param ctsAmount Amount to pay in cent
     * @param aoIdsToPay list of account operations's id
     * @param createAO true if need to create account operation
     * @param matchingAO true if matching operation.
     * @param paymentGateway if set, this paymentGateway will be used
     * @return instance of PayByCardResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto payByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws Exception {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true, PaymentMethodEnum.CARD);
    }

    /**
     * Pay by card. A new card payment type is registered as token if payment was successfull.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoIdsToPay list of account operation's id to pay
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto payByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws Exception {

        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, true,
            PaymentMethodEnum.CARD);
    }

    /**
     * Pay by sepa.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to pay
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto payByMandat(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws Exception {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true, PaymentMethodEnum.DIRECTDEBIT);
    }

    /**
     * Refund by sepa.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param createAO if true refund account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto refundByMandat(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws Exception {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, false, PaymentMethodEnum.DIRECTDEBIT);
    }

    /**
     * Refund by card token. An existing and preferred card payment method will be used. If currently preferred card payment method is not valid, a new currently valid card payment
     * will be used (and marked as preferred)
     * 
     * @param customerAccount Customer account
     * @param ctsAmount Amount to refund
     * @param aoIdsToRefund list of account operations ids to be refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway if set, this paymentGateway will be used
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto refundByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws Exception {
        return doPayment(customerAccount, ctsAmount, aoIdsToRefund, createAO, matchingAO, paymentGateway, null, null, null, null, null, false, PaymentMethodEnum.CARD);
    }

    /**
     * Refund by card. A new card payment type is registered as token if refund was successfull.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoToRefund list of account operation's id to refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    public PaymentResponseDto refundByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoToRefund, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws Exception {
        return doPayment(customerAccount, ctsAmount, aoToRefund, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, false,
            PaymentMethodEnum.CARD);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private PaymentResponseDto makeTheRealPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType,Long aoPaymentId)
            throws Exception {

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        PaymentMethod preferredMethod = null;
        OperationCategoryEnum operationCat = isPayment ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT;
        
        
        try {
            boolean isNewCard = !StringUtils.isBlank(cardNumber);
            preferredMethod = customerAccount.getPreferredPaymentMethod();
            if (!isNewCard) {
                if (preferredMethod == null) {
                    throw new PaymentException(PaymentErrorEnum.NO_PAY_METHOD_FOR_CA, "There no payment method for customerAccount:" + customerAccount.getCode());
                }
            }
            GatewayPaymentInterface gatewayPaymentInterface = null;
            PaymentGateway matchedPaymentGatewayForTheCA = paymentGatewayService.getAndCheckPaymentGateway(customerAccount, preferredMethod ,cardType,null,paymentGateway.getCode());
            if (matchedPaymentGatewayForTheCA == null) {
                throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
            }
            
            paymentGateway = matchedPaymentGatewayForTheCA;
            
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
            
            
           
            PaymentErrorTypeEnum errorType = null;
            
                if (isNewCard) {
                    preferredMethod = addCardFromPayment(doPaymentResponseDto.getTokenId(), customerAccount, cardNumber, cardType, ownerName, cvv, expiryDate);
                }
                preferredMethod.setUserId(doPaymentResponseDto.getCodeClientSide());
                preferredMethod = paymentMethodService.update(preferredMethod);

           
            if (PaymentMethodEnum.CARD == paymentMethodType) {
                if (!(preferredMethod instanceof CardPaymentMethod)) {
                    throw new PaymentException(PaymentErrorEnum.PAY_CARD_CANNOT_BE_PREFERED, "Can not process payment card as prefered payment method is " + preferredMethod.getPaymentType());
                }
                // If card payment method is currently not valid, find a valid
                // one and mark it as preferred or throw an exception
                if (!((CardPaymentMethod) preferredMethod).isValidForDate(new Date())) {
                    preferredMethod = customerAccount.markCurrentlyValidCardPaymentAsPreferred();
                    if (preferredMethod != null) {
                        customerAccount = customerAccountService.update(customerAccount);
                    } else {
                        throw new PaymentException(PaymentErrorEnum.PAY_CB_INVALID, "There is no currently valid payment method for customerAccount:" + customerAccount.getCode());
                    }
                }
                
                if (isPayment) {
                    if (isNewCard) {
                        doPaymentResponseDto = gatewayPaymentInterface.doPaymentCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                    } else {
                        doPaymentResponseDto = gatewayPaymentInterface.doPaymentToken(((CardPaymentMethod) preferredMethod), ctsAmount, null);
                    }
                } else {
                    if (isNewCard) {
                        doPaymentResponseDto = gatewayPaymentInterface.doRefundCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                    } else {
                        doPaymentResponseDto = gatewayPaymentInterface.doRefundToken(((CardPaymentMethod) preferredMethod), ctsAmount, null);
                    }
                }

            }
            if (PaymentMethodEnum.DIRECTDEBIT == paymentMethodType) {
            	Map<String, Object> additionalParams=new HashedMap<String, Object>();
            	additionalParams.put("customerAccountCode", customerAccount.getCode());  
            	additionalParams.put("aoToPayOrRefund", aoIdsToPay.get(0));
            	additionalParams.put("createdAO", aoPaymentId);
                if (!(preferredMethod instanceof DDPaymentMethod)) {
                    throw new PaymentException(PaymentErrorEnum.PAY_METHOD_IS_NOT_DD, "Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
                }
                if (StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getMandateIdentification())) {
                    throw new PaymentException(PaymentErrorEnum.PAY_SEPA_MANDATE_BLANK, "Can not process payment sepa as Mandate is blank");
                }
                if (isPayment) {
                    doPaymentResponseDto = gatewayPaymentInterface.doPaymentSepa(((DDPaymentMethod) preferredMethod), ctsAmount, additionalParams);
                } else {
                    doPaymentResponseDto = gatewayPaymentInterface.doRefundSepa(((DDPaymentMethod) preferredMethod), ctsAmount, additionalParams);
                }
            }
            
            if(PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()) {
            	paymentCallback(doPaymentResponseDto.getPaymentID(), PaymentStatusEnum.REJECTED, doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage());
            }
            if(PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.NOT_PROCESSED == doPaymentResponseDto.getPaymentStatus()){
            	throw new BusinessException(doPaymentResponseDto.getErrorCode());
            }
             
			Refund refund = (!isPayment && aoPaymentId != null) ? refundService.findById(aoPaymentId) : null;
			Payment payment = (isPayment && aoPaymentId != null) ? findById(aoPaymentId) : null;
			AccountOperation accountOperation =accountOperationService.findById(aoPaymentId);
			if(accountOperation!=null) {
			accountOperation.setReference(doPaymentResponseDto.getPaymentID());
			accountOperationService.update(accountOperation);
			}else {
				log.warn("Cant find account operation by id= "+aoPaymentId);
			}
			paymentHistoryService.addHistory(customerAccount, payment, refund, ctsAmount, doPaymentResponseDto.getPaymentStatus(),doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage(),
					errorType, operationCat, paymentGateway.getCode(), preferredMethod,aoIdsToPay);

        } catch (PaymentException e) {
            log.error("PaymentException during payment AO:", e);
            doPaymentResponseDto = processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, e.getCode(), e.getMessage(),aoIdsToPay);
            throw  e;
        } catch (Exception e) {
            log.error("Error during payment AO:", e);
            doPaymentResponseDto = processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, null, e.getMessage(),aoIdsToPay);
            throw new Exception(e.getMessage());
        }
        return doPaymentResponseDto;
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private PaymentResponseDto createAO(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
    	 Long aoPaymentId = null;
    	 PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
    	 if (createAO) {
             try {
                 if (isPayment) {
                     aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
                 } else {
                     aoPaymentId = refundService.createRefundAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
                 }
                 doPaymentResponseDto.setAoCreated(true);
             } catch (Exception e) {
                 log.warn("Cant create Account operation payment :", e);
             }
             if (matchingAO ) {
                 try {
                     List<Long> aoIdsToMatch = aoIdsToPay;
                     aoIdsToMatch.add(aoPaymentId);
                     matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);
                     doPaymentResponseDto.setMatchingCreated(true);
                 } catch (Exception e) {
                	 aoPaymentId = null;
                     log.warn("Cant create matching :", e);
                 }
             }
         } 
    	 
    	 doPaymentResponseDto.setAccountOperationId(aoPaymentId);
    	
    	return doPaymentResponseDto;
    	
    }
    
    /**
     * Do payment or refund by token or card.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param isPayment if true is a payment else is a refund.
     * @param paymentMethodType payment method to use, CARD or DIRECTDEIBT.
     * @return instance of PaymentResponseDto
     * @throws Exception 
     */
    
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public PaymentResponseDto doPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
			String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType)
			throws Exception {

		PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();

		doPaymentResponseDto = createAO(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, isPayment,
				paymentMethodType);
		
		Long aoId = doPaymentResponseDto.getAccountOperationId();

		if (!createAO || !StringUtils.isBlank(aoId)) {
			try {
				doPaymentResponseDto = makeTheRealPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate,
						cardType, isPayment, paymentMethodType, aoId);
				if (doPaymentResponseDto.getPaymentStatus() != PaymentStatusEnum.ACCEPTED && doPaymentResponseDto.getPaymentStatus() != PaymentStatusEnum.PENDING) {
					cancelPayment(doPaymentResponseDto.getAccountOperationId());
				}else {
					updatePaymentAO(aoId, doPaymentResponseDto);
				}
			} catch (Exception e) {
				cancelPayment(aoId);
				throw new Exception(e.getMessage());
			}
		}		
		return doPaymentResponseDto;

	}

	private void cancelPayment(Long aoId) {

		if (aoId != null) {
			matchingCodeService.unmatchingByAOid(aoId);
			accountOperationService.remove(aoId);
		}

	}

	private void updatePaymentAO(Long aoId, PaymentResponseDto doPaymentResponseDto) {
		if (aoId != null && doPaymentResponseDto != null) {
			AccountOperation ao = accountOperationService.findById(aoId);
			if(ao!=null) {
			ao.setType(doPaymentResponseDto.getPaymentBrand());
			ao.setBankReference(doPaymentResponseDto.getBankRefenrence());
			ao.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
			ao.setReference(doPaymentResponseDto.getPaymentID());
			accountOperationService.updateNoCheck(ao);
		}
		}
	}
    
    

    private PaymentResponseDto processPaymentException(CustomerAccount customerAccount, Long ctsAmount, PaymentGateway paymentGateway, PaymentResponseDto doPaymentResponseDto,
            PaymentMethod preferredMethod, OperationCategoryEnum operationCat, String code, String msg,List<Long> aoIdsToPay) throws BusinessException {
        if (doPaymentResponseDto == null) {
            doPaymentResponseDto = new PaymentResponseDto();
        }
        doPaymentResponseDto.setErrorMessage(msg);
        doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
        doPaymentResponseDto.setErrorCode(code);
        paymentHistoryService.addHistory(customerAccount, null, null, ctsAmount, PaymentStatusEnum.ERROR, code, msg, PaymentErrorTypeEnum.ERROR, operationCat,
            paymentGateway.getCode(), preferredMethod,aoIdsToPay);
        return doPaymentResponseDto;
    }

    /**
     * Create the payment account operation for the payment that was processed.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param doPaymentResponseDto payment responsse dto
     * @param paymentMethodType payment method used
     * @param aoIdsToPay list AO to paid
     * @return the AO id created
     * @throws BusinessException business exception.
     */
    public Long createPaymentAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto, PaymentMethodEnum paymentMethodType,
            List<Long> aoIdsToPay) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String occTemplateCode = paramBean.getProperty("occ.payment.card", "PAY_CRD");
        if (paymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            occTemplateCode = paramBean.getProperty("occ.payment.dd", "PAY_DDT");
        }
        if (paymentMethodType == PaymentMethodEnum.STRIPE) {
            occTemplateCode = paramBean.getProperty("occ.payment.stp", "PAY_STP");
        }
        if (paymentMethodType == PaymentMethodEnum.PAYPAL) {
            occTemplateCode = paramBean.getProperty("occ.payment.pal", "PAY_PAL");
        }
        
        
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethodType);
        payment.setAmount((new BigDecimal(ctsAmount).divide(new BigDecimal(100))));
        payment.setUnMatchingAmount(payment.getAmount());
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(occTemplate.getDescription());
        payment.setType(doPaymentResponseDto.getPaymentBrand());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(doPaymentResponseDto.getPaymentID());
        payment.setTransactionDate(new Date());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setBankReference(doPaymentResponseDto.getBankRefenrence());
        BigDecimal sumTax = BigDecimal.ZERO;
        BigDecimal sumWithoutTax = BigDecimal.ZERO;
//        String orderNums = "";
        StringBuffer orderNumsSB = new StringBuffer();
        for (Long aoId : aoIdsToPay) {
            AccountOperation ao = accountOperationService.findById(aoId);
            sumTax = sumTax.add(ao.getTaxAmount()!=null?ao.getTaxAmount():BigDecimal.ZERO);
            sumWithoutTax = sumWithoutTax.add(ao.getAmountWithoutTax()!=null?ao.getAmountWithoutTax():BigDecimal.ZERO);
            if (!StringUtils.isBlank(ao.getOrderNumber())) {
//                orderNums = orderNums + ao.getOrderNumber() + "|";
                orderNumsSB.append(ao.getOrderNumber() + "|");
            }
        }
        payment.setTaxAmount(sumTax);
        payment.setAmountWithoutTax(sumWithoutTax);
        String orderNums = orderNumsSB.toString();
        payment.setOrderNumber(orderNums);
        create(payment);
        return payment.getId();

    }

    /**
     * Create a card as CardPaymentMethod from initial payment.
     *
     * @param tokenId tokenId returned from the initial payment
     * @param customerAccount customer Account
     * @param cardNumber card Number
     * @param cardType card Type
     * @param ownerName owner Name
     * @param cvv cvv
     * @param expiryDate expiryDate
     * @return The CardPaymentMethod created
     * @throws BusinessException Business Exception
     */
    private CardPaymentMethod addCardFromPayment(String tokenId, CustomerAccount customerAccount, String cardNumber, CreditCardTypeEnum cardType, String ownerName, String cvv,
            String expiryDate) throws BusinessException {
        CardPaymentMethod paymentMethod = new CardPaymentMethod();
        paymentMethod.setAlias("Card_" + cardNumber.substring(12, 16));
        paymentMethod.setCardNumber(cardNumber);
        paymentMethod.setCardType(cardType);
        paymentMethod.setCustomerAccount(customerAccount);
        paymentMethod.setPreferred(true);
        paymentMethod.setMonthExpiration(new Integer(expiryDate.substring(0, 2)));
        paymentMethod.setYearExpiration(new Integer(expiryDate.substring(2, 4)));
        paymentMethod.setOwner(ownerName);
        paymentMethod.setTokenId(tokenId);
        paymentMethodService.create(paymentMethod);
        return paymentMethod;
    }

    /**
     * Handle payment callBack, if the payment/refund is accepted then nothing to do, if it's rejected a new AO rejected payment/refund will be created.
     * 
     * @param paymentId payment Id
     * @param paymentStatus payment Status
     * @param errorCode error Code
     * @param errorMessage error Message
     * @throws BusinessException Business Exception
     */
    public void paymentCallback(String paymentId, PaymentStatusEnum paymentStatus, String errorCode, String errorMessage) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        try {
            if (paymentStatus == null) {
                throw new BusinessException("paymentStatus is required");
            }
            if (StringUtils.isBlank(paymentId)) {
                throw new BusinessException("paymentId is required");
            }
            AccountOperation accountOperation = accountOperationService.findByReference(paymentId);

            if (accountOperation == null) {
                throw new BusinessException("Payment " + paymentId + " not found");
            }
            if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L) {
                throw new BusinessException("CallBack unexpected  for payment " + paymentId);
            }
            if (PaymentStatusEnum.ACCEPTED == paymentStatus) {
                log.debug("Payment ok, nothing to do.");
            } else {
                String occTemplateCode = null;
                List<AccountOperation> listAoThatSupposedPaid = getAccountOperationThatWasPaid(accountOperation);
                if (accountOperation instanceof AutomatedRefund || accountOperation instanceof Refund) {
                    if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                        occTemplateCode = paramBean.getProperty("occ.rejectedRefund.card", "REJ_RCR");
                    } else {
                        occTemplateCode = paramBean.getProperty("occ.rejectedRefund.dd", "REJ_RDD");
                    }
                } else if (accountOperation instanceof Payment) {
                    if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                        occTemplateCode = paramBean.getProperty("occ.rejectedPayment.card", "REJ_CRD");
                    } else {
                        occTemplateCode = paramBean.getProperty("occ.rejectedPayment.dd", "REJ_DDT");
                    }
                }

                OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
                if (occTemplate == null) {
                    throw new BusinessException("Cannot find AO Template with code:" + occTemplateCode);
                }
                CustomerAccount ca = accountOperation.getCustomerAccount();
                Long aoPaymentIdWasRejected = accountOperation.getId();

                matchingCodeService.unmatchingByAOid(aoPaymentIdWasRejected);

                RejectedPayment rejectedPayment = new RejectedPayment();
                rejectedPayment.setType("R");
                rejectedPayment.setMatchingAmount(BigDecimal.ZERO);
                rejectedPayment.setMatchingStatus(MatchingStatusEnum.O);
                rejectedPayment.setUnMatchingAmount(accountOperation.getUnMatchingAmount());
                rejectedPayment.setAmount(accountOperation.getUnMatchingAmount());
                rejectedPayment.setReference("r_" + paymentId);
                rejectedPayment.setCustomerAccount(ca);
                rejectedPayment.setAccountingCode(occTemplate.getAccountingCode());
                rejectedPayment.setCode(occTemplate.getCode());
                rejectedPayment.setDescription(occTemplate.getDescription());
                rejectedPayment.setTransactionCategory(occTemplate.getOccCategory());
                rejectedPayment.setAccountCodeClientSide(accountOperation.getAccountCodeClientSide());
                rejectedPayment.setPaymentMethod(accountOperation.getPaymentMethod());
                rejectedPayment.setTaxAmount(accountOperation.getTaxAmount());
                rejectedPayment.setAmountWithoutTax(accountOperation.getAmountWithoutTax());
                rejectedPayment.setOrderNumber(accountOperation.getOrderNumber());
                rejectedPayment.setRejectedType(RejectedType.A);
                rejectedPayment.setRejectedDate(new Date());
                rejectedPayment.setTransactionDate(new Date());
                rejectedPayment.setRejectedDescription(errorMessage);
                rejectedPayment.setRejectedCode(errorCode);
                rejectedPayment.setListAaccountOperationSupposedPaid(listAoThatSupposedPaid);

                accountOperationService.create(rejectedPayment);
                for(AccountOperation ao : listAoThatSupposedPaid) {
                    ao.setRejectedPayment(rejectedPayment);
                }
                Long oARejectPaymentID = rejectedPayment.getId();

                List<Long> aos = new ArrayList<>();
                aos.add(aoPaymentIdWasRejected);
                aos.add(oARejectPaymentID);

                matchingCodeService.matchOperations(ca.getId(), null, aos, null);
            }
            PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(paymentId);
            if (paymentHistory != null) {
                paymentHistory.setAsyncStatus(paymentStatus);
                paymentHistory.setLastUpdateDate(new Date());
                paymentHistory.setErrorCode(errorCode);
                paymentHistory.setErrorMessage(errorMessage);
                paymentHistoryService.update(paymentHistory);
            }
        } catch (Exception e) {
            log.error("Error on payment callback processing:", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Retrieve the list account Operation that was paid.
     * 
     * @param paymentOrRefund the payment or refund.
     * @return list of the account Operation that was paid
     * @throws BusinessException Business Exception
     */
    public  List<AccountOperation> getAccountOperationThatWasPaid(AccountOperation paymentOrRefund) throws BusinessException {
        if (paymentOrRefund.getMatchingStatus() != MatchingStatusEnum.L && paymentOrRefund.getMatchingStatus() != MatchingStatusEnum.P) {
            return null;
        }
        List<AccountOperation> listAoThatSupposedPaid = new ArrayList<AccountOperation>();
        List<MatchingAmount> matchingAmounts = paymentOrRefund.getMatchingAmounts();
        log.trace("matchingAmounts:" + matchingAmounts);
        for (MatchingAmount ma : paymentOrRefund.getMatchingAmounts().get(0).getMatchingCode().getMatchingAmounts()) {
            log.trace("ma.getAccountOperation() id:{} , occ code:{}", ma.getAccountOperation().toString(), ma.getAccountOperation().getCode());
            if (!(ma.getAccountOperation() instanceof Payment) && !(ma.getAccountOperation() instanceof Refund) && !(ma.getAccountOperation() instanceof RejectedPayment) ) {
                listAoThatSupposedPaid.add(ma.getAccountOperation());
            }
        }
        if (listAoThatSupposedPaid.isEmpty()) {
            throw new BusinessException("Cant find invoice account operation to unmatching");
        }
        return listAoThatSupposedPaid;

    }
    
    public int updatePaymentReference() {
        return getEntityManager().createNamedQuery("Payment.updateReference").executeUpdate();
    }
    public int updateRefundReference() {
        return getEntityManager().createNamedQuery("Refund.updateReference").executeUpdate();
    }

}