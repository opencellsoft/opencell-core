package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "DiscountPlanItemsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemsResponseDto extends BaseResponse {
	
	private static final long serialVersionUID = -4771102434084711881L;
	
	@XmlElementWrapper(name="discountPlanItems")
	@XmlElement(name="discountPlanItem")
	private List<DiscountPlanItemDto> discountPlanItems;

	public List<DiscountPlanItemDto> getDiscountPlanItems() {
		return discountPlanItems;
	}

	public void setDiscountPlanItems(List<DiscountPlanItemDto> discountPlanItems) {
		this.discountPlanItems = discountPlanItems;
	}
	
	

}
