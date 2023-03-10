package org.meveo.service.script.demo;

import java.io.StringReader;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.script.payment.PaymentScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author anasseh
 */
public class CustomGatewayPaymentScript extends PaymentScript {
    private static final Logger log = LoggerFactory.getLogger(CustomGatewayPaymentScript.class);

    public void createCardToken(Map<String, Object> methodContext) throws BusinessException {
        methodContext.put(PaymentScript.RESULT_TOKEN, "7ced0000-3ab2-000d-6a50-08d4b8a6" + (int) (Math.random() * 1000 + 1));
    }

    public void doPaymentToken(Map<String, Object> methodContext) throws BusinessException {
        doIt(methodContext, "PAL");
    }

    public void doPaymentCard(Map<String, Object> methodContext) throws BusinessException {
        doIt(methodContext, "PAL_CARD");
    }

    public void doRefundToken(Map<String, Object> methodContext) throws BusinessException {
        doIt(methodContext, "RFD");
    }

    public void doRefundCard(Map<String, Object> methodContext) throws BusinessException {
        doIt(methodContext, "RFD_CARD");
    }

    public void doRefundSepa(Map<String, Object> methodContext) throws BusinessException {
        doIt(methodContext, "RFD_SEPA");
    }

    private void doIt(Map<String, Object> methodContext, String operationType) throws BusinessException {
        try {
            log.debug("EXECUTE  methodContext {} ", methodContext);
            Long amountCts = (Long) methodContext.get(PaymentScript.CONTEXT_AMOUNT_CTS);
            if (amountCts == null) {
                throw new BusinessException("amountCts is null");
            }

            if (!"RFD_SEPA".equals(operationType)) {
                CardPaymentMethod paymentToken = (CardPaymentMethod) methodContext.get(PaymentScript.CONTEXT_TOKEN);
                if (paymentToken == null && (operationType.equals("PAL") || operationType.equals("RFD"))) {
                    throw new BusinessException("paymentMethod is null");
                }
            }

            // DEMO : get tokenId or card infos from context
            String body = "{";
            body += " \"PSPID\" : \"PSPID\" ,";
            body += " \"USERID\" : \"USERID\" ,";
            body += " \"PSWD\" : \"PSWD\" ,";
            body += " \"REFKIND\" :  \"PSPID\" ,";
            body += " \"REFID\" : \"PSPID\" ,";
            body += " \"OPERATION\" : \"" + operationType + "\" ,";
            body += " \"AMOUNT\" : \"" + amountCts.longValue() + "\" ,";
            body += " \"SHASIGN\" : \"SHASIGN\" ,";
            body += " }";

            String paymentResponse = "<root>";
            paymentResponse += "<STATUS>92</STATUS>";
            paymentResponse += "<PAYID>" + ((int) (Math.random() * 100000 + 1)) + "</PAYID>";
            paymentResponse += "<TRANSACTIONID>92</TRANSACTIONID>";
            paymentResponse += "<CRMTOKEN>clientSide</CRMTOKEN>";
            paymentResponse += "<BRAND>VISA</BRAND>";
            paymentResponse += "<ACCEPTENCE>bankRef</ACCEPTENCE>";
            paymentResponse += "</root>";
            methodContext.put(PaymentScript.RESULT_PAYMENT_STATUS, PaymentStatusEnum.PENDING);
            methodContext.put(PaymentScript.RESULT_PAYMENT_ID, getValue(paymentResponse, "/root/PAYID"));
            methodContext.put(PaymentScript.RESULT_TRANSACTION_ID, getValue(paymentResponse, "/root/TRANSACTIONID"));
            methodContext.put(PaymentScript.RESULT_CODE_CLIENT_SIDE, getValue(paymentResponse, "/root/CRMTOKEN"));
            methodContext.put(PaymentScript.RESULT_PAYMENT_BRAND, getValue(paymentResponse, "/root/BRAND"));
            methodContext.put(PaymentScript.RESULT_BANK_REFERENCE, getValue(paymentResponse, "/root/ACCEPTENCE"));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private String getValue(String sourceXML, String expression) throws XPathExpressionException {
        InputSource iSource = new InputSource(new StringReader(sourceXML));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        return xpath.evaluate(expression, iSource);
    }
}