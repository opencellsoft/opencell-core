package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetDiscountPlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDiscountPlanResponse extends BaseResponse {

	private static final long serialVersionUID = 5949081653446113338L;

	private DiscountPlanDto discountPlan;

	public DiscountPlanDto getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlanDto discountPlan) {
		this.discountPlan = discountPlan;
	}

	@Override
	public String toString() {
		return "GetDiscountPlanResponse [discountPlan=" + discountPlan
				+ ", getActionStatus()=" + getActionStatus() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
