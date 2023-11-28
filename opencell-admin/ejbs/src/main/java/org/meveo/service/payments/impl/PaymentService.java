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
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.PaymentException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
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
import org.meveo.service.admin.impl.TradingCurrencyService;
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

    @Inject
    private TradingCurrencyService tradingCurrencyService;


    @MeveoAudit
    @Override
    public void create(Payment entity) throws BusinessException {
        accountOperationService.handleAccountingPeriods(entity);
        accountOperationService.fillOperationNumber(entity);
        super.create(entity);
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
     * @throws Exception exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
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
     * @throws Exception exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
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
     * @throws Exception exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
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
     * Refund by Mandat for Security Deposit.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param paymentGateway the set this payment gateway will be used.
     * @return id Refund
.
     */
    public Long refundByMandatSD(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, PaymentGateway paymentGateway) {
        return doPaymentSD(customerAccount, ctsAmount, aoIdsToPay, paymentGateway, null, null, null, null, null, PaymentMethodEnum.DIRECTDEBIT);
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
     * @throws Exception exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException un balance amount exception.
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
     * @throws Exception exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto refundByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoToRefund, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws Exception {
        return doPayment(customerAccount, ctsAmount, aoToRefund, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, false,
            PaymentMethodEnum.CARD);
    }

    /**
     * Refund by card for Security Deposit.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param paymentGateway the set this payment gateway will be used.
     * @return id Refund
.
     */
    public Long refundByCardSD(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoToRefund, PaymentGateway paymentGateway)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPaymentSD(customerAccount, ctsAmount, aoToRefund, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, PaymentMethodEnum.CARD);
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
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private PaymentResponseDto makeTheRealPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
    		String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType,Long aoPaymentId)
    				throws Exception,BusinessException {
    	
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        PaymentMethod preferredMethod = null;
        OperationCategoryEnum operationCat = isPayment ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT;
        
        
        try {
            boolean isNewCard = !StringUtils.isBlank(cardNumber);
            AccountOperation aoToPayRefund = accountOperationService.findById(aoIdsToPay.get(0));
            preferredMethod = customerAccountService.getPreferredPaymentMethod(aoToPayRefund, paymentMethodType);
            if (preferredMethod instanceof HibernateProxy) {
                preferredMethod = (PaymentMethod) ((HibernateProxy) preferredMethod).getHibernateLazyInitializer()
                        .getImplementation();
            }
            
            if (!isNewCard) {
                if (preferredMethod == null) {
                    throw new PaymentException(PaymentErrorEnum.NO_PAY_METHOD_FOR_CA, "There no payment method for customerAccount:" + customerAccount.getCode());
                }
            }
            GatewayPaymentInterface gatewayPaymentInterface = null;
            PaymentGateway matchedPaymentGatewayForTheCA = null;
            String pgCode = paymentGateway != null ? paymentGateway.getCode() : null;
            if(isNewCard) {
            	matchedPaymentGatewayForTheCA = paymentGatewayService.getAndCheckPaymentGateway(customerAccount, null ,cardType,aoToPayRefund.getSeller(),pgCode);
            	
            }else {
            	matchedPaymentGatewayForTheCA = paymentGatewayService.getAndCheckPaymentGateway(customerAccount, preferredMethod ,cardType,aoToPayRefund.getSeller(),pgCode);
            }
                        
            if (matchedPaymentGatewayForTheCA == null) {
                throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
            }
            paymentGateway = matchedPaymentGatewayForTheCA;
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
             
            PaymentErrorTypeEnum errorType = null;
            
			if (isNewCard && "true".equals(paramBeanFactory.getInstance().getProperty("paymentCard.saveCard.onPayment", "false"))) {
				String tokenId = gatewayPaymentInterface.createCardToken(customerAccount, (cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber), cardNumber, ownerName, expiryDate, null, cardType);
				preferredMethod = addCardFromPayment(tokenId, customerAccount, cardNumber, cardType, ownerName, cvv, expiryDate);				
			}
			
			  preferredMethod.setUserId(doPaymentResponseDto.getCodeClientSide());
	            preferredMethod = paymentMethodService.update(preferredMethod);
	            
            if (PaymentMethodEnum.CARD == paymentMethodType) {
            	if(!isNewCard) {
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
                if (preferredMethod instanceof HibernateProxy) {
                    preferredMethod = (PaymentMethod) ((HibernateProxy) preferredMethod).getHibernateLazyInitializer()
                            .getImplementation();
                }
                if (!(preferredMethod instanceof DDPaymentMethod)) {
                    throw new PaymentException(PaymentErrorEnum.PAY_METHOD_IS_NOT_DD, "Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
                }
                if (StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getMandateIdentification()) 
                		&& StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getTokenId()) ) {
                    throw new PaymentException(PaymentErrorEnum.PAY_SEPA_MANDATE_BLANK, "Can not process payment sepa as Mandate or token is blank");
                }
                if (isPayment) {
                    doPaymentResponseDto = gatewayPaymentInterface.doPaymentSepa(((DDPaymentMethod) preferredMethod), ctsAmount, null);
                } else {
                    doPaymentResponseDto = gatewayPaymentInterface.doRefundSepa(((DDPaymentMethod) preferredMethod), ctsAmount, additionalParams);
                }
            }
            
            if(PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.NOT_PROCESSED == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()){
            	throw new BusinessException(StringUtils.isBlank(doPaymentResponseDto.getErrorMessage())?doPaymentResponseDto.getErrorCode():doPaymentResponseDto.getErrorMessage());
            }
             
			Refund refund = (!isPayment && aoPaymentId != null) ? refundService.findById(aoPaymentId) : null;
			Payment payment = (isPayment && aoPaymentId != null) ? findById(aoPaymentId) : null;
			AccountOperation accountOperation =accountOperationService.findById(aoPaymentId);
			accountOperation.setReference(doPaymentResponseDto.getPaymentID());
			accountOperationService.update(accountOperation);
            // Le fait de mettre dans une nouvelle transaction crée de probleme de transaction et evite de créer les AO refund (utilisé dans ce payHistory)
            // avec addHistoryInNewTransaction nous aurons des erreurs comme : ERROR: insert or update on table "ar_payment_history" violates foreign key constraint "fk_payhisto_ao_refund"

			if(payment != null) {
				payment.setReference(doPaymentResponseDto.getPaymentID());
			}
			if(refund != null) {
				refund.setReference(doPaymentResponseDto.getPaymentID());
			}

			paymentHistoryService.addHistory(customerAccount, payment, refund, ctsAmount, doPaymentResponseDto.getPaymentStatus(),doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage(),
                    doPaymentResponseDto.getPaymentID(), errorType, operationCat, paymentGateway.getCode(), preferredMethod,aoIdsToPay);

        } catch (PaymentException e) {
            log.error("PaymentException during payment AO:", e);
            processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, e.getCode(), e.getMessage(),aoIdsToPay);
            throw e;
        }catch (BusinessException e) {
            log.error("Payment not persisted: ", e);
            processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat,null, e.getMessage(),aoIdsToPay);
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("Error during payment AO:", e);
            processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, null, e.getMessage(),aoIdsToPay);
            throw new BusinessException(e.getMessage());
        }
        return doPaymentResponseDto;
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private PaymentResponseDto createAO(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
    	 Long aoPaymentId = null;
    	 PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
    	 if (createAO) {

             if (isPayment) {
                 aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
             } else {
                 aoPaymentId = refundService.createRefundAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
             }
             doPaymentResponseDto.setAoCreated(true);
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public PaymentResponseDto doPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
			String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType)
			throws Exception {

		PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();

		doPaymentResponseDto = createAO(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, isPayment,
				paymentMethodType);
		
		Long aoId = doPaymentResponseDto.getAccountOperationId();

		if (!createAO || !StringUtils.isBlank(aoId)) {
			doPaymentResponseDto = makeTheRealPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate,
					cardType, isPayment, paymentMethodType, aoId);
			if (doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.ACCEPTED || doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.PENDING) {					
				updatePaymentAO(aoId, doPaymentResponseDto);
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
			ao.setType(doPaymentResponseDto.getPaymentBrand());
			ao.setBankReference(doPaymentResponseDto.getBankRefenrence());
			ao.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
			ao.setReference(doPaymentResponseDto.getPaymentID());
			accountOperationService.updateNoCheck(ao);
		}
	}

	/**
     * Do payment or refund by token or card for Security Deposit.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param paymentGateway the set this payment gateway will be used.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param isPayment if true is a payment else is a refund.
     * @param paymentMethodType payment method to use, CARD or DIRECTDEIBT.
     * @return id Refund
     */
    public Long doPaymentSD(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, PaymentGateway paymentGateway,
            String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, PaymentMethodEnum paymentMethodType)
            {        
        
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        PaymentMethod preferredMethod = null;
        OperationCategoryEnum operationCat = OperationCategoryEnum.DEBIT;
        
        Long aoPaymentId = null;        
    
        boolean isNewCard = !StringUtils.isBlank(cardNumber);
        AccountOperation aoToPayRefund = accountOperationService.findById(aoIdsToPay.get(0));
        preferredMethod = customerAccountService.getPreferredPaymentMethod(aoToPayRefund, paymentMethodType);
        if (preferredMethod instanceof HibernateProxy) {
            preferredMethod = (PaymentMethod) ((HibernateProxy) preferredMethod).getHibernateLazyInitializer()
                    .getImplementation();
        }
        
        if (!isNewCard) {
            if (preferredMethod == null) {
                throw new PaymentException(PaymentErrorEnum.NO_PAY_METHOD_FOR_CA, "There no payment method for customerAccount:" + customerAccount.getCode());
            }
        }
        GatewayPaymentInterface gatewayPaymentInterface = null;
        PaymentGateway matchedPaymentGatewayForTheCA = null;
        if(isNewCard) {
            matchedPaymentGatewayForTheCA = paymentGatewayService.getAndCheckPaymentGateway(customerAccount, null ,cardType,null,paymentGateway.getCode());
            
        }else {
            matchedPaymentGatewayForTheCA = paymentGatewayService.getAndCheckPaymentGateway(customerAccount, preferredMethod ,cardType,null,paymentGateway.getCode());
        }
                    
        if (matchedPaymentGatewayForTheCA == null) {
            throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
        }
        paymentGateway = matchedPaymentGatewayForTheCA;
        try {
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
        } catch (Exception e) {
            log.warn("Cant get gatewayPaymentFactory :", e);
        }
        
        PaymentErrorTypeEnum errorType = null;
        
        if (isNewCard && "true".equals(paramBeanFactory.getInstance().getProperty("paymentCard.saveCard.onPayment", "false"))) {
            String tokenId = gatewayPaymentInterface.createCardToken(customerAccount, (cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber), cardNumber, ownerName, expiryDate, null, cardType);
            preferredMethod = addCardFromPayment(tokenId, customerAccount, cardNumber, cardType, ownerName, cvv, expiryDate);               
        }
        Refund refund = new Refund();
        try {
            aoPaymentId = refundService.createSDRefundAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay, refund);
            doPaymentResponseDto.setAoCreated(true);
        } catch (Exception e) {
            log.warn("Cant create Account operation payment :", e);
        }

        try {
            List<Long> aoIdsToMatch = new ArrayList<Long>();
            aoIdsToMatch.addAll(aoIdsToPay);
            aoIdsToMatch.add(aoPaymentId);
            matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);
            doPaymentResponseDto.setMatchingCreated(true);
        } catch (BusinessException e) {
            throw new BusinessException(e);
        } catch (MeveoApiException | NoAllOperationUnmatchedException | UnbalanceAmountException e) {
            throw new MeveoApiException(e);
        }
        
        if (PaymentMethodEnum.CARD == paymentMethodType) {
            if(!isNewCard) {
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
                doPaymentResponseDto = gatewayPaymentInterface.doRefundToken(((CardPaymentMethod) preferredMethod), ctsAmount, null);
            }
            else{
                doPaymentResponseDto = gatewayPaymentInterface.doRefundCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
            } 
        }
        
        if (PaymentMethodEnum.DIRECTDEBIT == paymentMethodType) {
            Map<String, Object> additionalParams=new HashedMap<String, Object>();
            additionalParams.put("customerAccountCode", customerAccount.getCode());  
            additionalParams.put("aoToPayOrRefund", aoIdsToPay.get(0));
            additionalParams.put("createdAO", aoPaymentId);
            if (preferredMethod instanceof HibernateProxy) {
                preferredMethod = (PaymentMethod) ((HibernateProxy) preferredMethod).getHibernateLazyInitializer()
                        .getImplementation();
            }
            if (!(preferredMethod instanceof DDPaymentMethod)) {
                throw new PaymentException(PaymentErrorEnum.PAY_METHOD_IS_NOT_DD, "Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
            }
            if (StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getMandateIdentification())) {
                throw new PaymentException(PaymentErrorEnum.PAY_SEPA_MANDATE_BLANK, "Can not process payment sepa as Mandate is blank");
            }
            doPaymentResponseDto = gatewayPaymentInterface.doRefundSepa(((DDPaymentMethod) preferredMethod), ctsAmount, additionalParams);
        }
        
        if(PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.NOT_PROCESSED == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()){
            throw new BusinessException(doPaymentResponseDto.getErrorCode());
        }
        
        paymentHistoryService.addHistory(customerAccount, null, refund, ctsAmount, doPaymentResponseDto.getPaymentStatus(),doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage(),
                doPaymentResponseDto.getPaymentID(), errorType, operationCat, paymentGateway.getCode(), preferredMethod,aoIdsToPay);
        
        return aoPaymentId;
    }

    private PaymentResponseDto processPaymentException(CustomerAccount customerAccount, Long ctsAmount, PaymentGateway paymentGateway, PaymentResponseDto doPaymentResponseDto,
            PaymentMethod preferredMethod, OperationCategoryEnum operationCat, String code, String msg,List<Long> aoIdsToPay) throws BusinessException {
        if (doPaymentResponseDto == null) {
            doPaymentResponseDto = new PaymentResponseDto();
        }
        doPaymentResponseDto.setErrorMessage(msg);
        doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
        doPaymentResponseDto.setErrorCode(code);
        paymentHistoryService.addHistoryInNewTransaction(customerAccount, null, null, ctsAmount, PaymentStatusEnum.ERROR, code, msg, doPaymentResponseDto.getPaymentID(),
                PaymentErrorTypeEnum.ERROR, operationCat, paymentGateway == null ? "notFound" : paymentGateway.getCode(), preferredMethod,aoIdsToPay);
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
        if (paymentMethodType == PaymentMethodEnum.STRIPEDIRECTLINK) {
            occTemplateCode = paramBean.getProperty("occ.payment.stp", "PAY_STP");
        }
        if (paymentMethodType == PaymentMethodEnum.PAYPALPAYMENTLINK) {
            occTemplateCode = paramBean.getProperty("occ.payment.pal", "PAY_PAL");
        }


        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        Payment payment = new Payment();
        calculateAmountsByTransactionCurrency(payment, customerAccount, (new BigDecimal(ctsAmount).divide(new BigDecimal(100))),
                null, new Date());
        payment.setJournal(occTemplate.getJournal());
        payment.setPaymentMethod(paymentMethodType);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(occTemplate.getDescription());
        payment.setType(doPaymentResponseDto.getPaymentBrand());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(doPaymentResponseDto.getPaymentID());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setCollectionDate(new Date());
        payment.setAccountingDate(new Date());
        payment.setBankReference(doPaymentResponseDto.getBankRefenrence());
        payment.setCollectionDate(new Date());
        payment.setAccountingDate(new Date());
        setSumAndOrdersNumber(payment, aoIdsToPay);
        accountOperationService.handleAccountingPeriods(payment);
        create(payment);
        return payment.getId();

    }

    /**
     * Create the payment account operation for the payment that was processed (Security Deposit).
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param doPaymentResponseDto payment responsse dto
     * @param paymentMethodType payment method used
     * @param aoIdsToPay list AO to paid
     * @return the AO id created
     * @throws BusinessException business exception.
     */    
    public Long createSDPaymentAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto, PaymentMethodEnum paymentMethodType,
            List<Long> aoIdsToPay) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String occTemplateCode = paramBean.getProperty("occ.payment.ref_sd", "REF_SD");
        
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        Payment payment = new Payment();
        calculateAmountsByTransactionCurrency(payment, customerAccount, (new BigDecimal(ctsAmount).divide(new BigDecimal(100))),
                null, new Date());
        payment.setPaymentMethod(paymentMethodType);
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
        setSumAndOrdersNumber(payment, aoIdsToPay);
        accountOperationService.handleAccountingPeriods(payment);
        create(payment);
        return payment.getId();
    }

    public void setSumAndOrdersNumber(Payment payment, List<Long> aoIdsToPay) {
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
        paymentMethod.setAlias("Card_" + (cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber));
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
     * @param paymentReference payment reference
     * @param paymentStatus payment Status
     * @param errorCode error Code
     * @param errorMessage error Message
     * @throws BusinessException Business Exception
     */
    public void paymentCallback(String paymentReference, PaymentStatusEnum paymentStatus, String errorCode, String errorMessage) throws BusinessException {       
        try {
            if (paymentStatus == null) {
                throw new BusinessException("paymentStatus is required");
            }
            if (StringUtils.isBlank(paymentReference)) {
                throw new BusinessException("paymentReference is required");
            }
            AccountOperation aoPaymentToReject = accountOperationService.findByReference(paymentReference);

            if (aoPaymentToReject == null) {
                throw new BusinessException("Payment " + paymentReference + " not found");
            }
            if (aoPaymentToReject.getMatchingStatus() != MatchingStatusEnum.L && aoPaymentToReject.getMatchingStatus() != MatchingStatusEnum.P) {
                throw new BusinessException("CallBack unexpected  for payment " + paymentReference);
            }
            if (PaymentStatusEnum.ACCEPTED == paymentStatus) {
                log.debug("Payment ok, nothing to do.");
            } else {
               
                List<AccountOperation> listAoThatSupposedPaid = getAccountOperationThatWasPaid(aoPaymentToReject);
                OCCTemplate occTemplate = getOCCTemplateRejectPayment(aoPaymentToReject);
				CustomerAccount ca = aoPaymentToReject.getCustomerAccount();
				for (AccountOperation aoWasPaid : listAoThatSupposedPaid) {

					matchingCodeService.unmatchingByAOid(aoWasPaid.getId());
				}

                RejectedPayment rejectedPayment = new RejectedPayment();
                rejectedPayment.setType("R");
                rejectedPayment.setMatchingAmount(BigDecimal.ZERO);
                rejectedPayment.setMatchingStatus(MatchingStatusEnum.O);
                rejectedPayment.setUnMatchingAmount(aoPaymentToReject.getUnMatchingAmount());
                rejectedPayment.setAmount(aoPaymentToReject.getUnMatchingAmount());
                rejectedPayment.setReference("r_" + paymentReference);
                rejectedPayment.setCustomerAccount(ca);
                rejectedPayment.setAccountingCode(occTemplate.getAccountingCode());
                rejectedPayment.setCode(occTemplate.getCode());
                rejectedPayment.setDescription(occTemplate.getDescription());
                rejectedPayment.setTransactionCategory(occTemplate.getOccCategory());
                rejectedPayment.setAccountCodeClientSide(aoPaymentToReject.getAccountCodeClientSide());
                rejectedPayment.setPaymentMethod(aoPaymentToReject.getPaymentMethod());
                rejectedPayment.setTaxAmount(aoPaymentToReject.getTaxAmount());
                rejectedPayment.setAmountWithoutTax(aoPaymentToReject.getAmountWithoutTax());
                rejectedPayment.setOrderNumber(aoPaymentToReject.getOrderNumber());
                rejectedPayment.setRejectedType(RejectedType.A);
                rejectedPayment.setRejectedDate(new Date());
                rejectedPayment.setTransactionDate(new Date());
                rejectedPayment.setDueDate(aoPaymentToReject.getDueDate());
                rejectedPayment.setRejectedDescription(errorMessage);
                rejectedPayment.setRejectedCode(errorCode);
                rejectedPayment.setListAaccountOperationSupposedPaid(listAoThatSupposedPaid);

                accountOperationService.create(rejectedPayment);
                for(AccountOperation ao : listAoThatSupposedPaid) {
                    ao.setRejectedPayment(rejectedPayment);
                }
                Long oARejectPaymentID = rejectedPayment.getId();

                List<Long> aos = new ArrayList<>();
                aos.add(aoPaymentToReject.getId());
                aos.add(oARejectPaymentID);

                matchingCodeService.matchOperations(ca.getId(), null, aos, null);
            }
            PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(paymentReference);
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
        log.info("paymentOrRefund.reference:{}",paymentOrRefund.getReference());
        List<MatchingAmount> matchingAmounts = paymentOrRefund.getMatchingAmounts();
        log.debug("matchingAmounts:{}" , matchingAmounts);
        for(MatchingAmount maPay : matchingAmounts) {        	  
        	 for (MatchingAmount ma : maPay.getMatchingCode().getMatchingAmounts()) {
                 log.debug("ma.getAccountOperation() id:{} , occ code:{}", ma.getAccountOperation().toString(), ma.getAccountOperation().getCode());
                 if (!(ma.getAccountOperation() instanceof Payment) && !(ma.getAccountOperation() instanceof Refund) && !(ma.getAccountOperation() instanceof RejectedPayment) ) {
                     listAoThatSupposedPaid.add(ma.getAccountOperation());
                 }
             }
        }      
        
        if (listAoThatSupposedPaid.isEmpty()) {
            throw new BusinessException("Cant find invoice account operation to unmatching");
        }
        return listAoThatSupposedPaid;

    }

    private OCCTemplate getOCCTemplateRejectPayment(AccountOperation accountOperation) {
    	ParamBean paramBean = paramBeanFactory.getInstance();
    	String occTemplateCode = null;
        if (accountOperation instanceof AutomatedRefund || accountOperation instanceof Refund) {
            if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedRefund.card", "REJ_RCR");
            } else {
                occTemplateCode = paramBean.getProperty("occ.rejectedRefund.dd", "REJ_RDD");
            }
        } else if (accountOperation instanceof Payment) {
            if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedPayment.card", "REJ_CRD");
            } 
           if(PaymentMethodEnum.DIRECTDEBIT == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedPayment.dd", "REJ_DDT");
            }
           if(PaymentMethodEnum.CHECK == accountOperation.getPaymentMethod()) {
               occTemplateCode = paramBean.getProperty("occ.rejectedPayment.chk", "REJ_CHK");
           }
           if(PaymentMethodEnum.WIRETRANSFER == accountOperation.getPaymentMethod()) {
               occTemplateCode = paramBean.getProperty("occ.rejectedPayment.chk", "REJ_WTF");
           }
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find AO Template with code:" + occTemplateCode);
        }
        return occTemplate;
        
    }
    
    public int updatePaymentReference() {
        return getEntityManager().createNamedQuery("Payment.updateReference").executeUpdate();
    }
    public int updateRefundReference() {
        return getEntityManager().createNamedQuery("Refund.updateReference").executeUpdate();
    }

    public void calculateAmountsByTransactionCurrency(Payment payment, CustomerAccount customerAccount,
                                                      BigDecimal amount,
                                                      String transactionalCurrencyCode, Date transactionDate) {
        TradingCurrency functionalCurrency = appProvider.getCurrency() != null && appProvider.getCurrency().getCurrencyCode() != null ?
                tradingCurrencyService.findByTradingCurrencyCode(appProvider.getCurrency().getCurrencyCode()) : null;
        TradingCurrency transactionalCurrency = customerAccount != null ? customerAccount.getTradingCurrency() : null;

        BigDecimal lastApliedRate = BigDecimal.ONE;
        Date transactionDateToUse = transactionDate == null ? new Date() : transactionDate;
        BigDecimal functionalAmount = amount;
        BigDecimal transactionalAmount = amount;
        if (StringUtils.isNotBlank(transactionalCurrencyCode)) {
            transactionalCurrency = tradingCurrencyService.findByTradingCurrencyCode(transactionalCurrencyCode);
            checkTransactionalCurrency(transactionalCurrencyCode, transactionalCurrency);
        }

        if (functionalCurrency != null && transactionalCurrency != null && !functionalCurrency.equals(transactionalCurrency)) {
            ExchangeRate exchangeRate = getExchangeRate(transactionalCurrency, transactionDateToUse);
            if (!Objects.equals(exchangeRate.getExchangeRate(), BigDecimal.ZERO)) {
                functionalAmount = transactionalAmount.divide(exchangeRate.getExchangeRate(),
                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
                lastApliedRate = exchangeRate.getExchangeRate();
            }
        } else {
            transactionalAmount = toTransactional(amount, lastApliedRate);
        }

        payment.setCustomerAccount(customerAccount);
        payment.setAmount(functionalAmount);
        payment.setUnMatchingAmount(functionalAmount);
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setTransactionalMatchingAmount(BigDecimal.ZERO);
        payment.setTransactionalAmount(transactionalAmount);
        payment.setTransactionalAmountWithoutTax(transactionalAmount);
        payment.setAmountWithoutTax(functionalAmount);
        payment.setTransactionalUnMatchingAmount(transactionalAmount);
        payment.setTransactionDate(transactionDateToUse);
        payment.setTransactionalCurrency(transactionalCurrency != null ? transactionalCurrency : functionalCurrency);
        payment.setAppliedRate(lastApliedRate);
    }
    private void checkTransactionalCurrency(String transactionalcurrency, TradingCurrency tradingCurrency) {
        if (tradingCurrency == null || StringUtils.isBlank(tradingCurrency)) {
            throw new InvalidParameterException("Currency " + transactionalcurrency +
                    " is not recorded a trading currency in Opencell. Only currencies declared as trading currencies can be used to record account operations.");
        }
    }
    private ExchangeRate getExchangeRate(TradingCurrency tradingCurrency, Date transactionDate) {
        Date exchangeDate = transactionDate != null ? transactionDate : new Date();
        ExchangeRate exchangeRate = tradingCurrency.getExchangeRate(exchangeDate);
        if (exchangeRate == null || exchangeRate.getExchangeRate() == null) {
            throw new EntityDoesNotExistsException("No valid exchange rate found for currency " + tradingCurrency.getCurrencyCode()
                    + " on " + exchangeDate);
        }
        return exchangeRate;
    }
    private BigDecimal toTransactional(BigDecimal amount, BigDecimal rate) {
        return amount != null ? amount.multiply(rate) : BigDecimal.ZERO;
    }
}
