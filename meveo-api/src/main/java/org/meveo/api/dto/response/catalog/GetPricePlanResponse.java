package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetPricePlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPricePlanResponse extends BaseResponse {

	private static final long serialVersionUID = 9135612368906230878L;

	private PricePlanDto pricePlan;

	public PricePlanDto getPricePlan() {
		return pricePlan;
	}

	public void setPricePlan(PricePlanDto pricePlan) {
		this.pricePlan = pricePlan;
	}

	@Override
	public String toString() {
		return "GetPricePlanResponse [pricePlan=" + pricePlan + ", toString()=" + super.toString() + "]";
	}

}