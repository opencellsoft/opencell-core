package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "WirePaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class WirePaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "wirePaymentMethods")
    @XmlElement(name = "wirePaymentMethod")
    private List<WirePaymentMethodDto> wirePaymentMethods = new ArrayList<WirePaymentMethodDto>();

	public List<WirePaymentMethodDto> getWirePaymentMethods() {
		return wirePaymentMethods;
	}

	public void setWirePaymentMethods(List<WirePaymentMethodDto> wirePaymentMethods) {
		this.wirePaymentMethods = wirePaymentMethods;
	}

}