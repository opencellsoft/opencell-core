package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DiscountPlanItemResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlanItemResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3515060888372691612L;

    /** The discount plan item. */
    private DiscountPlanItemDto discountPlanItem;

    /**
     * Gets the discount plan item.
     *
     * @return the discount plan item
     */
    public DiscountPlanItemDto getDiscountPlanItem() {
        return discountPlanItem;
    }

    /**
     * Sets the discount plan item.
     *
     * @param discountPlanItem the new discount plan item
     */
    public void setDiscountPlanItem(DiscountPlanItemDto discountPlanItem) {
        this.discountPlanItem = discountPlanItem;
    }

}
