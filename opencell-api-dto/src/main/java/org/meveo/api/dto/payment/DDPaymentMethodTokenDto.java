package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "DDPaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDPaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private DDPaymentMethodDto ddPaymentMethod;

    public DDPaymentMethodTokenDto() {
    }

    public DDPaymentMethodDto getDDPaymentMethod() {
        return ddPaymentMethod;
    }

    public void setDDPaymentMethod(DDPaymentMethodDto ddPaymentMethod) {
        this.ddPaymentMethod = ddPaymentMethod;
    }

    @Override
    public String toString() {
        return "DDPaymentMethodTokenDto [ddPaymentMethod=" + ddPaymentMethod + "]";
    }
}