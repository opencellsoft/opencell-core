package org.meveo.service.payments.impl;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MandatStateEnum;
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
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import com.slimpay.hapiclient.util.EntityConverter;

@PaymentGatewayClass
public class SlimpayGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(SlimpayGatewayPayment.class);
    private String API_URL = ParamBean.getInstance().getProperty("slimPay.apiURL", "https://api.preprod.slimpay.com");
    private String PROFILE = ParamBean.getInstance().getProperty("slimPay.profile", "https://api.slimpay.net/alps/v1");
    private String TOKEN_END_POINT = ParamBean.getInstance().getProperty("slimPay.tokenEndPoint", "/oauth/token");
    private String USER_ID = ParamBean.getInstance().getProperty("slimPay.userId", "opencelldev");
    private String SECRET_KEY = ParamBean.getInstance().getProperty("slimPay.secretKey", "OSpPCH4jYWfg7SRT53d5OTNCIcodepvfpfKHULQi");
    private String SCOPE = ParamBean.getInstance().getProperty("slimPay.scope", "api");
    private String RELNS = ParamBean.getInstance().getProperty("slimPay.relns", "https://api.slimpay.net/alps#");
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
    public PayByCardResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        PayByCardResponseDto doPaymentResponseDto = new PayByCardResponseDto();
        try {
            String label = additionalParams != null ? (String) additionalParams.get("invoiceNumber") : null;
            log.trace("doPaymentSepa request:" + (getSepaPaymentRequest(USER_ID, paymentToken.getMandateIdentification(), ctsAmount,
                "EUR"/* paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode() */, SCHEME, label).toString()));

            if (isCheckMandatBeforePayment) {
                MandatInfoDto mandatInfo = checkMandat(paymentToken.getMandateIdentification());
                if (MandatStateEnum.active != mandatInfo.getState()) {
                    doPaymentResponseDto.setErrorMessage("Mandate " + paymentToken.getMandateIdentification() + " not active");
                    doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
                    return doPaymentResponseDto;
                }
            }

            Follow follow = new Follow.Builder(new CustomRel(RELNS + "create-payins")).setMethod(Method.POST)
                .setMessageBody(EntityConverter.jsonToStringEntity(getSepaPaymentRequest(USER_ID, paymentToken.getMandateIdentification(), ctsAmount,
                    "EUR"/* paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode() */, SCHEME, label)))
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
        log.trace("doPaymentSepa response:"+doPaymentResponseDto.toString());
        return doPaymentResponseDto;
    }

    @Override
    public MandatInfoDto checkMandat(String mandatReference) throws BusinessException {
        log.trace("checkMandat request:" + (getCheckMandatRequest(USER_ID, mandatReference).toString()));
        Follow follow = new Follow.Builder(new CustomRel(RELNS + "get-mandates")).setMethod(Method.GET).setUrlVariable("creditorReference", USER_ID)
            .setUrlVariable("reference", mandatReference).build();
        MandatInfoDto mandatInfoDto = new MandatInfoDto();
        try {
            Resource response = getClient().send(follow);
            JsonObject body = response.getState();
            mandatInfoDto.setDateCreated(DateUtils.parseDateWithPattern(body.getString("dateCreated"), "yyyy-MM-dd'T'HH:mm:ss.S"));
            mandatInfoDto.setDateSigned(DateUtils.parseDateWithPattern(body.getString("dateSigned"), "yyyy-MM-dd'T'HH:mm:ss.S"));
            mandatInfoDto.setState(MandatStateEnum.valueOf(body.getString("state")));
            mandatInfoDto.setId(body.getString("id"));
            mandatInfoDto.setInitialScore(body.getInt("initialScore"));
            mandatInfoDto.setPaymentScheme(body.getString("paymentScheme"));
            mandatInfoDto.setReference(body.getString("reference"));
            mandatInfoDto.setStandard(body.getString("standard"));
        } catch (Exception e) {
            log.error("Error on slimpay check mandat:", e);
            throw new BusinessException(e.getMessage());
        }
        log.trace("checkMandat response:" + (mandatInfoDto.toString()));
        return mandatInfoDto;
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
     * @param mandateReference
     * @return
     */
    private JsonObject getCheckMandatRequest(String creditor, String mandateReference) {
        return Json.createObjectBuilder().add("creditorReference", creditor).add("reference", mandateReference).build();
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

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType, String countryCode) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {

        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
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

}
