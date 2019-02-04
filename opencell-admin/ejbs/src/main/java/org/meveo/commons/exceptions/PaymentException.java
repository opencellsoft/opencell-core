package org.meveo.commons.exceptions;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.PaymentErrorEnum;

public class PaymentException extends BusinessException {
    private static final long serialVersionUID = 1L;
    private String code;
    
    public PaymentException(PaymentErrorEnum errorCode, String message) {
        super(message);
        this.code = errorCode.toString();
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
}
