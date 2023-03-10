
package org.meveo.service.script.demo;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author anasseh
 *
 *         Script executed after a payment callBack (inbound request), if the payment is rejected , the invoice matching is removed and a new is created with a the new reject payment account operation
 *
 */

public class PaymentCallBackScript extends Script {

    private static final Logger log = LoggerFactory.getLogger(PaymentCallBackScript.class);
    private PaymentService paymentService = (PaymentService) getServiceInterface("PaymentService");

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        log.debug("EXECUTE  methodContext {} ", methodContext);
        @SuppressWarnings("unchecked")
        Map<String, String> params = (Map<String, String>) methodContext.get("params");
        log.info("params:" + params);
        if (params.get("STATUS") == null) {
            throw new BusinessException("Parameter STATUS is required");
        }
        if (params.get("PAYID") == null) {
            throw new BusinessException("Parameter PAYID is required");
        }
        PaymentStatusEnum paymentStatus = PaymentStatusEnum.ACCEPTED;
        if (!"9".equals(params.get("STATUS"))) {
            paymentStatus = PaymentStatusEnum.REJECTED;
        }
        paymentService.paymentCallback(params.get("PAYID"), paymentStatus, params.get("NCERROR"), params.get("NCERROR"));
    }

}