package org.meveo.service.script.payment;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Query;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.Script;

import com.ingenico.connect.gateway.sdk.java.RequestHeader;
import com.ingenico.connect.gateway.sdk.java.domain.errors.definitions.APIError;
import com.ingenico.connect.gateway.sdk.java.domain.payment.PaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.refund.RefundResponse;
import com.ingenico.connect.gateway.sdk.java.domain.webhooks.WebhooksEvent;
import com.ingenico.connect.gateway.sdk.java.webhooks.InMemorySecretKeyStore;
import com.ingenico.connect.gateway.sdk.java.webhooks.SignatureValidationException;
import com.ingenico.connect.gateway.sdk.java.webhooks.Webhooks;
import com.ingenico.connect.gateway.sdk.java.webhooks.WebhooksHelper;

 

public class PaymentCallBack extends Script {

    private PaymentService paymentService = (PaymentService) getServiceInterface("PaymentService");
    private PaymentGatewayService paymentGatewayService = (PaymentGatewayService) getServiceInterface("PaymentGatewayService");
    private final CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private final  AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());
    
    private static final String CF_RETRY_PAYMENT = "CF_RETRY_PAYMENT";
    private final String CF_SEPA_INGENICO_REJECT_NO_RETRY_CODES = "CF_SEPA_INGENICO_REJECT_NO_RETRY_CODES";
    private final ProviderService providerService = (ProviderService) getServiceInterface(ProviderService.class.getSimpleName());
    private static final String CF_REJECTS_COUNT = "CF_REJECTS_COUNT";

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        log.info("EXECUTE PaymentCallBackScript methodContext {} ", methodContext);

        InboundRequest inReq = (InboundRequest) methodContext.get("event");

        if ("GET".equalsIgnoreCase(inReq.getMethod())) {
            Map<String, String> headers = inReq.getHeaders();
            for (String headerKey : headers.keySet()) {
                if ("X-GCS-Webhooks-Endpoint-Verification".equalsIgnoreCase(headerKey)) {
                    String headerValue = headers.get(headerKey);
                    inReq.setResponseBody(headerValue);
                }
            }
        } else {
            
            final String reqBody = inReq.getBody();
          log.info("reqBody={}",reqBody);
            String webhooksKeyId = null;
            String webhooksKey = null;
            try {

                List<RequestHeader> requestHeaders = getWebhookRequestHeaders(inReq.getHeaders());
               log.info("requestHeaders={}",requestHeaders);
                webhooksKeyId =  this.getHeaderValue(requestHeaders, "X-GCS-KeyId");

                InMemorySecretKeyStore keyStore = InMemorySecretKeyStore.INSTANCE;
                webhooksKey = getWebhookSecretKey(webhooksKeyId);
                log.info("-------------------------------webhooksKeyID =  "+webhooksKeyId + " webhooksKey = " + webhooksKey);
                keyStore.storeSecretKey(webhooksKeyId, webhooksKey);

                WebhooksHelper helper = Webhooks.createHelper(keyStore);
                WebhooksEvent webhooksEvent = helper.unmarshal(reqBody, requestHeaders);

                String eventType = webhooksEvent.getType();
                log.info("PaymentCallBackScript Event type : [{}] ", eventType);
                if ("payment.rejected_capture".equals(eventType) || "payment.rejected".equals(eventType)) {

                    log.debug(">>> Rejection Event type : [{}] ", eventType);

                    PaymentResponse payment = webhooksEvent.getPayment();
                    if (payment == null) {
                        throw new BusinessException(String.format("PaymentResponse not found on webhooksEvent : [%s]", webhooksEvent));
                    }
                    final String paymentId = payment.getId();
                    String errorCode = getErrorCode(payment);

                    log.debug(">>> Rejection : [errorCode : {}, paymentId : {}] ", eventType, paymentId);
                    List<String> listRsnCodesToDontRetry = this.fetchRsnCodesToDontRetry(CF_SEPA_INGENICO_REJECT_NO_RETRY_CODES);
                    // don't retry the payment later if errorCode in listRsnCodesToDontRetry :
                    if (safe( listRsnCodesToDontRetry ).contains(errorCode)) {
                        this.retryPayments(paymentId,"NO");
                    }
                    this.rejectPayment(paymentId, errorCode);
                }else if ("payment.captured".equals(eventType)) {
                     try {
                    	 PaymentResponse payment = webhooksEvent.getPayment();
                         if (payment == null) {
                             throw new BusinessException(String.format("PaymentResponse not found on webhooksEvent : [%s]", webhooksEvent));
                         }
                         final String paymentId = payment.getId();
                         AccountOperation ao = accountOperationService.findByReference(paymentId);
                         if (ao == null) {
                             throw new BusinessException(String.format("AccountOperation not found for paymentId = [%s]", paymentId));
                         }
                    	 AccountOperation invoiceAO=getMatchedInvoiceOperation(ao);
                    	 if(invoiceAO!=null) {
                    	 invoiceAO.setPaymentInfo(ao.getReference());
                    	 accountOperationService.update(invoiceAO);
                    	 }
                      } catch (Exception exception) {
                          throw new BusinessException(exception);
                      }

                } else if ("refund.captured".equals(eventType) || "refund.refund_requested".equals(eventType)) {
                	try {
                		log.info("Refund Event type : ",eventType);
                		RefundResponse refund = webhooksEvent.getRefund();
                		if (refund == null) {
                			throw new BusinessException(String.format("RefundResponse not found on webhooksEvent : [%s]", webhooksEvent));
                		}
                		final String refundId = refund.getId();
                		log.info("Refund refund ID  : ",refundId);
                		AccountOperation ao = accountOperationService.findByReference(refundId);
                		if (ao == null) {
                			throw new BusinessException(String.format("AccountOperation not found for refundId = [%s]", refundId));
                		}
                		AccountOperation invoiceAO=getMatchedInvoiceOperation(ao);
                		log.info("Credit note=",invoiceAO.getReference());
                		if(invoiceAO!=null) {
                		invoiceAO.setPaymentInfo(ao.getReference());
                		accountOperationService.update(invoiceAO);
                		}
                	} catch (Exception exception) {
                		throw new BusinessException(exception);
                	}

                }else {
                    log.debug(" Event type : [{}] is not concerned by this callback ", eventType);
                }
            } catch (SignatureValidationException e) {
                log.debug(" SignatureValidationException [{}]", e.getMessage());

                // *************** To activate juste for QA tests : ******************************
                // This code is just a workaround to be able to test the callback by a mock request
                // inReq.setResponseBody(generateSignature(reqBody, webhooksKey) );
                // ****************************** ****************************** ******************
            } 

        }
    }
    
    @SuppressWarnings("unchecked")
    private List<String> fetchRsnCodesToDontRetry(String cfKey) {
        try {
            Provider appProvider = providerService.getProvider();
            log.debug(" appProvider found : {} ", appProvider);
            List<String> reslut = (List<String>) this.fetchCFConfigValue(appProvider, cfKey, false);
            log.debug("fetchRsnCodesToDontRetry : {} ",reslut);
            return reslut;
        } catch (BusinessException e) {
            log.error(e.getMessage());
            return null;
        }
    }
    
    private Object fetchCFConfigValue(ICustomFieldEntity entity, String cfKey, boolean isMandatory) throws BusinessException {
        Object cfValue = customFieldInstanceService.getCFValue(entity, cfKey);
        if (isMandatory && (cfValue == null || (cfValue instanceof String && StringUtils.isEmpty(String.valueOf(cfValue))))) {
            throw new BusinessException(" Missing mandatory config [" + cfKey + "]");
        }
        return cfValue;
    }

    private void rejectPayment(final String paymentId, String errorCode) {
        try {
            paymentService.paymentCallback(paymentId, PaymentStatusEnum.REJECTED, errorCode, errorCode);
            this.retryPayments(paymentId,"YES");
        } catch (Exception e) {
            log.debug(" Error on paymentService.paymentCallback : [paymentId={},errorCode={}]", paymentId, errorCode);
        }
    }

    private String getErrorCode(PaymentResponse payment) {
        try {
            String errorCode = null;
            List<APIError> erros = payment.getStatusOutput().getErrors();
            if (CollectionUtils.isNotEmpty(erros)) {
                errorCode = erros.get(0).getCode();
            }
            return errorCode;
        } catch (Exception e) {
            log.error(" Error on getErrorCode : [{}]  ", e.getMessage());
            return null;
        }
    }

    private List<RequestHeader> getWebhookRequestHeaders(Map<String, String> headers) {
        List<RequestHeader> webhookHeaders = new ArrayList<>();
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> webhookHeaders.add(new RequestHeader(key, value)));
        }
        return webhookHeaders;
    }

    private void retryPayments(String paymentId,String retryPayment) {
        try {
            AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());
            CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());

            AccountOperation ao = accountOperationService.findByReference(paymentId);
            if (ao == null) {
                throw new BusinessException(String.format("AccountOperation not found for paymentId = [%s]", paymentId));
            }
            Subscription subscription = this.getSubscription(ao);
            if (subscription == null) {
                throw new BusinessException(String.format("Subscription not found for paymentId = [%s]", paymentId));
            }
            customFieldInstanceService.setCFValue(subscription, CF_RETRY_PAYMENT,retryPayment);
            if("YES".equals(retryPayment)) {
            	Long nbrRejects=(Long)customFieldInstanceService.getCFValue(subscription, CF_REJECTS_COUNT);
            	customFieldInstanceService.setCFValue(subscription, CF_REJECTS_COUNT,nbrRejects+1);
            }
        } catch (Exception e) {
            log.error(" Error on dontRetryPayments : [{}]", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Subscription getSubscription(AccountOperation ao) {
        try { // For Fnac Dary : there is one invoice per AO , and one user account per ba and one subscription per UA :return
              // ao.getInvoices().get(0).getBillingAccount().getUsersAccounts().get(0).getSubscriptions().get(0);
            final SubscriptionService subscriptionService = (SubscriptionService) EjbUtils.getServiceInterface(SubscriptionService.class.getSimpleName());
            String queryOneSubscriptionFromAo = "select distinct s from Subscription s, AccountOperation ao  where ao.id =:aoId and s.userAccount.billingAccount.customerAccount = ao.customerAccount";
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("aoId", ao.getId());
            Object objSubscription = subscriptionService.executeSelectQuery(queryOneSubscriptionFromAo, queryParams);

            return ((List<Subscription>) objSubscription).get(0);
        } catch (Exception e) {
            log.error(" Error on getSubscription : [{}] ", e.getMessage(), e);
            return null;
        }
    }


    private String getHeaderValue(List<RequestHeader> requestHeaders, String headerName) {
        String value = null;
        for (RequestHeader header : requestHeaders) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                if (value == null) {
                    value = header.getValue();
                } else {
                    throw new SignatureValidationException("enocuntered multiple occurrences of header '" + headerName + "'");
                }
            }
        }
        if (value == null) {
            throw new SignatureValidationException("could not find header '" + headerName + "'");
        }
        return value;
    }

    @SuppressWarnings("unused")
    private String generateSignature(String bodyString , String secretKey) {
        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA256");
            final Charset CHARSET = Charset.forName("UTF-8");
            byte[] body = bodyString.getBytes(CHARSET);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(CHARSET), "HmacSHA256");
            hmac.init(key);
            byte[] unencodedResult = hmac.doFinal(body);
            String expectedSignature = Base64.encodeBase64String(unencodedResult);
            return expectedSignature;
        } catch (NoSuchAlgorithmException e) {
            log.error(" Error on generateSignature : [{}]", e.getMessage());
        } catch (InvalidKeyException e) {
            log.error(" Error on generateSignature : [{}]", e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getWebhookSecretKey(String webhooksKeyId) throws BusinessException {
        try {
         String query = "select pg.webhooksSecretKey from PaymentGateway pg where pg.webhooksKeyId=:webhooksKeyId";
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("webhooksKeyId", webhooksKeyId);
            Object result = paymentGatewayService.executeSelectQuery(query, queryParams);
            return ((List<String>)result).get(0);
         } catch (Exception e) {
            log.error("Error on getWebhookSecretKey for webhooksKeyId = " + webhooksKeyId, e);
          	throw new BusinessException(String.format("Error on getWebhookSecretKey for webhooksKeyId = [%s]", webhooksKeyId));
        }     
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> safe(Collection<T> collection) {
        return collection == null ? Collections.EMPTY_LIST : collection;
    }
  
    public Object executeQuery(String query, Map<String, Object> params) {
        Query q = paymentService.getEntityManager().createQuery(query);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return q.executeUpdate();
    }
    private AccountOperation getMatchedInvoiceOperation(AccountOperation ao) {
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            AccountOperation matchedAO=null;
            MatchingCode matchingCode=ao.getMatchingAmounts()!=null && !ao.getMatchingAmounts().isEmpty()?ao.getMatchingAmounts().get(0).getMatchingCode():null;
            if(matchingCode!=null) {
                for(MatchingAmount matching:matchingCode.getMatchingAmounts()) {
                    matchedAO=matching.getAccountOperation();
                    if (matchedAO.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel()) ) {
                        return matchedAO;
                    }
                }
            }

        }
        return null;
    }
    enum ReportTransactionType {
        INV_STD("I"), PAY_DDT("P"), COM_PSP("OCC");
        String label;

        ReportTransactionType(String label) {
            this.label = label;
        }

        public static ReportTransactionType fromValue(String value) {
            return Arrays.stream(ReportTransactionType.values())
                    .filter(reportTransactionType -> reportTransactionType.label.equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("no type for the given value " + value));
        }

        public String getLabel() {
            return label;
        }
    }
}
