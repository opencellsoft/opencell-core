package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
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

    private static HapiClient client = null;
    
    private static void connect() {
        System.out.println("connect ...");
        client =  new HapiClient.Builder()
                  .setApiUrl("https://api.preprod.slimpay.com")
                  .setProfile("https://api.slimpay.net/alps/v1")
                  .setAuthenticationMethod(
                  new Oauth2BasicAuthentication.Builder()
                      .setTokenEndPointUrl("/oauth/token")
                      .setUserid("opencelldev")
                      .setPassword("OSpPCH4jYWfg7SRT53d5OTNCIcodepvfpfKHULQi")
                      .setScope("api")
                      .build()
                  )
                  .build();
        
    }

    private static HapiClient getClient() {
        System.out.println("getClient ...");
        if (client == null) {
            connect();
        }
        return client;
    }


    private JsonObject getCreditor(String ref) {
        return Json.createObjectBuilder().add("reference", ref).build();
    }
    private JsonObject getMandate(String mandate) {
        return Json.createObjectBuilder().add("reference", mandate).build();
    }
    private JsonObject getSepaRequest(String creditor,String mandate,String ref, Long amountCts,String currency,String scheme,String label,Date execution ) {
        return Json.createObjectBuilder()
                .add("creditor", getCreditor(creditor))
                .add("mandate", getMandate(mandate))
                .add("reference", ref)
                .add("amount", new Double(amountCts/100d))
                .add("scheme", scheme)
                .add("label", label)                
                .build();        
    }
    
    public void doSepaPayment(String creditor,String mandate,String ref, Long amountCts,String currency,String scheme,String label,Date execution) {
        System.out.println("doSepaPayment ...");
        Follow follow = new Follow.Builder(new CustomRel("https://api.slimpay.net/alps#create-payins"))
                 .setMethod(Method.POST)
                  .setMessageBody( EntityConverter.jsonToStringEntity(getSepaRequest(creditor, mandate, ref, amountCts, currency, scheme, label, execution)))
                  .build();
       
        System.out.println("before send ...");
                try {
                    System.out.println("getClient().getApiUrl():"+getClient().getApiUrl());
                    System.out.println("getClient().profile():"+getClient().getProfile());
                    
                    
                  Resource response = getClient().send(follow);
                  JsonObject body = response.getState();
                  String id = body.getString("id"); 
                  String status = body.getString("executionStatus"); 
                  log.info("\n\n\n id:"+id+" \n\n\n");
                  log.info("\n\n\n status:"+status+" \n\n\n");
                } catch (HttpException e) {
                    System.out.println("e.getStatusCode():"+e.getStatusCode());
                    System.out.println("e.getResponseBody():"+e.getResponseBody());
                 e.printStackTrace();
                }    
    }
    
    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType, String countryCode) throws BusinessException {
       return null;
    }

    @Override
    public PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {

        return null;
    }

    @Override
    public PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        return null;
    }

    private PayByCardResponseDto doPayment(CardPaymentMethod paymentToken, Long ctsAmount, CustomerAccount customerAccount, String cardNumber, String ownerName, String cvv,
            String expirayDate, CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {

       return null;
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        
    }

    private Address getBillingAddress(CustomerAccount customerAccount, String countryCode) {
        Address billingAddress = new Address();
        if (customerAccount.getAddress() != null) {
            billingAddress.setAdditionalInfo(customerAccount.getAddress().getAddress3());
            billingAddress.setCity(customerAccount.getAddress().getCity());
            billingAddress.setCountryCode(countryCode);
            billingAddress.setHouseNumber(customerAccount.getAddress().getAddress1());
            billingAddress.setState(customerAccount.getAddress().getState());
            billingAddress.setStreet(customerAccount.getAddress().getAddress2());
            billingAddress.setZip(customerAccount.getAddress().getZipCode());
        }
        return billingAddress;
    }

    private PaymentStatusEnum mappingStaus(String ingenicoStatus) {
        if (ingenicoStatus == null) {
            return PaymentStatusEnum.REJECTED;
        }
        if ("CREATED".equals(ingenicoStatus) || "PAID".equals(ingenicoStatus)) {
            return PaymentStatusEnum.ACCEPTED;
        }
        if (ingenicoStatus.startsWith("PENDING")) {
            return PaymentStatusEnum.PENDING;
        }
        return PaymentStatusEnum.REJECTED;
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

