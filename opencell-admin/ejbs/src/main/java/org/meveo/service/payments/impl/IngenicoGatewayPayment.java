package org.meveo.service.payments.impl;

import java.util.Map;


import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.*;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ingenico.connect.gateway.sdk.java.ApiException;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.DeclinedPaymentException;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Card;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.CardWithoutCvv;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.CompanyInformation;
import com.ingenico.connect.gateway.sdk.java.domain.errors.definitions.APIError;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentRequest;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.PaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.token.CreateTokenRequest;
import com.ingenico.connect.gateway.sdk.java.domain.token.CreateTokenResponse;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.CustomerToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.PersonalInformationToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.PersonalNameToken;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.TokenCard;
import com.ingenico.connect.gateway.sdk.java.domain.token.definitions.TokenCardData;

/**
 * 
 * @author anasseh
 * 
 * @lastModifiedVersion 5.2
 */
@PaymentGatewayClass
public class IngenicoGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(IngenicoGatewayPayment.class);

    private static Client client = null;
    
    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    private ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface("ParamBeanFactory");

    private static void connect() {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        ParamBean paramBean = paramBeanFactory.getInstance();
        //Init properties
        paramBean.getProperty("connect.api.authorizationType", "changeIt");
        paramBean.getProperty("connect.api.connectTimeout", "5000");
        paramBean.getProperty("connect.api.endpoint.host", "changeIt");
        paramBean.getProperty("connect.api.endpoint.scheme", "changeIt");
        paramBean.getProperty("connect.api.integrator", "");
        paramBean.getProperty("connect.api.socketTimeout", "300000");        
        CommunicatorConfiguration communicatorConfiguration = new CommunicatorConfiguration(ParamBean.getInstance().getProperties());
        communicatorConfiguration.setApiKeyId(paramBean.getProperty("ingenico.ApiKeyId", "changeIt"));
        communicatorConfiguration.setSecretApiKey(paramBean.getProperty("ingenico.SecretApiKey", "changeIt"));
        client = Factory.createClient(communicatorConfiguration);
    }

    public static Client getClient() {
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
            String merchantId = paramBeanFactory.getInstance().getProperty("ingenico.merchantId", "changeIt");
            CreateTokenResponse response = getClient().merchant(merchantId).tokens().create(body);
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

    private PaymentResponseDto doPayment(DDPaymentMethod ddPaymentMethod, CardPaymentMethod paymentCardToken, Long ctsAmount, CustomerAccount customerAccount, String cardNumber,
            String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        try {
            AmountOfMoney amountOfMoney = new AmountOfMoney();
            amountOfMoney.setAmount(ctsAmount);
            amountOfMoney.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());

            Customer customer = new Customer();
            customer.setBillingAddress(getBillingAddress(customerAccount));

            Order order = new Order();
            order.setAmountOfMoney(amountOfMoney);
            order.setCustomer(customer);

            CreatePaymentRequest body = new CreatePaymentRequest();
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

            String merchantId = paramBeanFactory.getInstance().getProperty("ingenico.merchantId", "changeIt");
            CreatePaymentResponse response = getClient().merchant(merchantId).payments().create(body);
            if (response != null) {
                PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
                doPaymentResponseDto.setPaymentID(response.getPayment().getId());
                doPaymentResponseDto.setPaymentStatus(mappingStaus(response.getPayment().getStatus()));
                if (response.getCreationOutput() != null) {
                    doPaymentResponseDto.setTransactionId(response.getCreationOutput().getExternalReference());
                    doPaymentResponseDto.setTokenId(response.getCreationOutput().getToken());
                    doPaymentResponseDto.setNewToken(response.getCreationOutput().getIsNewToken());
                }
                if (response.getPayment() != null && response.getPayment().getStatusOutput() != null && response.getPayment().getStatusOutput().getErrors() != null) {
                    doPaymentResponseDto.setErrorMessage(response.getPayment().getStatusOutput().getErrors().toString());
                }

                return doPaymentResponseDto;
            } else {
                throw new BusinessException("Gateway response is null");
            }
        } catch (DeclinedPaymentException e) {
            throw new BusinessException(e.getResponseBody());
        } catch (ApiException e) {
            throw new BusinessException(e.getResponseBody());
        }
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        String merchantId = paramBeanFactory.getInstance().getProperty("ingenico.merchantId", "changeIt");
        try {
            getClient().merchant(merchantId).payments().cancel(paymentID);
        } catch (ApiException e) {
            throw new BusinessException(e.getResponseBody());
        }
    }

    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        String merchantId = paramBeanFactory.getInstance().getProperty("ingenico.merchantId", "changeIt");
        try {
            PaymentResponse paymentResponse = getClient().merchant(merchantId).payments().get(paymentID);
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

    private Address getBillingAddress(CustomerAccount customerAccount) {
        Address billingAddress = new Address();
        if (customerAccount.getAddress() != null) {
            billingAddress.setAdditionalInfo(customerAccount.getAddress().getAddress3());
            billingAddress.setCity(customerAccount.getAddress().getCity());
            billingAddress.setCountryCode(customerAccount.getAddress().getCountry() == null ? null : customerAccount.getAddress().getCountry().getCountryCode());
            billingAddress.setHouseNumber(customerAccount.getAddress().getAddress1());
            billingAddress.setState(customerAccount.getAddress().getState());
            billingAddress.setStreet(customerAccount.getAddress().getAddress2());
            billingAddress.setZip(customerAccount.getAddress().getZipCode());
        }
        return billingAddress;
    }

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
        return PaymentStatusEnum.REJECTED;
    }

    private CardPaymentMethodSpecificInput getCardInput(String cardNumber, String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType) {
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setCardholderName(ownerName);
        card.setCvv(cvv);
        card.setExpiryDate(expirayDate);
        cardPaymentMethodSpecificInput.setCard(card);
        cardPaymentMethodSpecificInput.setPaymentProductId(cardType.getId());
        return cardPaymentMethodSpecificInput;
    }

    private CardPaymentMethodSpecificInput getCardTokenInput(CardPaymentMethod cardPaymentMethod) {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        ParamBean paramBean = paramBeanFactory.getInstance();
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        cardPaymentMethodSpecificInput.setToken(cardPaymentMethod.getTokenId());
        cardPaymentMethodSpecificInput.setReturnUrl(paramBean.getProperty("ingenico.urlReturnPayment", "changeIt"));
        cardPaymentMethodSpecificInput.setIsRecurring(Boolean.TRUE);
        cardPaymentMethodSpecificInput.setRecurringPaymentSequenceIndicator("recurring");

        return cardPaymentMethodSpecificInput;
    }

    private SepaDirectDebitPaymentMethodSpecificInput getSepaInput(DDPaymentMethod ddPaymentMethod) {
        SepaDirectDebitPaymentMethodSpecificInput sepaPmInput = new SepaDirectDebitPaymentMethodSpecificInput();
        sepaPmInput.setPaymentProductId(771);
        SepaDirectDebitPaymentProduct771SpecificInput sepaDirectDebitPaymentProduct771SpecificInput = new SepaDirectDebitPaymentProduct771SpecificInput();
        sepaDirectDebitPaymentProduct771SpecificInput.setMandateReference(ddPaymentMethod.getMandateIdentification());
        sepaPmInput.setPaymentProduct771SpecificInput(sepaDirectDebitPaymentProduct771SpecificInput);
        return sepaPmInput;
    }

    @Override
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
        try
        {
            String returnUrl = hostedCheckoutInput.getReturnUrl();
            Long id = hostedCheckoutInput.getCustomerAccountId();
            String TimeMillisWithcustomerAccountId =  System.currentTimeMillis() + "_-_" + id;

            String redirectionUrl;

            String merchantId = paramBeanFactory.getInstance().getProperty("ingenico.merchantId", "changeIt");

            HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
            hostedCheckoutSpecificInput.setLocale(hostedCheckoutInput.getLocale());
            hostedCheckoutSpecificInput.setVariant(hostedCheckoutInput.getVariant());
            hostedCheckoutSpecificInput.setReturnUrl(returnUrl);

            AmountOfMoney amountOfMoney = new AmountOfMoney();
            amountOfMoney.setAmount(Long.valueOf(hostedCheckoutInput.getAmount()));
            amountOfMoney.setCurrencyCode(hostedCheckoutInput.getCurrencyCode());

            Address billingAddress = new Address();
            billingAddress.setCountryCode(hostedCheckoutInput.getCountryCode());

            Customer customer = new Customer();
            customer.setBillingAddress(billingAddress);

            OrderReferences orderReferences = new OrderReferences();
            orderReferences.setMerchantReference(TimeMillisWithcustomerAccountId);

            Order order = new Order();
            order.setAmountOfMoney(amountOfMoney);
            order.setCustomer(customer);
            order.setReferences(orderReferences);

            CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
            cardPaymentMethodSpecificInput.setAuthorizationMode(hostedCheckoutInput.getAuthorizationMode());
            cardPaymentMethodSpecificInput.setTokenize(true);
            cardPaymentMethodSpecificInput.setSkipAuthentication(hostedCheckoutInput.isSkipAuthentication());
            cardPaymentMethodSpecificInput.setIsRecurring(true);
            cardPaymentMethodSpecificInput.setReturnUrl(hostedCheckoutInput.getReturnUrl());

            CreateHostedCheckoutRequest body = new CreateHostedCheckoutRequest();
            body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
            body.setCardPaymentMethodSpecificInput(cardPaymentMethodSpecificInput);
            body.setOrder(order);

            CreateHostedCheckoutResponse response = getClient().merchant(merchantId).hostedcheckouts().create(body);

            redirectionUrl = "https://payment." +  response.getPartialRedirectUrl();
            return redirectionUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

}