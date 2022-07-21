/**
 * 
 */
package org.meveo.service.script.ingenico;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.script.payment.PaymentScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onlinepayments.ApiException;
import com.onlinepayments.Client;
import com.onlinepayments.CommunicatorConfiguration;
import com.onlinepayments.Factory;
import com.onlinepayments.Marshaller;
import com.onlinepayments.defaultimpl.DefaultMarshaller;
import com.onlinepayments.domain.APIError;
import com.onlinepayments.domain.Address;
import com.onlinepayments.domain.AmountOfMoney;
import com.onlinepayments.domain.BrowserData;
import com.onlinepayments.domain.Card;
import com.onlinepayments.domain.CardPaymentMethodSpecificInput;
import com.onlinepayments.domain.CardPaymentMethodSpecificInputBase;
import com.onlinepayments.domain.CardRecurrenceDetails;
import com.onlinepayments.domain.CreateHostedCheckoutRequest;
import com.onlinepayments.domain.CreateHostedCheckoutResponse;
import com.onlinepayments.domain.CreatePaymentRequest;
import com.onlinepayments.domain.CreatePaymentResponse;
import com.onlinepayments.domain.Customer;
import com.onlinepayments.domain.CustomerDevice;
import com.onlinepayments.domain.HostedCheckoutSpecificInput;
import com.onlinepayments.domain.Order;
import com.onlinepayments.domain.OrderReferences;
import com.onlinepayments.domain.PaymentProductFilter;
import com.onlinepayments.domain.PaymentProductFiltersHostedCheckout;
import com.onlinepayments.domain.PaymentResponse;
import com.onlinepayments.domain.PaymentStatusOutput;
import com.onlinepayments.domain.RedirectionData;
import com.onlinepayments.domain.SepaDirectDebitPaymentMethodSpecificInput;
import com.onlinepayments.domain.SepaDirectDebitPaymentProduct771SpecificInput;
import com.onlinepayments.domain.ThreeDSecure;
import com.onlinepayments.domain.ThreeDSecureBase;
import com.onlinepayments.domain.ThreeDSecureData;

/**
 * @author anasseh
 *
 */
public class IngenicoDirectImplScript extends PaymentScript {

    /** The log. */
    protected Logger log = LoggerFactory.getLogger(IngenicoDirectImplScript.class);

    /** The payment gateway. */
    private PaymentGateway paymentGateway = null;

    /** The client. */
    private Client client = null;

    private Marshaller marshaller = null;

    @Override
    public void getHostedCheckoutUrl(Map<String, Object> methodContext) throws BusinessException {
        try {
            HostedCheckoutInput hostedCheckoutInput = (HostedCheckoutInput) methodContext.get(PaymentScript.CONTEXT_HOSTED_CO);
            paymentGateway = (PaymentGateway) methodContext.get(PaymentScript.CONTEXT_PG);

            String returnUrl = hostedCheckoutInput.getReturnUrl();
            Long id = hostedCheckoutInput.getCustomerAccountId();
            String timeMillisWithcustomerAccountId = System.currentTimeMillis() + "_-_" + id;

            log.info("hostedCheckoutInput.isOneShotPayment(): " + hostedCheckoutInput.isOneShotPayment());

            if (hostedCheckoutInput.isOneShotPayment()) {
                timeMillisWithcustomerAccountId = "oneShot_" + timeMillisWithcustomerAccountId;
            }

            String redirectionUrl;

            HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
            hostedCheckoutSpecificInput.setLocale(hostedCheckoutInput.getLocale());
            hostedCheckoutSpecificInput.setVariant(hostedCheckoutInput.getVariant());
            hostedCheckoutSpecificInput.setReturnUrl(returnUrl);
            hostedCheckoutSpecificInput.setIsRecurring(false);

            PaymentProductFiltersHostedCheckout dd = new PaymentProductFiltersHostedCheckout();
            PaymentProductFilter cc = new PaymentProductFilter();
            cc.setProducts(getListProductIds());
            dd.setRestrictTo(cc);
            hostedCheckoutSpecificInput.setPaymentProductFilters(dd);

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
            // cardPaymentMethodSpecificInputBase.setRequiresApproval(true);
            cardPaymentMethodSpecificInputBase.setAuthorizationMode(hostedCheckoutInput.getAuthorizationMode());
            cardPaymentMethodSpecificInputBase.setTokenize(true);

            AmountOfMoney amountOfMoney3DS = new AmountOfMoney();
            amountOfMoney3DS.setAmount(Long.valueOf(hostedCheckoutInput.getAuthenticationAmount()));
            amountOfMoney3DS.setCurrencyCode(hostedCheckoutInput.getCurrencyCode());

            ThreeDSecureBase threeDSecure = new ThreeDSecureBase();
            // threeDSecure.setAuthenticationAmount(amountOfMoney3DS);
            threeDSecure.setSkipAuthentication(hostedCheckoutInput.isSkipAuthentication());
            cardPaymentMethodSpecificInputBase.setThreeDSecure(threeDSecure);

            CardRecurrenceDetails cardRecurrenceDetails = new CardRecurrenceDetails();
            cardRecurrenceDetails.setRecurringPaymentSequenceIndicator(paramBean().getProperty("ingenico.HostedCheckout.RecurringPaymentSequenceIndicator", ""));
            cardPaymentMethodSpecificInputBase.setRecurring(cardRecurrenceDetails);

            cardPaymentMethodSpecificInputBase
                .setUnscheduledCardOnFileRequestor(paramBean().getProperty("ingenico.HostedCheckout.UnscheduledCardOnFileRequestor", "cardholderInitiated"));
            cardPaymentMethodSpecificInputBase
                .setUnscheduledCardOnFileSequenceIndicator(paramBean().getProperty("ingenico.HostedCheckout.UnscheduledCardOnFileSequenceIndicator", "first"));

            CreateHostedCheckoutRequest body = new CreateHostedCheckoutRequest();
            body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
            body.setCardPaymentMethodSpecificInput(cardPaymentMethodSpecificInputBase);
            body.setOrder(order);
            getClient();
            log.info("REQUEST:" + marshaller.marshal(body));
            CreateHostedCheckoutResponse response = client.merchant(paymentGateway.getMarchandId()).hostedCheckout().createHostedCheckout(body);
            log.info("RESPONSE:" + marshaller.marshal(response));
            redirectionUrl = paramBean().getProperty("ingenico.hostedCheckoutUrl.prefix", "https://payment.") + response.getPartialRedirectUrl();

            methodContext.put(PaymentScript.RESULT_HOSTED_CO_URL, redirectionUrl);

        } catch (Exception e) {
            log.error("Error on getHostedCheckoutUrl:", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void doPaymentToken(Map<String, Object> methodContext) throws BusinessException {
        Map<String, Object> additionalParams = (Map<String, Object>) methodContext.get(PaymentScript.CONTEXT_ADDITIONAL_INFOS);
        CardPaymentMethod paymentToken = (CardPaymentMethod) methodContext.get(PaymentScript.CONTEXT_TOKEN);
        Long ctsAmount = (Long) methodContext.get(PaymentScript.CONTEXT_AMOUNT_CTS);
        paymentGateway = (PaymentGateway) methodContext.get(PaymentScript.CONTEXT_PG);
        PaymentResponseDto doPaymentResponseDto = doPayment(null, paymentToken, ctsAmount, paymentToken.getCustomerAccount(), null, null, null, null, null, null, additionalParams);
        methodContext.put(PaymentScript.RESULT_PAYMENT_ID, doPaymentResponseDto.getPaymentID());
        methodContext.put(PaymentScript.RESULT_TRANSACTION_ID, doPaymentResponseDto.getTransactionId());
        methodContext.put(PaymentScript.RESULT_PAYMENT_STATUS, doPaymentResponseDto.getPaymentStatus());
        methodContext.put(PaymentScript.RESULT_ERROR_MSG, doPaymentResponseDto.getErrorMessage());
        methodContext.put(PaymentScript.RESULT_CODE_CLIENT_SIDE, doPaymentResponseDto.getCodeClientSide());
        methodContext.put(PaymentScript.RESULT_BANK_REFERENCE, doPaymentResponseDto.getBankRefenrence());
        methodContext.put(PaymentScript.RESULT_PAYMENT_BRAND, doPaymentResponseDto.getPaymentBrand());

    }

    private PaymentResponseDto doPayment(DDPaymentMethod ddPaymentMethod, CardPaymentMethod paymentCardToken, Long ctsAmount, CustomerAccount customerAccount, String cardNumber,
            String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED);
        try {

            CreatePaymentRequest body = buildPaymentRequest(ddPaymentMethod, paymentCardToken, ctsAmount, customerAccount, cardNumber, ownerName, cvv, expirayDate, cardType);
            getClient();
            log.info("doPayment REQUEST :" + marshaller.marshal(body));

            CreatePaymentResponse response = getClient().merchant(paymentGateway.getMarchandId()).payments().createPayment(body);

            if (response != null) {
                log.info("doPayment RESPONSE :" + marshaller.marshal(response));

                doPaymentResponseDto.setPaymentID(response.getPayment().getId());
                doPaymentResponseDto.setPaymentStatus(mappingStaus(response.getPayment().getStatus()));
                if (response.getCreationOutput() != null) {
                    doPaymentResponseDto.setTransactionId(response.getCreationOutput().getExternalReference());
                    doPaymentResponseDto.setTokenId(response.getCreationOutput().getToken());
                    doPaymentResponseDto.setNewToken(response.getCreationOutput().getIsNewToken());
                }
                PaymentResponse payment = response.getPayment();
                if (payment != null && response.getPayment().getStatusOutput().getErrors() != null) {
                    PaymentStatusOutput statusOutput = payment.getStatusOutput();
                    if (statusOutput != null) {
                        List<APIError> errors = statusOutput.getErrors();
                        if (CollectionUtils.isNotEmpty(errors)) {
                            doPaymentResponseDto.setErrorMessage(errors.toString());
                            doPaymentResponseDto.setErrorCode(errors.get(0).getId());
                        }
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

        AmountOfMoney amountOfMoney = new AmountOfMoney();
        amountOfMoney.setAmount(ctsAmount);
        amountOfMoney.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());

        Customer customer = new Customer();
        customer.setBillingAddress(getBillingAddress(customerAccount));
        if ("true".equals(paramBean().getProperty("ingenico.CreatePayment.includeDeviceData", "true"))) {
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

        return body;
    }

    private void connect() {
        ParamBean paramBean = paramBean();
        // Init properties

        paramBean.getProperty("onlinePayments.api.endpoint.host", "payment.preprod.direct.worldline-solutions.com");
        paramBean.getProperty("onlinePayments.api.endpoint.scheme", "https");
        paramBean.getProperty("onlinePayments.api.endpoint.port", "443");

        CommunicatorConfiguration communicatorConfiguration = new CommunicatorConfiguration(ParamBean.getInstance().getProperties());
        communicatorConfiguration.setApiKeyId(paymentGateway.getApiKey());
        communicatorConfiguration.setSecretApiKey(paymentGateway.getSecretKey());
        client = (Client) Factory.createClient(communicatorConfiguration);
        marshaller = DefaultMarshaller.INSTANCE;
    }

    private ParamBean paramBean() {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        ParamBean paramBean = paramBeanFactory.getInstance();
        return paramBean;
    }

    private Client getClient() {
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

    public Object getClientObject() {
        if (client == null) {
            connect();
        }
        return client;
    }

    private CustomerDevice getDeviceData() {
        CustomerDevice customerDevice = new CustomerDevice();
        customerDevice.setAcceptHeader(paramBean().getProperty("ingenico.device.AcceptHeader",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"));
        customerDevice.setLocale(paramBean().getProperty("ingenico.device.Locale", "fr_FR"));
        customerDevice.setTimezoneOffsetUtcMinutes(paramBean().getProperty("ingenico.device.TimezoneOffsetUtcMinutes", "60"));
        customerDevice.setUserAgent(paramBean().getProperty("ingenico.device.UserAgent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"));
        BrowserData browserData = new BrowserData();
        browserData.setColorDepth(paramBean().getPropertyAsInteger("ingenico.device.ColorDepth", 24));
        browserData.setJavaEnabled("true".equals(paramBean().getProperty("ingenico.device.JavaEnabled", "false")));
        browserData.setScreenHeight(paramBean().getProperty("ingenico.device.ScreenHeight", "1080"));
        browserData.setScreenWidth(paramBean().getProperty("ingenico.device.ScreenWidth", "1920"));
        customerDevice.setBrowserData(browserData);
        return customerDevice;
    }

    private List<Integer> getListProductIds() {
        List<Integer> listProduct = new ArrayList<Integer>();

        String productFilter = paramBean().getProperty("ingenico.HostedCheckout.ProductFilter", "1,2,3,122,114,119,130");
        for (String s : productFilter.split(",")) {
            listProduct.add(Integer.valueOf(s));
        }
        return listProduct;
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

    private CardPaymentMethodSpecificInput getCardTokenInput(CardPaymentMethod cardPaymentMethod) {
        if ("true".equals(paramBean().getProperty("ingenico.CreatePayment.use3DSecure", "true"))) {
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
        RedirectionData redirectionData = new RedirectionData();
        redirectionData.setReturnUrl(paramBean.getProperty("ingenico.urlReturnPayment", "changeIt"));
        threeDSecure.setRedirectionData(redirectionData);
        threeDSecure.setChallengeIndicator(paramBean.getProperty("ingenico.3ds.ChallengeIndicator", "no-preference"));
        // threeDSecure.setAuthenticationFlow(paramBean.getProperty("ingenico.3ds.AuthenticationFlow", "browser"));
        threeDSecure.setChallengeCanvasSize(paramBean.getProperty("ingenico.3ds.ChallengeCanvasSize", "600x400"));
        cardPaymentMethodSpecificInput.setThreeDSecure(threeDSecure);
        cardPaymentMethodSpecificInput.setIsRecurring(Boolean.valueOf("true".equals(paramBean().getProperty("ingenico.CreatePayment.IsRecurring", "false"))));

        cardPaymentMethodSpecificInput.setAuthorizationMode(getAuthorizationMode());

        cardPaymentMethodSpecificInput.setUnscheduledCardOnFileRequestor(paramBean().getProperty("ingenico.CreatePayment.UnscheduledCardOnFileRequestor", "merchantInitiated"));
        cardPaymentMethodSpecificInput
            .setUnscheduledCardOnFileSequenceIndicator(paramBean().getProperty("ingenico.CreatePayment.UnscheduledCardOnFileSequenceIndicator", "subsequent"));

        CardRecurrenceDetails cardRecurrenceDetails = new CardRecurrenceDetails();
        cardRecurrenceDetails.setRecurringPaymentSequenceIndicator(paramBean().getProperty("ingenico.CreatePayment.RecurringPaymentSequenceIndicator", "null"));
        cardPaymentMethodSpecificInput.setRecurring(cardRecurrenceDetails);

        return cardPaymentMethodSpecificInput;
    }

    private CardPaymentMethodSpecificInput getCardTokenInputDefault(CardPaymentMethod cardPaymentMethod) {
        ParamBean paramBean = paramBean();
        CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
        cardPaymentMethodSpecificInput.setToken(cardPaymentMethod.getTokenId());
        cardPaymentMethodSpecificInput.setReturnUrl(paramBean.getProperty("ingenico.urlReturnPayment", "changeIt"));
        cardPaymentMethodSpecificInput.setIsRecurring(Boolean.TRUE);
        // cardPaymentMethodSpecificInput.setRecurringPaymentSequenceIndicator("recurring");
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
        SepaDirectDebitPaymentProduct771SpecificInput sepaDirectDebitPaymentProduct771SpecificInput = new SepaDirectDebitPaymentProduct771SpecificInput();
        // sepaDirectDebitPaymentProduct771SpecificInput.setMandateReference(ddPaymentMethod.getMandateIdentification());
        sepaPmInput.setPaymentProduct771SpecificInput(sepaDirectDebitPaymentProduct771SpecificInput);
        return sepaPmInput;
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

}
