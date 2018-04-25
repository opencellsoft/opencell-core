package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DiscountPlanItemsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlanItemsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4771102434084711881L;

    /** The discount plan items. */
    @XmlElementWrapper(name = "discountPlanItems")
    @XmlElement(name = "discountPlanItem")
    private List<DiscountPlanItemDto> discountPlanItems;

    /**
     * Gets the discount plan items.
     *
     * @return the discount plan items
     */
    public List<DiscountPlanItemDto> getDiscountPlanItems() {
        return discountPlanItems;
    }

    /**
     * Sets the discount plan items.
     *
     * @param discountPlanItems the new discount plan items
     */
    public void setDiscountPlanItems(List<DiscountPlanItemDto> discountPlanItems) {
        this.discountPlanItems = discountPlanItems;
    }

}
