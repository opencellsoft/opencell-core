package org.meveo.service.payments.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.worldline.sips.checkout.WalletOrderRequest;
import org.meveo.model.worldline.sips.checkout.WalletOrderResponse;
import org.meveo.model.worldline.sips.wallet.WalletAction;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldline.sips.exception.SealCalculationException;
import com.worldline.sips.exception.UnknownStatusException;
import com.worldline.sips.model.CaptureMode;
import com.worldline.sips.model.Currency;
import com.worldline.sips.model.OrderChannel;
import com.worldline.sips.model.PaymentPattern;
import com.worldline.sips.model.ResponseCode;
import com.worldline.sips.util.SealCalculator;

/**
 * Gateway for ATOS SIPS Wallet payments
 * <p>
 * Use <a href="https://documentation.sips.worldline.com/fr/WLSIPS.310-UG-Sips-Office-JSON.html">SIPS Office JSON API</a> to make payment/refund requests
 * <br>
 * Currently support only card payment methods with payPage
 * </p>
 *
 * @author Julien LEBOUTEILLER
 */
@PaymentGatewayClass
public class AtosWalletGatewayPayment implements GatewayPaymentInterface {
    private static final String WALLET_URL_PROPERTY = "atos.api.walletUrl";
    private static final String WALLET_HOSTED_CHECK_OUT_URL_PROPERTY = "atos.api.walletUrl.hostedCheckOut";
    private static final String OFFICE_URL_PROPERTY = "atos.api.officeUrl";
    private static final String WALLET_ORDER_URI_PROPERTY = "atos.api.wallet.order.uri";
    private static final String WALLET_CREDIT_HOLDER_URI_PROPERTY = "atos.api.wallet.credit.uri";

    private static final String PAYPAGE_INTERFACE_VERSION_PROPERTY = "atos.paypage.version";
    private static final String CASHMANAGEMENT_INTERFACE_VERSION_PROPERTY = "atos.api.chashmanag.version";
    private static final String CHECKOUT_INTERFACE_VERSION_PROPERTY = "atos.api.checkout.version";
    private static final String SEAL_ALGORITHM_PROPERTY = "atos.api.seal.algorithm";
    
    private static final String CF_PRV_ACQUIRER_CODE_WLSIPS = "CF_PRV_ACQUIRER_CODE_WLSIPS";
    private static final String CF_PRV_COMPL_CODE_WLSIPS = "CF_PRV_COMPL_CODE_WLSIPS";
    private static final String CF_PRV_RESPONSE_CODE_WLSIPS = "CF_PRV_RESPONSE_CODE_WLSIPS";

    protected Logger log = LoggerFactory.getLogger(AtosWalletGatewayPayment.class);

    private PaymentGateway paymentGateway = null;
    private ParamBeanFactory paramBeanFactory;
    private CustomerAccountService customerAccountService;
    private RequestConfig requestConfig;   
    private PaymentMethodService paymentMethodService;
    private ScriptInstanceService scriptInstanceService = null;

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expiryDate, String issueNumber,
                                  CreditCardTypeEnum cardType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentCardToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPaymentOrRefundToken(paymentCardToken, ctsAmount, paramBean().getProperty(WALLET_ORDER_URI_PROPERTY,"/checkout/walletOrder"), paramBean().getProperty(CHECKOUT_INTERFACE_VERSION_PROPERTY,"IR_WS_2.42"),true);
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
    	
        return doPaymentOrRefundToken(paymentToken, ctsAmount, paramBean().getProperty(WALLET_CREDIT_HOLDER_URI_PROPERTY,"/cashManagement/walletCreditHolder"), paramBean().getProperty(CASHMANAGEMENT_INTERFACE_VERSION_PROPERTY,"CR_WS_2.25"),false);
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

	private PaymentResponseDto doPaymentOrRefundToken(CardPaymentMethod paymentMethod, Long ctsAmount, String wsUri, String interfaceVersion , boolean isPayment) throws BusinessException {
		PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
		String scriptInstanceCode = paramBean().getProperty("sips.paymentRequest.script", null);
		WalletOrderRequest request = null;
		if (StringUtils.isBlank(scriptInstanceCode)) {

			request = buildWalletOrderRequest(paymentMethod, ctsAmount, interfaceVersion,isPayment);

			String seal;
			try {
				String data = getSealString(request,isPayment);
				log.info("getSealString(request):{}" , data);
				seal = SealCalculator.calculate(data, paymentGateway.getWebhooksSecretKey());
				request.setSeal(seal);
				request.setSealAlgorithm(paramBean().getProperty(SEAL_ALGORITHM_PROPERTY, "HMAC-SHA-256"));
			} catch (SealCalculationException e) {
				processError("Error occurred during seal calculation", e, paymentResponseDto);
				return paymentResponseDto;
			}
		} else {

			Map<String, Object> context = new HashMap();

			context.put("CardPaymentMethod", paymentMethod);
			context.put("ctsAmount", ctsAmount);
			context.put("interfaceVersion", interfaceVersion);
			context.put("PaymentGateway", paymentGateway);

			context = getScriptInstanceService().executeCached(scriptInstanceCode, context);
			request = (WalletOrderRequest) context.get("WalletOrderRequest");

		}
		String wsUrl = paramBean().getProperty(OFFICE_URL_PROPERTY, null);
		if(!isPayment) {
			wsUrl = paramBean().getProperty(WALLET_URL_PROPERTY, null);
		}
		wsUrl += wsUri;
		
		log.info("wsUrl: {}", wsUrl);

		ObjectMapper mapper = new ObjectMapper();
		String requestBody;
		try {
			mapper.setSerializationInclusion(Include.NON_NULL);
			requestBody = mapper.writeValueAsString(request);
			log.info("WalletOrderRequest: {}", requestBody);
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
		WalletOrderResponse response;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpResponse httpResponse = httpClient.execute(httpPost);

			String responseBody = EntityUtils.toString(httpResponse.getEntity());
			log.debug("WallerOrderResponse: {}", responseBody);
			response = mapper.readValue(responseBody, WalletOrderResponse.class);
		} catch (IOException e) {
			processError("Error occurred while calling WalletOrder method", e, paymentResponseDto);
			return paymentResponseDto;
		}

		if (response != null) {
			paymentResponseDto.setPaymentID(request.getTransactionReference());
			paymentResponseDto.setTokenId(paymentMethod.getTokenId()); // Token ID = merchant wallet ID (since created, this never change)
			paymentResponseDto.setNewToken(false);
			paymentResponseDto.setPaymentStatus(mappingStatus(response.getResponseCode()));
			paymentResponseDto.setTransactionId(response.getAuthorisationId());
			if (!StringUtils.isBlank(response.getSchemeTransactionIdentifier())) {
				paymentMethod.setToken3DsId(response.getSchemeTransactionIdentifier());
				paymentMethodService().updateNoCheck(paymentMethod);
			}

			if (!paymentResponseDto.getPaymentStatus().equals(PaymentStatusEnum.ACCEPTED)) {
				paymentResponseDto.setErrorCode(getErreurCodeAndMsg(response)[0]);
				paymentResponseDto.setErrorMessage(getErreurCodeAndMsg(response)[1]);
			}

			return paymentResponseDto;
		} else {
			paymentResponseDto.setErrorMessage("Empty response");
		}

		// Default fallback to ERROR
		paymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);

		return paymentResponseDto;
	}

	private String[] getErreurCodeAndMsg(WalletOrderResponse response) {
		Map<Object, Object> mapErrorCodeAndMsg = new HashMap();
		Provider provider = ((ProviderService) EjbUtils.getServiceInterface(ProviderService.class.getSimpleName())).getProvider();
		log.info("WalletOrderResponse AuthorisationId:{}  AcquirerResponseCode:{}  ComplementaryCode:{}  ResponseCode:{}", response.getAuthorisationId(),
				response.getAcquirerResponseCode(), response.getComplementaryCode(), response.getResponseCode());
		String[] codeAndMsg = { "notFound", "notFound" };

		String prefixCodeError = "";
		if (!StringUtils.isBlank(response.getAcquirerResponseCode())) {
			codeAndMsg[0] = "" + response.getAcquirerResponseCode();
			prefixCodeError = "A";
			mapErrorCodeAndMsg = (Map<Object, Object>) provider.getCfValue(CF_PRV_ACQUIRER_CODE_WLSIPS);
		} else {
			if (StringUtils.isBlank(response.getComplementaryCode()) || "0".equals(response.getComplementaryCode()) || "00".equals(response.getComplementaryCode())) {
				codeAndMsg[0] = response.getResponseCode();
				prefixCodeError = "R";
				mapErrorCodeAndMsg = (Map<Object, Object>) provider.getCfValue(CF_PRV_RESPONSE_CODE_WLSIPS);
			} else {
				codeAndMsg[0] = response.getComplementaryCode();
				prefixCodeError = "C";
				mapErrorCodeAndMsg = (Map<Object, Object>) provider.getCfValue(CF_PRV_COMPL_CODE_WLSIPS);
			}
		}
		codeAndMsg[1] = (String) mapErrorCodeAndMsg.get(codeAndMsg[0]);
		codeAndMsg[0] = prefixCodeError + codeAndMsg[0];
		return codeAndMsg;
	}

	@Override
	public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput)
			throws BusinessException {
		String returnUrl = hostedCheckoutInput.getReturnUrl();
		String walletUrl = paramBean().getProperty(WALLET_HOSTED_CHECK_OUT_URL_PROPERTY, "changeIt");

		PaymentHostedCheckoutResponseDto response = new PaymentHostedCheckoutResponseDto();
		PaymentHostedCheckoutResponseDto.Result result = response.getResult();

		result.setHostedCheckoutUrl(walletUrl);
		result.setHostedCheckoutVersion(paramBean().getProperty(PAYPAGE_INTERFACE_VERSION_PROPERTY, "HP_2.39"));
		result.setReturnUrl(returnUrl);

		CustomerAccount ca = customerAccountService().findById(hostedCheckoutInput.getCustomerAccountId());

		String merchantWalletId = ca.getId() + "_" + (ca.getCardPaymentMethods(false).size() + 1);

		String transactionReference = System.currentTimeMillis() + "R" + ((int) (Math.random() * 1000 + 1)) + "CA"
				+ ca.getId();
		
		if (hostedCheckoutInput.isOneShotPayment()) {
			transactionReference = "oneShot" + transactionReference;
		}

		String data ="amount="+hostedCheckoutInput.getAmount()+
        		"|authenticationData.authentAmount="+hostedCheckoutInput.getAuthenticationAmount()+
        		"|currencyCode="+hostedCheckoutInput.getCurrencyCode()+
        		"|merchantId="+paymentGateway.getMarchandId() +
        		"|normalReturnUrl="+returnUrl+
        		"|orderChannel="+OrderChannel.INTERNET.name()+
        		"|transactionReference="+transactionReference+
        		"|paymentPattern="+PaymentPattern.RECURRING_1.name()+
                "|normalReturnUrl=" + returnUrl +
                "|merchantSessionId=" + hostedCheckoutInput.getCustomerAccountId() +
                "|fraudData.challengeMode3DS=CHALLENGE_MANDATE"+
                "|captureMode="+CaptureMode.AUTHOR_CAPTURE.name()+
                "|captureDay=0"+
                "|merchantWalletId=" + merchantWalletId +
                "|keyVersion=" + paymentGateway.getWebhooksKeyId()+
                "|sealAlgorithm="+paramBean().getProperty(SEAL_ALGORITHM_PROPERTY,"HMAC-SHA-256");

		if (!StringUtils.isBlank(hostedCheckoutInput.getVariant())) {
			data += "|templateName=" + hostedCheckoutInput.getVariant();
		}

		if (!StringUtils.isBlank(hostedCheckoutInput.getAutomaticReturnUrl())) {
			data += "|automaticResponseUrl=" + hostedCheckoutInput.getAutomaticReturnUrl();
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
			throw new BusinessException("Seal couldn't be calculated for merchantId=" + paymentGateway.getMarchandId(),
					e);
		}
        log.info("getHostedCheckoutUrl data:{} seal:{}",data,result.getSeal());
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

	private WalletOrderRequest buildWalletOrderRequest(PaymentMethod paymentMethod, Long amount, String interfaceVersion,boolean isPayment) {
		WalletOrderRequest request = new WalletOrderRequest();
		request.setAmount(String.valueOf(amount));
		request.setCurrencyCode(Currency.valueOf(paymentMethod.getCustomerAccount().getTradingCurrency().getCurrencyCode()).getCode());
		request.setMerchantId(paymentGateway.getMarchandId());
		request.setOrderChannel(OrderChannel.INTERNET.name());
		if(isPayment) {
			request.setPaymentPattern(PaymentPattern.RECURRING_N.name());
		}
		request.setInterfaceVersion(interfaceVersion);
		request.setKeyVersion(paymentGateway.getWebhooksKeyId());
		request.setTransactionReference(buildTransactionReference());
		if (!StringUtils.isBlank(paymentMethod.getToken3DsId()) && isPayment) {
			request.setInitialSchemeTransactionIdentifier(paymentMethod.getToken3DsId());
		}

		// Needed for backward compatibility purpose, in 5.X version, the merchant
		// wallet ID is the customer account ID
		// Starting at 9.X version, the merchant wallet ID match the token ID of payment
		// method
		if (StringUtils.isBlank(paymentMethod.getInfo1())) {
			request.setMerchantWalletId(String.valueOf(paymentMethod.getCustomerAccount().getId()));
			request.setPaymentMeanId(paymentMethod.getTokenId());
		} else {
			request.setMerchantWalletId(paymentMethod.getTokenId());
			request.setPaymentMeanId(paymentMethod.getInfo1());
		}

		return request;
	}

    private String buildTransactionReference() {
		return UUID.randomUUID().toString().substring(0, paramBean().getPropertyAsInteger("sips.transReference.size",35)).replace("-", "x");
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

    private String getSealString(WalletOrderRequest request,boolean isPayment) {
    	
    	String token3ds = request.getInitialSchemeTransactionIdentifier();
    	if(StringUtils.isBlank(token3ds) || !isPayment) {
    		token3ds = "";
    	}
        return    request.getAmount()
                + request.getCurrencyCode()
                + token3ds
                + request.getInterfaceVersion()                 
                + request.getMerchantId()
                + request.getMerchantWalletId()  
                + request.getOrderChannel() 
                + request.getPaymentMeanId()
                + (isPayment ? request.getPaymentPattern() : "")
                + request.getTransactionReference();                 
    }
    
    
    private void processError(String message, Exception e, PaymentResponseDto paymentResponseDto) {
        log.error(message, e);
        paymentResponseDto.setErrorMessage(message);
        paymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
        paymentResponseDto.setPaymentID(paymentResponseDto.getPaymentID());
        paymentResponseDto.setTransactionId(paymentResponseDto.getTransactionId());
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

        return paramBeanFactory != null ? paramBeanFactory.getInstance() : ParamBean.getInstance();
    }

    private CustomerAccountService customerAccountService() {
        if (customerAccountService == null) {
            customerAccountService = (CustomerAccountService) EjbUtils.getServiceInterface(CustomerAccountService.class.getSimpleName());
        }

        return customerAccountService;
    }
    
    private ScriptInstanceService getScriptInstanceService() {
    	if(scriptInstanceService != null) {
    		return scriptInstanceService;
    	}
    	scriptInstanceService = (ScriptInstanceService) EjbUtils.getServiceInterface(ScriptInstanceService.class.getSimpleName());
    	return scriptInstanceService;
    }
    
    private PaymentMethodService paymentMethodService() {
        if (paymentMethodService == null) {
        	paymentMethodService = (PaymentMethodService) EjbUtils.getServiceInterface(PaymentMethodService.class.getSimpleName());
        }

        return paymentMethodService;
    }
}