package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CheckPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckPaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "checkPaymentMethods")
    @XmlElement(name = "checkPaymentMethod")
    private List<CheckPaymentMethodDto> checkPaymentMethods = new ArrayList<CheckPaymentMethodDto>();

	public List<CheckPaymentMethodDto> getCheckPaymentMethods() {
		return checkPaymentMethods;
	}

	public void setCheckPaymentMethods(List<CheckPaymentMethodDto> checkPaymentMethods) {
		this.checkPaymentMethods = checkPaymentMethods;
	}

}