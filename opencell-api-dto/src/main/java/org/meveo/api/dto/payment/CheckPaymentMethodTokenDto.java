package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CheckPaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckPaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private CheckPaymentMethodDto checkPaymentMethod;

    public CheckPaymentMethodTokenDto() {
    }

    public CheckPaymentMethodDto getCheckPaymentMethod() {
        return checkPaymentMethod;
    }

    public void setCheckPaymentMethod(CheckPaymentMethodDto checkPaymentMethod) {
        this.checkPaymentMethod = checkPaymentMethod;
    }

    @Override
    public String toString() {
        return "CheckPaymentMethodTokenDto [checkPaymentMethod=" + checkPaymentMethod + "]";
    }
}