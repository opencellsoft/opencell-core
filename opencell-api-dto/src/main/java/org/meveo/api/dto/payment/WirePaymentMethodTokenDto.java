package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "WirePaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class WirePaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private WirePaymentMethodDto wirePaymentMethod;

    public WirePaymentMethodTokenDto() {
    }

    public WirePaymentMethodDto getWirePaymentMethod() {
        return wirePaymentMethod;
    }

    public void setWirePaymentMethod(WirePaymentMethodDto wirePaymentMethod) {
        this.wirePaymentMethod = wirePaymentMethod;
    }

    @Override
    public String toString() {
        return "WirePaymentMethodTokenDto [wirePaymentMethod=" + wirePaymentMethod + "]";
    }
}