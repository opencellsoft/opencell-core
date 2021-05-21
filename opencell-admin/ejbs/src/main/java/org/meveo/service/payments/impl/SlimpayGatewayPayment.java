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

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
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
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Link;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.Follow.Builder;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.Method;
import com.slimpay.hapiclient.http.Request;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import com.slimpay.hapiclient.util.EntityConverter;

/**
 *
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
@PaymentGatewayClass
public class SlimpayGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(SlimpayGatewayPayment.class);

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    private ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface("ParamBeanFactory");
    
    private PaymentGateway paymentGateway = null; 

    private String getApiUrl() {
        return paramBeanFactory.getInstance().getProperty("slimPay.apiURL", "changeIt");
    }

    private String getProfile() {
        return paymentGateway.getProfile();
    }

    private String getTokenEndPoint() {
        return paramBeanFactory.getInstance().getProperty("slimPay.tokenEndPoint", "/oauth/token");
    }

    private String getUserId() {
        return paymentGateway.getMarchandId();
    }

    private String getSecretKey() {
        return paymentGateway.getSecretKey();
    }

    private String getScope() {
        return paramBeanFactory.getInstance().getProperty("slimPay.scope", "api");
    }

    private String getRelns() {
        return paramBeanFactory.getInstance().getProperty("slimPay.relns", "changeIt");
    }

    private boolean isCheckMandatBeforePayment() {
        return "true".equals(paramBeanFactory.getInstance().getProperty("slimPay.checkMandatBeforePayment", "true"));
    }

    private String getScheme() {
        return paramBeanFactory.getInstance().getProperty("slimPay.scheme", "SEPA.DIRECT_DEBIT.CORE");
    }

    private HapiClient connect() {
        HapiClient client = new HapiClient.Builder().setApiUrl(getApiUrl()).setProfile(getProfile())
            .setAuthenticationMethod(
                new Oauth2BasicAuthentication.Builder().setTokenEndPointUrl(getTokenEndPoint()).setUserid(getUserId()).setPassword(getSecretKey()).setScope(getScope()).build())
            .build();
        return client;
    }

    private HapiClient getClient() {
        return connect();
    }

    @Override
    public Object getClientObject() {
        return null;
    }

    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPaymentSepaOrRefundSepa(paymentToken, ctsAmount, additionalParams, true);
    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        return doPaymentSepaOrRefundSepa(paymentToken, ctsAmount, additionalParams, false);
    }

    @Override
    public MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
        log.trace("checkMandat request:" + (getCheckMandatRequest(getUserId(), mandatReference, mandateId).toString()));
        Builder builder = new Follow.Builder(new CustomRel(getRelns() + "get-mandates")).setMethod(Method.GET).setUrlVariable("creditorReference", getUserId());
        if (!StringUtils.isBlank(mandatReference)) {
            builder = builder.setUrlVariable("reference", mandatReference);
        }
        if (!StringUtils.isBlank(mandateId)) {
            builder = builder.setUrlVariable("id", mandateId);
        }
        Follow follow = builder.build();
        MandatInfoDto mandatInfoDto = null;
        try {
            Resource response = getClient().send(follow);
            JsonObject body = response.getState();
            Link bankAccountLink = response.getLink(new CustomRel(getRelns() + "get-bank-account"));
            Request request = new Request.Builder(bankAccountLink.getHref()).setMethod(Method.GET).build();
            Resource bankAccountResponse = getClient().send(request);
            mandatInfoDto = getMandateFromJson(body, bankAccountResponse.getState());
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
        Follow follow = new Follow.Builder(new CustomRel(getRelns() + "get-direct-debits")).setMethod(Method.GET).setUrlVariable("id", paymentID).build();
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
     * 
     * @param referenceOrder Order reference
     * @return Customer account dto
     * @throws BusinessException Business Exception
     */
    public CustomerAccountDto getSubscriberFromOrder(String referenceOrder) throws BusinessException {
        CustomerAccountDto customerAccountDto = new CustomerAccountDto();
        log.trace("getSubscriberFromOrder request:" + ("/orders/" + referenceOrder + "/subscriber"));
        try {
            Request request = new Request.Builder("/orders/" + referenceOrder + "/subscriber").setMethod(Method.GET).build();
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
     * Initiate a payment or refund sepa whit valid mandat.
     * 
     * @param paymentToken payment token(mandat)
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @param isPayment true for payment ,false for payout(refund)
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    private PaymentResponseDto doPaymentSepaOrRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams, boolean isPayment)
            throws BusinessException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        try {
            String label = additionalParams != null ? (String) additionalParams.get("invoiceNumber") : null;
            log.trace("doPaymentSepaOrRefundSepa request:" + (getSepaPaymentRequest(getUserId(), paymentToken.getMandateIdentification(), ctsAmount,
                paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode(), getScheme(), label).toString()));

            if (isCheckMandatBeforePayment()) {
                MandatInfoDto mandatInfo = checkMandat(paymentToken.getMandateIdentification(), null);
                if (MandatStateEnum.active != mandatInfo.getState()) {
                    doPaymentResponseDto.setErrorMessage("Mandate " + paymentToken.getMandateIdentification() + " not active");
                    doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
                    return doPaymentResponseDto;
                }
            }
            String operationName = isPayment ? "create-payins" : "create-payouts";
            Follow follow = new Follow.Builder(new CustomRel(getRelns() + operationName)).setMethod(Method.POST)
                .setMessageBody(EntityConverter.jsonToStringEntity(getSepaPaymentRequest(getUserId(), paymentToken.getMandateIdentification(), ctsAmount,
                    paymentToken.getCustomerAccount().getTradingCurrency().getCurrencyCode(), getScheme(), label)))
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
                log.error("Error on slimpay sepa payment/refund:", e);
            }
        } catch (Exception e) {
            doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
            doPaymentResponseDto.setErrorMessage(e.getMessage());
        }
        log.trace("doPaymentSepaOrRefundSepa response:" + doPaymentResponseDto.toString());
        return doPaymentResponseDto;
    }

    /**
     * Build check payment request
     * 
     * @param paymentID The payment id
     * @param paymentMethodType The payment Method Type
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
    private JsonObject getCheckMandatRequest(String creditor, String mandateReference, String mandateId) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder().add("creditorReference", creditor);
        if (!StringUtils.isBlank(mandateReference)) {
            jsonObjectBuilder = jsonObjectBuilder.add("reference", mandateReference);
        }
        if (!StringUtils.isBlank(mandateId)) {
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

    private MandatInfoDto getMandateFromJson(JsonObject bodyGetMandat, JsonObject bodyBankAccount) {
        MandatInfoDto mandatInfoDto = new MandatInfoDto();
        mandatInfoDto.setDateCreated(DateUtils.parseDateWithPattern(bodyGetMandat.getString("dateCreated"), "yyyy-MM-dd'T'HH:mm:ss.S"));
        mandatInfoDto.setDateSigned(DateUtils.parseDateWithPattern(bodyGetMandat.getString("dateSigned"), "yyyy-MM-dd'T'HH:mm:ss.S"));
        mandatInfoDto.setState(MandatStateEnum.valueOf(bodyGetMandat.getString("state")));
        mandatInfoDto.setId(bodyGetMandat.getString("id"));
        mandatInfoDto.setInitialScore(bodyGetMandat.getInt("initialScore"));
        mandatInfoDto.setPaymentScheme(bodyGetMandat.getString("paymentScheme"));
        mandatInfoDto.setReference(bodyGetMandat.getString("reference"));
        mandatInfoDto.setStandard(bodyGetMandat.getString("standard"));
        mandatInfoDto.setIban(bodyBankAccount.getString("iban"));
        mandatInfoDto.setBic(bodyBankAccount.getString("bic"));
        mandatInfoDto.setBankName(bodyBankAccount.getString("institutionName"));
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
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
        
    }

	@Override
	public String createInvoice(Invoice invoice) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
