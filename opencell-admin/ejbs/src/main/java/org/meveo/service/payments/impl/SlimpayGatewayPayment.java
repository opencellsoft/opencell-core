package org.meveo.service.payments.impl;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MandatStateEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.Method;
import com.slimpay.hapiclient.http.Request;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import com.slimpay.hapiclient.util.EntityConverter;

/**
 * 
 * @author anasseh
 *
 */
@PaymentGatewayClass
public class SlimpayGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(SlimpayGatewayPayment.class);
    private String API_URL = ParamBean.getInstance().getProperty("slimPay.apiURL", null);
    private String PROFILE = ParamBean.getInstance().getProperty("slimPay.profile", null);
    private String TOKEN_END_POINT = ParamBean.getInstance().getProperty("slimPay.tokenEndPoint", "/oauth/token");
    private String USER_ID = ParamBean.getInstance().getProperty("slimPay.userId", null);
    private String SECRET_KEY = ParamBean.getInstance().getProperty("slimPay.secretKey", null);
    private String SCOPE = ParamBean.getInstance().getProperty("slimPay.scope", "api");
    private String RELNS = ParamBean.getInstance().getProperty("slimPay.relns", null);
    private boolean isCheckMandatBeforePayment = "true".equals(ParamBean.getInstance().getProperty("slimPay.checkMandatBeforePayment", "true"));
    private String SCHEME = ParamBean.getInstance().getProperty("slimPay.scheme", "SEPA.DIRECT_DEBIT.CORE");

    private HapiClient client = null;

    private void connect() {
        client = new HapiClient.Builder().setApiUrl(API_URL).setProfile(PROFILE).setAuthenticationMethod(
            new Oauth2BasicAuthentication.Builder().setTokenEndPointUrl(TOKEN_END_POINT).setUserid(USER_ID).setPassword(SECRET_KEY).setScope(SCOPE).build()).build();
    }
    
    private HapiClient getClient() {
        // if (client == null) {
        connect();
        // }
        return client;
    }    
    
    

    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        try {
            String label = additionalParams != null ? (String) additionalParams.get("invoiceNumber") : null;
            log.trace("doPaymentSepa request:" + (getSepaPaymentRequest(USER_ID, paymentToken.getMandateIdentification(), ctsAmount,
                paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode(), SCHEME, label).toString()));

            if (isCheckMandatBeforePayment) {
                MandatInfoDto mandatInfo = checkMandat(paymentToken.getMandateIdentification(),null);
                if (MandatStateEnum.active != mandatInfo.getState()) {
                    doPaymentResponseDto.setErrorMessage("Mandate " + paymentToken.getMandateIdentification() + " not active");
                    doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
                    return doPaymentResponseDto;
                }
            }

            Follow follow = new Follow.Builder(new CustomRel(RELNS + "create-payins")).setMethod(Method.POST)
                .setMessageBody(EntityConverter.jsonToStringEntity(getSepaPaymentRequest(USER_ID, paymentToken.getMandateIdentification(), ctsAmount,
                    paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode(), SCHEME, label)))
                .build();

            try {
                Resource response = getClient().send(follow);
                JsonObject body = response.getState();
                doPaymentResponseDto.setPaymentID(body.getString("id"));
                doPaymentResponseDto.setPaymentStatus(mappingStatus(body.getString("executionStatus")));
            } catch (HttpException e) {
                doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
                doPaymentResponseDto.setErrorMessage(e.getResponseBody());
                doPaymentResponseDto.setErrorCode("" + e.getStatusCode());
                log.error("Error on slimpay sepa payment:", e);
            }
        } catch (Exception e) {
            doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
            doPaymentResponseDto.setErrorMessage(e.getMessage());
        }
        log.trace("doPaymentSepa response:" + doPaymentResponseDto.toString());
        return doPaymentResponseDto;
    }

    @Override
    public MandatInfoDto checkMandat(String mandatReference,String mandateId) throws BusinessException {
        log.trace("checkMandat request:" + (getCheckMandatRequest(USER_ID, mandatReference,mandateId).toString()));
        Follow follow = new Follow.Builder(new CustomRel(RELNS + "get-mandates")).setMethod(Method.GET).setUrlVariable("creditorReference", USER_ID)
            .setUrlVariable("reference", mandatReference).build();
        MandatInfoDto mandatInfoDto = null;
        try {
            Resource response = getClient().send(follow);
            JsonObject body = response.getState();
            mandatInfoDto = getMandateFromJson(body);
        } catch (Exception e) {
            log.error("Error on slimpay check mandat:", e);
            throw new BusinessException(e.getMessage());
        }
        log.trace("checkMandat response:" + (mandatInfoDto.toString()));
        return mandatInfoDto;
    }

    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        log.trace("checkPayment request:" + (getCheckPaymentRequest(paymentID, paymentMethodType).toString()));
        Follow follow = new Follow.Builder(new CustomRel(RELNS + "get-direct-debits")).setMethod(Method.GET).setUrlVariable("id", paymentID).build();
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        try {
            Resource response = getClient().send(follow);
            JsonObject body = response.getState();
            paymentResponseDto.setPaymentStatus(mappingStatus(body.getString("executionStatus")));
            paymentResponseDto.setPaymentID(paymentID);
        } catch (Exception e) {
            log.error("Error on slimpay check payment:", e);
            throw new BusinessException(e.getMessage());
        }
        log.trace("checkPayment response:" + (paymentResponseDto.toString()));
        return paymentResponseDto;
    }

    /**
     * Retrieve the subscriber as a customerAccount from the signMandate order.
     * @param reference
     * @return
     * @throws BusinessException
     */
    public CustomerAccountDto getSubscriberFromOrder(String referenceOrder) throws BusinessException {
        CustomerAccountDto customerAccountDto = new CustomerAccountDto();
        log.trace("getSubscriberFromOrder request:" + ("/orders/"+referenceOrder+"/subscriber"));
        try {          
            Request request = new Request.Builder("/orders/"+referenceOrder+"/subscriber").setMethod(Method.GET).build();
            Resource response = getClient().send(request);
            JsonObject body = response.getState();
            customerAccountDto.setCode(body.getString("reference"));
        } catch (Exception e) {
            log.error("Error on slimpay getSubscriberFromOrder:", e);
            throw new BusinessException(e.getMessage());
        }
        log.trace("getSubscriberFromOrder response:" + (customerAccountDto.toString()));
        return customerAccountDto;
    }

    /**
     * @param paymentID
     * @param paymentMethodType
     * @return
     */
    private JsonObject getCheckPaymentRequest(String paymentID, PaymentMethodEnum paymentMethodType) {
        return Json.createObjectBuilder().add("id", paymentID).build();
    }

    /**
     * Build creditor json object.
     * 
     * @param ref
     * @return
     */
    private JsonObject getCreditor(String ref) {
        return Json.createObjectBuilder().add("reference", ref).build();
    }

    /**
     * Build Mandat json object.
     * 
     * @param mandate
     * @return
     */
    private JsonObject getMandate(String mandate) {
        return Json.createObjectBuilder().add("reference", mandate).build();
    }

    /**
     * Build Sepapayment request json object.
     * 
     * @param creditor
     * @param mandate
     * @param amountCts
     * @param currency
     * @param scheme
     * @param label
     * @return
     */
    private JsonObject getSepaPaymentRequest(String creditor, String mandate, Long amountCts, String currency, String scheme, String label) {
        JsonObjectBuilder sepaRequestBuilder = Json.createObjectBuilder().add("creditor", getCreditor(creditor)).add("mandate", getMandate(mandate));
        sepaRequestBuilder = sepaRequestBuilder.add("amount", new Double(amountCts / 100d)).add("scheme", scheme);
        if (!StringUtils.isBlank(label)) {
            sepaRequestBuilder = sepaRequestBuilder.add("label", label);
        }
        return sepaRequestBuilder.build();
    }

    /**
     * Build checkMandat request json object.
     * 
     * @param creditor
     * @param mandateReference Mandate reference (RUM)
     * @param mandateId Mandate ID
     * @return
     */
    private JsonObject getCheckMandatRequest(String creditor, String mandateReference,String mandateId) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder().add("creditorReference", creditor);
        if(!StringUtils.isBlank(mandateReference)) {
            jsonObjectBuilder = jsonObjectBuilder.add("reference", mandateReference);
        }
        if(!StringUtils.isBlank(mandateId)) {
            jsonObjectBuilder = jsonObjectBuilder.add("id", mandateId);
        }
        return jsonObjectBuilder.build();
    }

    /**
     * Mapping slimpay payment status to Opencell payment status.
     * 
     * @param slimpayStatus
     * @return
     */
    private PaymentStatusEnum mappingStatus(String slimpayStatus) {
        if (slimpayStatus == null) {
            return PaymentStatusEnum.ERROR;
        }
        if ("processed".equals(slimpayStatus)) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if ("toprocess".equals(slimpayStatus) || "processing".equals(slimpayStatus) || "toreplay".equals(slimpayStatus)) {
            return PaymentStatusEnum.PENDING;
        }
        return PaymentStatusEnum.REJECTED;
    }

    private MandatInfoDto getMandateFromJson(JsonObject body) {
        MandatInfoDto mandatInfoDto = new MandatInfoDto();
        mandatInfoDto.setDateCreated(DateUtils.parseDateWithPattern(body.getString("dateCreated"), "yyyy-MM-dd'T'HH:mm:ss.S"));
        mandatInfoDto.setDateSigned(DateUtils.parseDateWithPattern(body.getString("dateSigned"), "yyyy-MM-dd'T'HH:mm:ss.S"));
        mandatInfoDto.setState(MandatStateEnum.valueOf(body.getString("state")));
        mandatInfoDto.setId(body.getString("id"));
        mandatInfoDto.setInitialScore(body.getInt("initialScore"));
        mandatInfoDto.setPaymentScheme(body.getString("paymentScheme"));
        mandatInfoDto.setReference(body.getString("reference"));
        mandatInfoDto.setStandard(body.getString("standard"));
        return mandatInfoDto;
    }
    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        throw new UnsupportedOperationException();
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
    public void doBulkPaymentAsFile(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }
}
