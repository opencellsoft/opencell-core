package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "PaymentMethodTokens")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "paymentMethod")
    private List<PaymentMethodDto> paymentMethods = new ArrayList<PaymentMethodDto>();

	public List<PaymentMethodDto> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

  
}