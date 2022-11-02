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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MandatStateEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.util.PaymentGatewayClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingenico.connect.gateway.sdk.java.ApiException;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.Marshaller;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.DefaultMarshaller;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.BankAccountIban;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Card;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.CardWithoutCvv;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.CompanyInformation;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.ContactDetailsBase;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.OrderStatusOutput;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.PaymentProductFilter;
import com.ingenico.connect.gateway.sdk.java.domain.errors.definitions.APIError;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.PaymentProductFiltersHostedCheckout;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.CreateMandateRequest;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.GetMandateResponse;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateAddress;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateContactDetails;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateCustomer;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandatePersonalInformation;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandatePersonalName;
import com.ingenico.connect.gateway.sdk.java.domain.mandates.definitions.MandateResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentRequest;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.PaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.BrowserData;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.CardPaymentMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.CardPaymentMethodSpecificInputBase;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.CardRecurrenceDetails;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.CustomerDevice;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.OrderReferences;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Payment;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.PaymentStatusOutput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.PersonalName;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.RedirectionData;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.SepaDirectDebitPaymentMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.SepaDirectDebitPaymentProduct771SpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.ThreeDSecure;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.ThreeDSecureBase;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.ThreeDSecureData;
import com.ingenico.connect.gateway.sdk.java.domain.payout.CreatePayoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.payout.PayoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payout.definitions.CardPayoutMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payout.definitions.PayoutCustomer;
import com.ingenico.connect.gateway.sdk.java.domain.payout.definitions.PayoutDetails;
import com.ingenico.connect.gateway.sdk.java.domain.payout.definitions.PayoutReferences;
import com.ingenico.connect.gateway.sdk.java.domain.token.ApproveTokenRequest;
import com.ingenico.connect.gateway.sdk.java.domain.token.CreateTokenRequest;
import com.ingenico.connect.gateway.sdk.java.domain.token.CreateTokenResponse;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.ContactDetailsToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.CustomerToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.CustomerTokenWithContactDetails;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.Debtor;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.MandateSepaDirectDebitWithoutCreditor;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.PersonalInformationToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.PersonalNameToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.TokenCard;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.TokenCardData;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.TokenSepaDirectDebitWithoutCreditor;

/**
 * The Class IngenicoGatewayPayment.
 *
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.5.2 
 */
@PaymentGatewayClass
public class IngenicoGatewayPayment implements GatewayPaymentInterface {

    /** The log. */
    protected Logger log = LoggerFactory.getLogger(IngenicoGatewayPayment.class);
    
    /** The payment gateway. */
    private PaymentGateway paymentGateway = null; 
        
    /** The client. */
    private  Client client = null;
    
    private Marshaller marshaller = null;
    
    private ScriptInstanceService scriptInstanceService = null;

    /**
     * Connect.
     */
    private void connect() {
        ParamBean paramBean = paramBean();
        //Init properties
        paramBean.getProperty("connect.api.authorizationType", "changeIt");
        paramBean.getProperty("connect.api.connectTimeout", "5000");
        paramBean.getProperty("connect.api.endpoint.host", "changeIt");
        paramBean.getProperty("connect.api.endpoint.scheme", "changeIt");
        paramBean.getProperty("connect.api.integrator", "");
        paramBean.getProperty("connect.api.socketTimeout", "300000");        
        CommunicatorConfiguration communicatorConfiguration = new CommunicatorConfiguration(ParamBean.getInstance().getProperties());
        communicatorConfiguration.setApiKeyId(paymentGateway.getApiKey());
        communicatorConfiguration.setSecretApiKey(paymentGateway.getSecretKey());
        client = Factory.createClient(communicatorConfiguration);
        marshaller = DefaultMarshaller.INSTANCE;
    }

    private ParamBean paramBean() {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        ParamBean paramBean = paramBeanFactory.getInstance();
        return paramBean;
    }

    private ScriptInstanceService getScriptInstanceService() {
    	if(scriptInstanceService != null) {
    		return scriptInstanceService;
    	}
    	scriptInstanceService = (ScriptInstanceService) EjbUtils.getServiceInterface(ScriptInstanceService.class.getSimpleName());
    	return scriptInstanceService;
    }
    /**
     * Gets the client.
     *
     * @return the client
     */
    private  Client getClient() {
        if (client == null) {
            connect();
        }
        return client;
    }

    /**
     * Gets the client object
     *
     * @return the client object
     */
    @Override
    public  Object getClientObject() {
        if (client == null) {
            connect();
        }
        return client;
    }

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType) throws BusinessException {
        try {
            CompanyInformation companyInformation = new CompanyInformation();
            companyInformation.setName(customerAccount.getCode());

            PersonalNameToken name = new PersonalNameToken();
            if (customerAccount.getName() != null) {
                name.setFirstName(customerAccount.getName().getFirstName());
                name.setSurname(customerAccount.getName().getLastName());
                name.setSurnamePrefix(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getCode());
            }

            PersonalInformationToken personalInformation = new PersonalInformationToken();
            personalInformation.setName(name);

            CustomerToken customerToken = new CustomerToken();
            customerToken.setBillingAddress(getBillingAddress(customerAccount));
            customerToken.setCompanyInformation(companyInformation);
            customerToken.setMerchantCustomerId(customerAccount.getCode());
            customerToken.setPersonalInformation(personalInformation);

            CardWithoutCvv cardWithoutCvv = new CardWithoutCvv();
            cardWithoutCvv.setCardholderName(cardHolderName);
            cardWithoutCvv.setCardNumber(cardNumber);
            cardWithoutCvv.setExpiryDate(expirayDate);
            cardWithoutCvv.setIssueNumber(issueNumber);

            TokenCardData tokenCardData = new TokenCardData();
            tokenCardData.setCardWithoutCvv(cardWithoutCvv);

            TokenCard tokenCard = new TokenCard();
            tokenCard.setAlias(alias);
            tokenCard.setCustomer(customerToken);
            tokenCard.setData(tokenCardData);

            CreateTokenRequest body = new CreateTokenRequest();
            body.setCard(tokenCard);
            body.setPaymentProductId(cardType.getId());    
          
            CreateTokenResponse response = getClient().merchant(paymentGateway.getMarchandId()).tokens().create(body);
            if (!response.getIsNewToken()) {
                throw new BusinessException("A token already exist for card:" + CardPaymentMethod.hideCardNumber(cardNumber));
            }
            return response.getToken();
        } catch (ApiException ev) {
            throw new BusinessException(ev.getResponseBody());

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentCardToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPayment(null, paymentCardToken, ctsAmount, paymentCardToken.getCustomerAccount(), null, null, null, null, null, null, additionalParams);
    }

    @Override
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        return doPayment(null, null, ctsAmount, customerAccount, cardNumber, ownerName, cvv, expirayDate, cardType, countryCode, additionalParams);
    }
    
    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod ddPaymentMethod, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPayment(ddPaymentMethod, null, ctsAmount, ddPaymentMethod.getCustomerAccount(), null, null, null, null, null, null, additionalParams);
    }

    /**
     * Do payment.
     *
     * @param ddPaymentMethod the dd payment method
     * @param paymentCardToken the payment card token
     * @param ctsAmount the cts amount
     * @param customerAccount the customer account
     * @param cardNumber the card number
     * @param ownerName the owner name
     * @param cvv the cvv
     * @param expirayDate the expiray date
     * @param cardType the card type
     * @param countryCode the country code
     * @param additionalParams the additional params
     * @return the payment response dto
     * @throws BusinessException the business exception
     */
    private PaymentResponseDto doPayment(DDPaymentMethod ddPaymentMethod, CardPaymentMethod paymentCardToken,
			Long ctsAmount, CustomerAccount customerAccount, String cardNumber, String ownerName, String cvv,
			String expirayDate, CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams)
			throws BusinessException {
		PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
		doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED);
		try {

			CreatePaymentRequest body = buildPaymentRequest(ddPaymentMethod, paymentCardToken, ctsAmount,
					customerAccount, cardNumber, ownerName, cvv, expirayDate, cardType);

			CreatePaymentResponse response = getClient().merchant(paymentGateway.getMarchandId()).payments()
					.create(body);

			if (response != null && response.getPayment() != null) {
				log.info("doPayment RESPONSE :" + marshaller.marshal(response));
				Payment paymentResponse = response.getPayment();
				doPaymentResponseDto.setPaymentID(paymentResponse.getId());
				doPaymentResponseDto.setPaymentStatus(mappingStaus(paymentResponse.getStatus()));
				if (response.getCreationOutput() != null) {
					doPaymentResponseDto.setTransactionId(response.getCreationOutput().getExternalReference());
					doPaymentResponseDto.setTokenId(response.getCreationOutput().getToken());
					doPaymentResponseDto.setNewToken(response.getCreationOutput().getIsNewToken() == null ? false
							: response.getCreationOutput().getIsNewToken());
				}

				if (paymentResponse.getStatusOutput() != null && paymentResponse.getStatusOutput().getErrors() != null) {
					PaymentStatusOutput statusOutput = paymentResponse.getStatusOutput();
						List<APIError> errors = statusOutput.getErrors();
						if (CollectionUtils.isNotEmpty(errors)) {
							doPaymentResponseDto.setErrorMessage(errors.toString());
							doPaymentResponseDto.setErrorCode(errors.get(0).getId());
						}					
				}
				return doPaymentResponseDto;
			} else {
				throw new BusinessException("Gateway response is null");
			}
		} catch (ApiException e) {
			log.error("Error on doPayment :", e);
			doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
			doPaymentResponseDto.setErrorMessage(e.getResponseBody());
			if (CollectionUtils.isNotEmpty(e.getErrors())) {
				doPaymentResponseDto.setErrorCode(e.getErrors().get(0).getId());
			}
		}
		return doPaymentResponseDto;
	}
    
    private CreatePaymentRequest buildPaymentRequest(DDPaymentMethod ddPaymentMethod, CardPaymentMethod paymentCardToken, Long ctsAmount, CustomerAccount customerAccount,
			String cardNumber, String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType) {

		CreatePaymentRequest body = new CreatePaymentRequest();
		String scriptInstanceCode = paramBean().getProperty("ingenico.paymentRequest.script", null);
		if (StringUtils.isBlank(scriptInstanceCode)) {
			AmountOfMoney amountOfMoney = new AmountOfMoney();
			amountOfMoney.setAmount(ctsAmount);
			amountOfMoney.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());

			Customer customer = new Customer();
			customer.setBillingAddress(getBillingAddress(customerAccount));
			if("true".equals(paramBean().getProperty("ingenico.CreatePayment.includeDeviceData", "true"))) {
				customer.setDevice(getDeviceData());	
			}

			Order order = new Order();
			order.setAmountOfMoney(amountOfMoney);
			order.setCustomer(customer);

			if (ddPaymentMethod != null) {
				body.setSepaDirectDebitPaymentMethodSpecificInput(getSepaInput(ddPaymentMethod));
			}
			if (paymentCardToken != null) {
				body.setCardPaymentMethodSpecificInput(getCardTokenInput(paymentCardToken));
			}
			if (!StringUtils.isBlank(cardNumber)) {
				body.setCardPaymentMethodSpecificInput((getCardInput(cardNumber, ownerName, cvv, expirayDate, cardType)));
			}

			body.setOrder(order);
		} else {
			Map<String, Object> context = new  HashMap<String, Object>();
			context.put("DDPaymentMethod", ddPaymentMethod);
			context.put("CardPaymentMethod", paymentCardToken);
			context.put("ctsAmount", ctsAmount);
			context.put("CustomerAccount", customerAccount);
			context = getScriptInstanceService().executeCached( scriptInstanceCode, context);
			body = (CreatePaymentRequest)context.get("CreatePaymentRequest");
		}
		return body;
	}

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {       
        try {
            getClient().merchant(paymentGateway.getMarchandId()).payments().cancel(paymentID);
        } catch (ApiException e) {
            throw new BusinessException(e.getResponseBody());
        }
    }

    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();        
        try {
            PaymentResponse paymentResponse = getClient().merchant(paymentGateway.getMarchandId()).payments().get(paymentID);
            if (paymentResponse != null) {
                String errorMessage = "";
                doPaymentResponseDto.setPaymentID(paymentID);
                doPaymentResponseDto.setPaymentStatus(mappingStaus(paymentResponse.getStatus()));
                if (paymentResponse.getStatusOutput() != null) {
                    if (paymentResponse.getStatusOutput().getErrors() != null) {
                        for (APIError apiError : paymentResponse.getStatusOutput().getErrors()) {
                            errorMessage = errorMessage + apiError.getMessage() + "\n";
                        }
                    }
                }
                doPaymentResponseDto.setErrorMessage(errorMessage);
                return doPaymentResponseDto;
            } else {
                throw new BusinessException("Gateway response is null");
            }
        } catch (ApiException e) {
            throw new BusinessException(e.getResponseBody());
        }
    }

    /**
     * Gets the billing address.
     *
     * @param customerAccount the customer account
     * @return the billing address
     */
    private Address getBillingAddress(CustomerAccount customerAccount) {
        Address billingAddress = new Address();
        if (customerAccount.getAddress() != null) {
            billingAddress.setAdditionalInfo(StringUtils.truncate(customerAccount.getAddress().getAddress3(), 50, true));
            billingAddress.setCity(StringUtils.truncate(customerAccount.getAddress().getCity(), 20, true));
            billingAddress.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode());
            billingAddress.setHouseNumber("");
            billingAddress.setState(StringUtils.truncate(customerAccount.getAddress().getState(), 35, true));
            billingAddress.setStreet(StringUtils.truncate(customerAccount.getAddress().getAddress1(), 50, true));
            billingAddress.setZip(StringUtils.truncate(customerAccount.getAddress().getZipCode(), 8, true));
        }
        return billingAddress;
    }

    /**
     * Mapping staus.
     *
     * @param ingenicoStatus the ingenico status
     * @return the payment status enum
     */
    private PaymentStatusEnum mappingStaus(String ingenicoStatus) {
        if (ingenicoStatus == null) {
            return PaymentStatusEnum.ERROR;
        }
        if ("CREATED".equals(ingenicoStatus) || "PAID".equals(ingenicoStatus) || "REFUNDED".equals(ingenicoStatus) || "CAPTURED".equals(ingenicoStatus)) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.startsWith("PENDING")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("ACCOUNT_VERIFIED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("AUTHORIZATION_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("CAPTURE_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("REJECTED_CAPTURE")) {
            return PaymentStatusEnum.REJECTED;
        }
        if (ingenicoStatus.equals("REVERSED")) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.equals("CHARGEBACKED")) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.equals("REFUND_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        if (ingenicoStatus.equals("PAYOUT_REQUESTED")) {
            return PaymentStatusEnum.PENDING;
        }
        
        return PaymentStatusEnum.REJECTED;
    }

    /**
     * Gets the card input.
     *
     * @param cardNumber the card number
     * @param ownerName the owner name
     * @param cvv the cvv
     * @param expirayDate the expiray date
     * @param cardType the card type
     * @return the card input
     */
    private CardPaymentMethodSpecificInput getCardInput(String cardNumber, String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType) {
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setCardholderName(ownerName);
        card.setCvv(cvv);
        card.setExpiryDate(expirayDate);
        cardPaymentMethodSpecificInput.setCard(card);
        cardPaymentMethodSpecificInput.setPaymentProductId(cardType.getId());
        cardPaymentMethodSpecificInput.setAuthorizationMode(getAuthorizationMode()); 
        return cardPaymentMethodSpecificInput;
    }

    
    private CardPaymentMethodSpecificInput getCardTokenInput(CardPaymentMethod cardPaymentMethod) {
    	if("true".equals(paramBean().getProperty("ingenico.CreatePayment.use3DSecure", "true"))) {
    		return getCardTokenInput3dSecure(cardPaymentMethod);
    	}
    	return getCardTokenInputDefault(cardPaymentMethod);
        
    }
    
    /**
     * Gets the card token input.
     *
     * @param cardPaymentMethod the card payment method
     * @return the card token input
     */
    private CardPaymentMethodSpecificInput getCardTokenInput3dSecure(CardPaymentMethod cardPaymentMethod) {
        ParamBean paramBean = paramBean();
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        cardPaymentMethodSpecificInput.setToken(cardPaymentMethod.getTokenId());
        
        ThreeDSecure threeDSecure = new ThreeDSecure();
        ThreeDSecureData threeDSecureData = new ThreeDSecureData();
        threeDSecureData.setAcsTransactionId(cardPaymentMethod.getToken3DsId());
        threeDSecure.setPriorThreeDSecureData(threeDSecureData);
        threeDSecure.setSkipAuthentication(Boolean.valueOf("true".equals(paramBean().getProperty("ingenico.CreatePayment.SkipAuthentication", "false"))));
        RedirectionData redirectionData = new  RedirectionData();
        redirectionData.setReturnUrl(paramBean.getProperty("ingenico.urlReturnPayment", "changeIt"));
        threeDSecure.setRedirectionData(redirectionData);
        threeDSecure.setChallengeIndicator(paramBean.getProperty("ingenico.3ds.ChallengeIndicator", "no-preference"));
        threeDSecure.setAuthenticationFlow(paramBean.getProperty("ingenico.3ds.AuthenticationFlow", "browser"));
        threeDSecure.setChallengeCanvasSize(paramBean.getProperty("ingenico.3ds.ChallengeCanvasSize", "600x400"));
        cardPaymentMethodSpecificInput.setThreeDSecure(threeDSecure);       
        cardPaymentMethodSpecificInput.setIsRecurring(Boolean.valueOf("true".equals(paramBean().getProperty("ingenico.CreatePayment.IsRecurring", "false"))));
     

        cardPaymentMethodSpecificInput.setAuthorizationMode(getAuthorizationMode());        

        cardPaymentMethodSpecificInput.setUnscheduledCardOnFileRequestor(paramBean().getProperty("ingenico.CreatePayment.UnscheduledCardOnFileRequestor", "merchantInitiated"));
        cardPaymentMethodSpecificInput.setUnscheduledCardOnFileSequenceIndicator(paramBean().getProperty("ingenico.CreatePayment.UnscheduledCardOnFileSequenceIndicator", "subsequent"));
        
        CardRecurrenceDetails cardRecurrenceDetails = new CardRecurrenceDetails();					
		cardRecurrenceDetails.setRecurringPaymentSequenceIndicator( paramBean().getProperty("ingenico.CreatePayment.RecurringPaymentSequenceIndicator", "null"));			
		cardPaymentMethodSpecificInput.setRecurring(cardRecurrenceDetails);
		
		
        return cardPaymentMethodSpecificInput;
    }

    
    private CardPaymentMethodSpecificInput getCardTokenInputDefault(CardPaymentMethod cardPaymentMethod) {
        ParamBean paramBean = paramBean();
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        cardPaymentMethodSpecificInput.setToken(cardPaymentMethod.getTokenId());
        cardPaymentMethodSpecificInput.setReturnUrl(paramBean.getProperty("ingenico.urlReturnPayment", "changeIt"));
        cardPaymentMethodSpecificInput.setIsRecurring(Boolean.TRUE);
        cardPaymentMethodSpecificInput.setRecurringPaymentSequenceIndicator("recurring");
        cardPaymentMethodSpecificInput.setAuthorizationMode(getAuthorizationMode());
        return cardPaymentMethodSpecificInput;
    }

    private String getAuthorizationMode() {
        return paramBean().getProperty("ingenico.api.authorizationMode", "SALE");
    }

    /**
     * Gets the sepa input.
     *
     * @param ddPaymentMethod the dd payment method
     * @return the sepa input
     */
    private SepaDirectDebitPaymentMethodSpecificInput getSepaInput(DDPaymentMethod ddPaymentMethod) {
        SepaDirectDebitPaymentMethodSpecificInput sepaPmInput = new SepaDirectDebitPaymentMethodSpecificInput();
        sepaPmInput.setPaymentProductId(771);
        sepaPmInput.setToken(ddPaymentMethod.getTokenId());
        SepaDirectDebitPaymentProduct771SpecificInput sepaDirectDebitPaymentProduct771SpecificInput = new SepaDirectDebitPaymentProduct771SpecificInput();
        sepaDirectDebitPaymentProduct771SpecificInput.setMandateReference(ddPaymentMethod.getMandateIdentification());
        sepaPmInput.setPaymentProduct771SpecificInput(sepaDirectDebitPaymentProduct771SpecificInput);
        return sepaPmInput;
    }


    @Override
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
		PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
		doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED);
    	try {
            CustomerAccount customerAccount = paymentToken.getCustomerAccount();
			AmountOfMoney amountOfMoney = new AmountOfMoney();
			amountOfMoney.setAmount(ctsAmount);
			amountOfMoney.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());

			Address address = getBillingAddress(customerAccount);

			CompanyInformation companyInformation = new CompanyInformation();
			companyInformation.setName(StringUtils.truncate(customerAccount.getCode(), 40, true));
			ContactDetailsBase contactDetails = new ContactDetailsBase();
			
			if(customerAccount.getContactInformation() != null ) {
				contactDetails.setEmailAddress(StringUtils.truncate(customerAccount.getContactInformation().getEmail(), 70, false));
			}
			
			PersonalName name = new PersonalName();
			if (customerAccount.getName() != null) {
				name.setFirstName(StringUtils.truncate(customerAccount.getName().getFirstName(), 15, true));
				name.setSurname(StringUtils.truncate(customerAccount.getName().getLastName(), 70, true));
				name.setTitle(StringUtils.truncate(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getCode(), 15, true));
			}

			PayoutCustomer customer = new PayoutCustomer();
			customer.setAddress(address);
			customer.setCompanyInformation(companyInformation);
			customer.setContactDetails(contactDetails);
			customer.setName(name);

			PayoutReferences references = new PayoutReferences();
			references.setMerchantReference(customerAccount.getId() + "-" + amountOfMoney.getAmount() + "-" + System.currentTimeMillis());
			CardPayoutMethodSpecificInput cardPayoutMethodSpecificInput = new CardPayoutMethodSpecificInput();
			cardPayoutMethodSpecificInput.setToken(paymentToken.getTokenId());
			cardPayoutMethodSpecificInput.setPaymentProductId(paymentToken.getCardType().getId());
			
			PayoutDetails payoutDetails = new PayoutDetails();
			payoutDetails.setAmountOfMoney(amountOfMoney);
			payoutDetails.setCustomer(customer);
			payoutDetails.setReferences(references);

			CreatePayoutRequest body = new CreatePayoutRequest();			
			body.setCardPayoutMethodSpecificInput(cardPayoutMethodSpecificInput);			
			body.setPayoutDetails(payoutDetails);
			
			getClient();
			log.info("REQUEST:"+marshaller.marshal(body));
			PayoutResponse response = client.merchant(paymentGateway.getMarchandId()).payouts().create(body);			
			if (response != null) {
				log.info("RESPONSE:"+marshaller.marshal(response));
				
				doPaymentResponseDto.setPaymentID(response.getId());
				doPaymentResponseDto.setPaymentStatus(mappingStaus(response.getStatus()));
				if (response.getPayoutOutput() != null && response.getPayoutOutput().getReferences() != null) {
					doPaymentResponseDto.setTransactionId(response.getPayoutOutput().getReferences().getPaymentReference());
					doPaymentResponseDto.setBankRefenrence(response.getPayoutOutput().getReferences().getPaymentReference());
				}
				OrderStatusOutput statusOutput = response.getStatusOutput();
				if (statusOutput != null) {
					List<APIError> errors = statusOutput.getErrors();
					if (CollectionUtils.isNotEmpty(errors)) {
						doPaymentResponseDto.setErrorMessage(errors.toString());
						doPaymentResponseDto.setErrorCode(errors.get(0).getId());
					}
				}
				return doPaymentResponseDto;
			} else {
				throw new BusinessException("Gateway response is null");
			}
    	} catch (ApiException e) {
			log.error("Error on doRefundToken :",e);
			doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
			doPaymentResponseDto.setErrorMessage(e.getResponseBody());
			if (CollectionUtils.isNotEmpty(e.getErrors())) {
				doPaymentResponseDto.setErrorCode(e.getErrors().get(0).getId());
			}
		}
		return doPaymentResponseDto;
	}


    @Override
    public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }
   
    @Override
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();

    }

    @Override
    public MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
    	MandatInfoDto mandatInfoDto=new MandatInfoDto();
    	GetMandateResponse response = client.merchant(paymentGateway.getMarchandId()).mandates().get(mandatReference); 
    	MandateResponse mandatResponse=response.getMandate();
    	if(mandatResponse!=null) { 
    		if("WAITING_FOR_REFERENCE".equals(mandatResponse.getStatus())) {
    			mandatInfoDto.setState(MandatStateEnum.waitingForReference); 
    		}else {
    			mandatInfoDto.setState(MandatStateEnum.valueOf(mandatResponse.getStatus().toLowerCase()));
    		}
    		mandatInfoDto.setReference(mandatResponse.getUniqueMandateReference());
    	}  

    	return mandatInfoDto;

    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

	@Override
	public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
		try {
			String returnUrl = hostedCheckoutInput.getReturnUrl();
			Long id = hostedCheckoutInput.getCustomerAccountId();
			String timeMillisWithcustomerAccountId = System.currentTimeMillis() + "_-_" + id;
			
			log.info("hostedCheckoutInput.isOneShotPayment(): "+ hostedCheckoutInput.isOneShotPayment());
			
            boolean isRequiresApproval = "true".equalsIgnoreCase(paramBean().getProperty("ingenico.HostedCheckout.saveCard.RequiresApproval", "true"));
            
            if(hostedCheckoutInput.isOneShotPayment()) {
                timeMillisWithcustomerAccountId = "oneShot_"+timeMillisWithcustomerAccountId;
                isRequiresApproval = "true".equalsIgnoreCase(paramBean().getProperty("ingenico.HostedCheckout.oneShot.RequiresApproval", "false"));
            }

			String redirectionUrl;

			HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
			hostedCheckoutSpecificInput.setLocale(hostedCheckoutInput.getLocale());
			hostedCheckoutSpecificInput.setVariant(hostedCheckoutInput.getVariant());
			hostedCheckoutSpecificInput.setReturnUrl(returnUrl);
			hostedCheckoutSpecificInput.setIsRecurring(false);
			
			PaymentProductFiltersHostedCheckout productFilters =  new PaymentProductFiltersHostedCheckout(); 
			PaymentProductFilter paymentProductFilter = new PaymentProductFilter();
			paymentProductFilter.setProducts(getListProductIds());
			productFilters.setRestrictTo(paymentProductFilter);
			hostedCheckoutSpecificInput.setPaymentProductFilters(productFilters);


			AmountOfMoney amountOfMoney = new AmountOfMoney();
			amountOfMoney.setAmount(Long.valueOf(hostedCheckoutInput.getAmount()));
			amountOfMoney.setCurrencyCode(hostedCheckoutInput.getCurrencyCode());

			Address billingAddress = new Address();
			billingAddress.setCountryCode(hostedCheckoutInput.getCountryCode());

			Customer customer = new Customer();
			customer.setBillingAddress(billingAddress);
			customer.setDevice(getDeviceData());
			

			OrderReferences orderReferences = new OrderReferences();
			orderReferences.setMerchantReference(timeMillisWithcustomerAccountId);

			Order order = new Order();
			order.setAmountOfMoney(amountOfMoney);
			order.setCustomer(customer);
			order.setReferences(orderReferences);

			CardPaymentMethodSpecificInputBase cardPaymentMethodSpecificInputBase = new CardPaymentMethodSpecificInputBase();
			cardPaymentMethodSpecificInputBase.setRequiresApproval(isRequiresApproval);
			cardPaymentMethodSpecificInputBase.setAuthorizationMode(hostedCheckoutInput.getAuthorizationMode());
			cardPaymentMethodSpecificInputBase.setTokenize(true);
			
			AmountOfMoney amountOfMoney3DS = new AmountOfMoney();
			amountOfMoney3DS.setAmount(Long.valueOf(hostedCheckoutInput.getAuthenticationAmount()));
			amountOfMoney3DS.setCurrencyCode(hostedCheckoutInput.getCurrencyCode());

			ThreeDSecureBase threeDSecure = new ThreeDSecureBase();
			threeDSecure.setAuthenticationAmount(amountOfMoney3DS);
			threeDSecure.setSkipAuthentication(hostedCheckoutInput.isSkipAuthentication());
			cardPaymentMethodSpecificInputBase.setThreeDSecure(threeDSecure);

			CardRecurrenceDetails cardRecurrenceDetails = new CardRecurrenceDetails();	
			cardRecurrenceDetails.setRecurringPaymentSequenceIndicator(paramBean().getProperty("ingenico.HostedCheckout.RecurringPaymentSequenceIndicator", ""));			
			cardPaymentMethodSpecificInputBase.setRecurring(cardRecurrenceDetails);

			cardPaymentMethodSpecificInputBase.setUnscheduledCardOnFileRequestor(paramBean().getProperty("ingenico.HostedCheckout.UnscheduledCardOnFileRequestor", "cardholderInitiated"));
			cardPaymentMethodSpecificInputBase.setUnscheduledCardOnFileSequenceIndicator(paramBean().getProperty("ingenico.HostedCheckout.UnscheduledCardOnFileSequenceIndicator", "first"));

			
			CreateHostedCheckoutRequest body = new CreateHostedCheckoutRequest();
			body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
			body.setCardPaymentMethodSpecificInput(cardPaymentMethodSpecificInputBase);
			body.setOrder(order);
			getClient();
			log.info("REQUEST:"+marshaller.marshal(body));
			CreateHostedCheckoutResponse response = client.merchant(paymentGateway.getMarchandId()).hostedcheckouts().create(body);			
			log.info("RESPONSE:"+marshaller.marshal(response));
			redirectionUrl = paramBean().getProperty("ingenico.hostedCheckoutUrl.prefix", "https://payment.") + response.getPartialRedirectUrl();

			return new PaymentHostedCheckoutResponseDto(redirectionUrl, null, null);
		} catch (Exception e) {
			log.error("Error on getHostedCheckoutUrl:",e);
			throw new BusinessException(e.getMessage());
		}
	}



    private List<Integer> getListProductIds() {
    	List<Integer> listProduct = new ArrayList<Integer>();

		String productFilter =  paramBean().getProperty("ingenico.HostedCheckout.ProductFilter", "1,2,3,122,114,119,130");
		for(String s : productFilter.split(",")) {
			listProduct.add(Integer.valueOf(s));
		}
		return listProduct;
	}

	@Override
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
   
	@Override
	public String createInvoice(Invoice invoice) throws BusinessException {
		throw new UnsupportedOperationException();
	}
	
	private CustomerDevice getDeviceData() {		
		CustomerDevice customerDevice = new CustomerDevice();
		customerDevice.setAcceptHeader(paramBean().getProperty("ingenico.device.AcceptHeader", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"));
		customerDevice.setLocale(paramBean().getProperty("ingenico.device.Locale", "fr_FR"));
		customerDevice.setTimezoneOffsetUtcMinutes(paramBean().getProperty("ingenico.device.TimezoneOffsetUtcMinutes", "60"));
		customerDevice.setUserAgent(paramBean().getProperty("ingenico.device.UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"));
		BrowserData browserData = new BrowserData();
		browserData.setColorDepth(paramBean().getPropertyAsInteger("ingenico.device.ColorDepth", 24));
		browserData.setJavaEnabled("true".equals(paramBean().getProperty("ingenico.device.JavaEnabled", "false")));
		browserData.setScreenHeight(paramBean().getProperty("ingenico.device.ScreenHeight", "1080"));
		browserData.setScreenWidth(paramBean().getProperty("ingenico.device.ScreenWidth", "1920"));
		customerDevice.setBrowserData(browserData);
		return customerDevice;
	}

	/*reserved to GlobalCollect platform*/
    @Override
    public String createSepaDirectDebitToken(CustomerAccount customerAccount, String alias,String accountHolderName,String iban) throws BusinessException {
        try {
            CompanyInformation companyInformation = new CompanyInformation();
            companyInformation.setName(customerAccount.getCode());  
            
            PersonalNameToken name = new PersonalNameToken();
            if (customerAccount.getName() != null) {
                name.setFirstName(customerAccount.getName().getFirstName());
                name.setSurname(customerAccount.getName().getLastName());
                name.setSurnamePrefix(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getCode()); 
            } 
            PersonalInformationToken personalInformation = new PersonalInformationToken();
            personalInformation.setName(name);
            
            ContactDetailsToken contactDetails=new ContactDetailsToken();
    		if(customerAccount.getContactInformation() != null ) {
    			contactDetails.setEmailAddress(customerAccount.getContactInformation().getEmail()); 
    		}
            
            CustomerTokenWithContactDetails customerTokenWithDetail = new CustomerTokenWithContactDetails();
            customerTokenWithDetail.setBillingAddress(getBillingAddress(customerAccount));
            customerTokenWithDetail.setCompanyInformation(companyInformation);
            customerTokenWithDetail.setMerchantCustomerId(customerAccount.getCode());
            customerTokenWithDetail.setPersonalInformation(personalInformation);
            customerTokenWithDetail.setContactDetails(contactDetails);
           
            TokenSepaDirectDebitWithoutCreditor  tokenSepaDDWithoutCreditor = new TokenSepaDirectDebitWithoutCreditor(); 
            MandateSepaDirectDebitWithoutCreditor  mandateSepaDDWithoutCreditor = new MandateSepaDirectDebitWithoutCreditor();
            
            BankAccountIban bankAccountIban=new BankAccountIban();
            bankAccountIban.setAccountHolderName(accountHolderName);
            bankAccountIban.setIban(iban);
            mandateSepaDDWithoutCreditor.setBankAccountIban(bankAccountIban); 
            
            Debtor debtor=new Debtor();
            debtor.setAdditionalAddressInfo(customerAccount.getAddress().getAddress3());
            debtor.setCity(customerAccount.getAddress().getCity());
            debtor.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode());
            
            if (customerAccount.getName() != null) {
            	debtor.setFirstName(customerAccount.getName().getFirstName());
            	debtor.setSurname(customerAccount.getName().getLastName());
            	debtor.setSurnamePrefix(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getCode());
            }
            debtor.setHouseNumber("");
            debtor.setState(customerAccount.getAddress().getState());
            debtor.setStreet(customerAccount.getAddress().getAddress1());
            debtor.setZip(customerAccount.getAddress().getZipCode());
            mandateSepaDDWithoutCreditor.setDebtor(debtor);
            
            tokenSepaDDWithoutCreditor.setMandate(mandateSepaDDWithoutCreditor); 
            tokenSepaDDWithoutCreditor.setCustomer(customerTokenWithDetail);  
            tokenSepaDDWithoutCreditor.setAlias(alias);
            CreateTokenRequest body = new CreateTokenRequest();
            body.setPaymentProductId(770);
            body.setSepaDirectDebit(tokenSepaDDWithoutCreditor);    
            
            ObjectMapper mapper = new ObjectMapper(); 
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            log.info("Body :"+jsonString);
            
            CreateTokenResponse response = getClient().merchant(paymentGateway.getMarchandId()).tokens().create(body);
            if (!response.getIsNewToken()) {
                throw new BusinessException("A token already exist for sepa:" + tokenSepaDDWithoutCreditor.getAlias());
            }
            return response.getToken();
        } catch (ApiException ev) {
            throw new BusinessException(ev.getResponseBody());

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }

	@Override
	public void createMandate(CustomerAccount customerAccount,String iban,String mandateReference) throws BusinessException {
    	try {
    		BankAccountIban bankAccountIban=new BankAccountIban(); 
    		bankAccountIban.setIban(iban);
 
    		MandateContactDetails contactDetails=new MandateContactDetails();
    		if(customerAccount.getContactInformation() != null ) {
    			contactDetails.setEmailAddress(customerAccount.getContactInformation().getEmail()); 
    		}
    		MandateAddress address=new MandateAddress();
    		if (customerAccount.getAddress() != null) {
    		address.setCity(customerAccount.getAddress().getCity());
    		address.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode()); 
    		address.setStreet(customerAccount.getAddress().getAddress1());
    		address.setZip(customerAccount.getAddress().getZipCode());
    		}
    		MandatePersonalName name = new MandatePersonalName();
    		MandatePersonalInformation  personalInformation =new MandatePersonalInformation();
    		
    		if (customerAccount.getName() != null) {
    			name.setFirstName(customerAccount.getName().getFirstName());
    			name.setSurname(customerAccount.getName().getLastName()); 
    			personalInformation.setTitle(customerAccount.getName().getTitle() == null ? "" : customerAccount.getName().getTitle().getDescription());
    		}  
    		personalInformation.setName(name);
    		MandateCustomer customer=new MandateCustomer();
    		customer.setBankAccountIban(bankAccountIban);
    		customer.setContactDetails(contactDetails);
    		customer.setMandateAddress(address);
    		customer.setPersonalInformation(personalInformation);

    		
    		CreateMandateRequest body = new CreateMandateRequest();
    		body.setUniqueMandateReference(mandateReference);
    		body.setCustomer(customer);
    		body.setCustomerReference(customerAccount.getExternalRef1()); 
    		body.setRecurrenceType("RECURRING");
    		body.setSignatureType("UNSIGNED");

    	    getClient().merchant(paymentGateway.getMarchandId()).mandates().create(body); 

    	} catch (ApiException ev) { 
    		throw new BusinessException(ev.getResponseBody());

    	} catch (Exception e) { 
    		throw new BusinessException(e.getMessage());
    	}

    }

	@Override
	public void approveSepaDDMandate(String token,Date signatureDate) throws BusinessException {
    	try {
    	ApproveTokenRequest body = new ApproveTokenRequest();
    	body.setMandateSignatureDate(DateUtils.formatDateWithPattern(signatureDate, "YYYYMMdd"));
    	body.setMandateSignaturePlace("");
    	body.setMandateSigned(true);
    	
    	client.merchant(paymentGateway.getMarchandId()).tokens().approvesepadirectdebit(token, body);
    	
    	}catch (Exception e) {
    		throw new BusinessException(e.getMessage());
    	}
    }
}