package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "DiscountPlanItemResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemResponseDto extends BaseResponse {

	private static final long serialVersionUID = 3515060888372691612L;

	private DiscountPlanItemDto discountPlanItem;

	public DiscountPlanItemDto getDiscountPlanItem() {
		return discountPlanItem;
	}

	public void setDiscountPlanItem(DiscountPlanItemDto discountPlanItem) {
		this.discountPlanItem = discountPlanItem;
	}

}
