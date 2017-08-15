package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "DDPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDPaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "ddPaymentMethods")
    @XmlElement(name = "ddPaymentMethod")
    private List<DDPaymentMethodDto> ddPaymentMethods = new ArrayList<DDPaymentMethodDto>();

	public List<DDPaymentMethodDto> getDdPaymentMethods() {
		return ddPaymentMethods;
	}

	public void setDdPaymentMethods(List<DDPaymentMethodDto> ddPaymentMethods) {
		this.ddPaymentMethods = ddPaymentMethods;
	}

  
}