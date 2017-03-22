package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BillingCycleDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetBillingCycleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingCycleResponse extends BaseResponse {

	private static final long serialVersionUID = -1125948385137327401L;

	private BillingCycleDto billingCycle;

	public BillingCycleDto getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(BillingCycleDto billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Override
	public String toString() {
		return "GetBillingCycleResponse [billingCycle=" + billingCycle + ", toString()=" + super.toString() + "]";
	}

}
