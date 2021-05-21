package org.meveo.service.payments.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.sips.exception.SealCalculationException;
import com.worldline.sips.exception.UnknownStatusException;
import com.worldline.sips.model.AcquirerResponseCode;
import com.worldline.sips.model.Currency;
import com.worldline.sips.model.OrderChannel;
import com.worldline.sips.model.ResponseCode;
import com.worldline.sips.util.SealCalculator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
import org.meveo.model.payments.*;
import org.meveo.model.worldline.sips.checkout.WalletOrderRequest;
import org.meveo.model.worldline.sips.checkout.WalletOrderResponse;
import org.meveo.model.worldline.sips.wallet.WalletAction;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Gateway for ATOS SIPS Wallet payments
 * <p>
 * Use <a href="https://documentation.sips.worldline.com/fr/WLSIPS.310-UG-Sips-Office-JSON.html">SIPS Office JSON API</a> to make payment/refund requests
 * <br>
 * Currently support only card payment methods
 * </p>
 *
 * @author Julien LEBOUTEILLER
 */
@PaymentGatewayClass
public class AtosWalletGatewayPayment implements GatewayPaymentInterface {
    private static final String WALLET_URL_PROPERTY = "atos.api.walletUrl";
    private static final String OFFICE_URL_PROPERTY = "atos.api.officeUrl";
    private static final String WALLET_ORDER_URI = "/checkout/walletOrder";
    private static final String WALLET_CREDIT_HOLDER_URI = "/cashManagement/walletCreditHolder";

    private static final String WALLETPAGE_INTERFACE_VERSION = "HP_2.5";
    private static final String CASHMANAGEMENT_INTERFACE_VERSION = "CR_WS_2.25";
    private static final String CHECKOUT_INTERFACE_VERSION = "IR_WS_2.24";
    private static final String SEAL_ALGORITHM = "HMAC-SHA-256";

    protected Logger log = LoggerFactory.getLogger(AtosWalletGatewayPayment.class);

    private PaymentGateway paymentGateway = null;
    private ParamBeanFactory paramBeanFactory;
    private CustomerAccountService customerAccountService;
    private RequestConfig requestConfig;

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expiryDate, String issueNumber,
                                  CreditCardTypeEnum cardType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentCardToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPaymentOrRefundToken(paymentCardToken, ctsAmount, WALLET_ORDER_URI, CHECKOUT_INTERFACE_VERSION);
    }

    @Override
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
                                            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod ddPaymentMethod, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPaymentOrRefundToken(paymentToken, ctsAmount, WALLET_CREDIT_HOLDER_URI, CASHMANAGEMENT_INTERFACE_VERSION);
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

    private PaymentResponseDto doPaymentOrRefundToken(CardPaymentMethod paymentMethod, Long ctsAmount, String wsUri, String interfaceVersion) throws BusinessException {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();

        WalletOrderRequest request = buildWalletOrderRequest(paymentMethod, ctsAmount, interfaceVersion);

        String seal;
        try {
            String data = getSealString(request);
            seal = SealCalculator.calculate(data, paymentGateway.getWebhooksSecretKey());
            request.setSeal(seal);
            request.setSealAlgorithm(SEAL_ALGORITHM);
        } catch (SealCalculationException e) {
            processError("Error occurred during seal calculation", e, paymentResponseDto);
            return paymentResponseDto;
        }

        String wsUrl = paramBean().getProperty(OFFICE_URL_PROPERTY, "changeIt");
        wsUrl += wsUri;

        ObjectMapper mapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(request);
            log.debug("WalletOrderRequest: {}", requestBody);
        } catch (JsonProcessingException e) {
            processError("Unable to parse request as JSON", e, paymentResponseDto);
            return paymentResponseDto;
        }

        // Request parameters and other properties.
        HttpPost httpPost = new HttpPost(wsUrl);
        httpPost.setConfig(getRequestConfig());
        httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        // Execute and get the response
        HttpResponse httpResponse;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            processError("Error occurred while calling WalletOrder method", e, paymentResponseDto);
            return paymentResponseDto;
        }

        WalletOrderResponse response;
        try {
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            log.debug("WallerOrderResponse: {}", responseBody);
            response = mapper.readValue(responseBody, WalletOrderResponse.class);
        } catch (IOException e) {
            processError("Unable to parse JSON response", e, paymentResponseDto);
            return paymentResponseDto;
        }

        if (response != null) {
            paymentResponseDto.setPaymentID(request.getTransactionReference());
            paymentResponseDto.setTokenId(paymentMethod.getTokenId()); // Token ID = merchant wallet ID (since created, this never change)
            paymentResponseDto.setNewToken(false);
            paymentResponseDto.setPaymentStatus(mappingStatus(response.getResponseCode()));
            paymentResponseDto.setTransactionId(response.getAuthorisationId());

            if (!paymentResponseDto.getPaymentStatus().equals(PaymentStatusEnum.ACCEPTED)) {
                paymentResponseDto.setErrorCode(response.getAcquirerResponseCode());
                try {
                    paymentResponseDto.setErrorMessage(AcquirerResponseCode.fromCode(response.getAcquirerResponseCode()).name());
                } catch (UnknownStatusException e) {
                    paymentResponseDto.setErrorMessage("UNKNOWN_STATUS");
                    log.error("Unknown acquirer response code received", e);
                }
            }

            return paymentResponseDto;
        } else {
            paymentResponseDto.setErrorMessage("Empty response");
        }

        // Default fallback to ERROR
        paymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);

        return paymentResponseDto;
    }

    @Override
    public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
        String returnUrl = hostedCheckoutInput.getReturnUrl();
        String walletUrl = paramBean().getProperty(WALLET_URL_PROPERTY, "changeIt");

        PaymentHostedCheckoutResponseDto response = new PaymentHostedCheckoutResponseDto();
        PaymentHostedCheckoutResponseDto.Result result = response.getResult();

        result.setHostedCheckoutUrl(walletUrl);
        result.setHostedCheckoutVersion(WALLETPAGE_INTERFACE_VERSION);
        result.setReturnUrl(returnUrl);

        CustomerAccount ca = customerAccountService().findById(hostedCheckoutInput.getCustomerAccountId());

        String merchantWalletId = ca.getId() + "_" + (ca.getCardPaymentMethods(false).size() + 1);

        String data = "merchantId=" +
                paymentGateway.getMarchandId() +
                "|normalReturnUrl=" + returnUrl +
                "|merchantSessionId=" + hostedCheckoutInput.getCustomerAccountId() +
                "|merchantWalletId=" + merchantWalletId +
                "|keyVersion=" + paymentGateway.getWebhooksKeyId() +
                "|requestDateTime=" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());

        // Wallet action
        if (!StringUtils.isBlank(hostedCheckoutInput.getAllowedActions())) {
            String walletActionnameList = buildWalletActionnameList(hostedCheckoutInput.getAllowedActions());
            if (!StringUtils.isBlank(walletActionnameList)) {
                data += "|walletActionnameList=" + walletActionnameList;
            }
        }

        if (!StringUtils.isBlank(hostedCheckoutInput.getVariant())) {
            data += "|templateName=" + hostedCheckoutInput.getVariant();
        }

        if (!StringUtils.isBlank(hostedCheckoutInput.getAutomaticReturnUrl())) {
            data += "|automaticResponseURL=" + hostedCheckoutInput.getAutomaticReturnUrl();
        }

        if (!StringUtils.isBlank(hostedCheckoutInput.getReturnContext())) {
            data += "|returnContext=" + hostedCheckoutInput.getReturnContext();
        }

        if (!StringUtils.isBlank(hostedCheckoutInput.getAdvancedOptions())) {
            data += "|" + hostedCheckoutInput.getAdvancedOptions();
        }

        result.setData(data);

        try {
            result.setSeal(SealCalculator.calculate(data, paymentGateway.getWebhooksSecretKey()).toLowerCase());
        } catch (SealCalculationException e) {
            throw new BusinessException("Seal couldn't be calculated for merchantId=" + paymentGateway.getMarchandId(), e);
        }

        return response;
    }

    @Override
    public String createInvoice(Invoice invoice) throws BusinessException {
        return null;
    }

    @Override
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public Object getClientObject() {
        return null;
    }

    private WalletOrderRequest buildWalletOrderRequest(PaymentMethod paymentMethod, Long amount, String interfaceVersion) {
        WalletOrderRequest request = new WalletOrderRequest();
        request.setAmount(String.valueOf(amount));
        request.setCurrencyCode(Currency.valueOf(paymentMethod.getCustomerAccount().getTradingCurrency().getCurrencyCode()).getCode());
        request.setMerchantId(paymentGateway.getMarchandId());
        request.setOrderChannel(OrderChannel.INTERNET.name());
        request.setInterfaceVersion(interfaceVersion);
        request.setKeyVersion(paymentGateway.getWebhooksKeyId());
        request.setTransactionReference(System.currentTimeMillis() + "_-_" + paymentMethod.getCustomerAccount().getId());

        // Needed for backward compatibility purpose, in 5.X version, the merchant wallet ID is the customer account ID
        // Starting at 9.X version, the merchant wallet ID match the token ID of payment method
        if (StringUtils.isBlank(paymentMethod.getInfo1())) {
            request.setMerchantWalletId(String.valueOf(paymentMethod.getCustomerAccount().getId()));
            request.setPaymentMeanId(paymentMethod.getTokenId());
        } else {
            request.setMerchantWalletId(paymentMethod.getTokenId());
            request.setPaymentMeanId(paymentMethod.getInfo1());
        }

        return request;
    }

    private String buildWalletActionnameList(String variant) {
        String[] variantList = variant.split(",");
        List<String> walletActionnameList = new ArrayList<>();

        for (String item : variantList) {
            if (WalletAction.get(item) != null) {
                walletActionnameList.add(variant);
            }
        }

        return String.join(",", walletActionnameList);
    }

    /**
     * Mapping status.
     *
     * @param responseCode the SIPS status
     * @return the payment status enum
     */
    private PaymentStatusEnum mappingStatus(String responseCode) {
        PaymentStatusEnum status;

        // Code 25 : Atos service temporary unavailable
        if ("25".equalsIgnoreCase(responseCode)) {
            return PaymentStatusEnum.REJECTED;
        }

        try {
            ResponseCode code = ResponseCode.fromCode(responseCode);

            switch (code) {
                case ACCEPTED:
                    status = PaymentStatusEnum.ACCEPTED;
                    break;
                case CARD_CEILING_EXCEEDED:
                case AUTHORIZATION_REFUSED:
                case FRAUD_SUSPECTED:
                case PAYMENT_MEAN_EXPIRED:
                case PAN_BLOCKED:
                case MAX_ATTEMPTS_REACHED:
                    status = PaymentStatusEnum.REJECTED;
                    break;
                case CUSTOMER_CANCELLATION:
                case DUPLICATED_TRANSACTION:
                case TIMEFRAME_EXCEEDED:
                    status = PaymentStatusEnum.NOT_PROCESSED;
                    break;
                default:
                case INVALID_MERCHANT_CONTRACT:
                case INVALID_TRANSACTION:
                case INVALID_DATA:
                case INCORRECT_FORMAT:
                case SERVICE_UNAVAILABLE:
                case INTERNAL_ERROR:
                    status = PaymentStatusEnum.ERROR;
                    break;
            }
        } catch (UnknownStatusException e) {
            status = PaymentStatusEnum.ERROR;
        }

        return status;
    }

    private String getSealString(WalletOrderRequest request) {
        return request.getAmount()
                + request.getCurrencyCode()
                + request.getInterfaceVersion()
                + request.getMerchantId()
                + request.getMerchantWalletId()
                + request.getOrderChannel()
                + request.getPaymentMeanId()
                + request.getTransactionReference();
    }

    private void processError(String message, Exception e, PaymentResponseDto paymentResponseDto) {
        log.error(message, e);
        paymentResponseDto.setErrorMessage(message);
        paymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
    }

    private RequestConfig getRequestConfig() {
        if (requestConfig == null) {
            String proxyHost = System.getProperty("https.proxyHost");
            String proxyPort = System.getProperty("https.proxyPort");

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

            if (!StringUtils.isBlank(proxyHost)) {
                requestConfigBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort))).build();
            }

            requestConfig = requestConfigBuilder.build();
        }

        return requestConfig;
    }

    private ParamBean paramBean() {
        if (paramBeanFactory == null) {
            paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        }

        return paramBeanFactory != null ? paramBeanFactory.getInstance() : null;
    }

    private CustomerAccountService customerAccountService() {
        if (customerAccountService == null) {
            customerAccountService = (CustomerAccountService) EjbUtils.getServiceInterface(CustomerAccountService.class.getSimpleName());
        }

        return customerAccountService;
    }
}