package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "TipPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class TipPaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "tipPaymentMethods")
    @XmlElement(name = "tipPaymentMethod")
    private List<TipPaymentMethodDto> tipPaymentMethods = new ArrayList<TipPaymentMethodDto>();

	public List<TipPaymentMethodDto> getTipPaymentMethods() {
		return tipPaymentMethods;
	}

	public void setTipPaymentMethods(List<TipPaymentMethodDto> tipPaymentMethods) {
		this.tipPaymentMethods = tipPaymentMethods;
	}

}