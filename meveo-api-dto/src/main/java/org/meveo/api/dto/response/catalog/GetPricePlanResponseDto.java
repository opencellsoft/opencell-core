package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetPricePlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPricePlanResponseDto extends BaseResponse {

	private static final long serialVersionUID = 9135612368906230878L;

	private PricePlanMatrixDto pricePlan;

	public PricePlanMatrixDto getPricePlan() {
		return pricePlan;
	}

	public void setPricePlan(PricePlanMatrixDto pricePlan) {
		this.pricePlan = pricePlan;
	}

	@Override
	public String toString() {
		return "GetPricePlanResponse [pricePlan=" + pricePlan + ", toString()=" + super.toString() + "]";
	}

}