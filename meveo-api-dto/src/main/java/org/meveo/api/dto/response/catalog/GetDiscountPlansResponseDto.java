package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlansDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetDiscountPlansResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDiscountPlansResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private DiscountPlansDto discountPlan;

	public DiscountPlansDto getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlansDto discountPlan) {
		this.discountPlan = discountPlan;
	}

	@Override
	public String toString() {
		return "GetDiscountPlansResponseDto [discountPlan=" + discountPlan
				+ "]";
	}
}
