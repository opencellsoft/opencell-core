package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlansDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<DiscountPlanDto> discountPlan;

	public List<DiscountPlanDto> getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(List<DiscountPlanDto> discountPlan) {
		this.discountPlan = discountPlan;
	}

	@Override
	public String toString() {
		return "DiscountPlansDto [discountPlan=" + discountPlan + "]";
	}
}
