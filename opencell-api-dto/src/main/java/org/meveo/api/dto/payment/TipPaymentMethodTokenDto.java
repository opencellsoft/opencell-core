package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "TipPaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class TipPaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private TipPaymentMethodDto tipPaymentMethod;

    public TipPaymentMethodTokenDto() {
    }

    public TipPaymentMethodDto getTipPaymentMethod() {
        return tipPaymentMethod;
    }

    public void setTipPaymentMethod(TipPaymentMethodDto tipPaymentMethod) {
        this.tipPaymentMethod = tipPaymentMethod;
    }

    @Override
    public String toString() {
        return "TipPaymentMethodTokenDto [tipPaymentMethod=" + tipPaymentMethod + "]";
    }
}