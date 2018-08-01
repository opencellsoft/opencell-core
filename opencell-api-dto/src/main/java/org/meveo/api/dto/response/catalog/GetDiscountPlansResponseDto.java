package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlansDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetDiscountPlansResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetDiscountPlansResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDiscountPlansResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The discount plan. */
    private DiscountPlansDto discountPlan;

    /**
     * Gets the discount plan.
     *
     * @return the discount plan
     */
    public DiscountPlansDto getDiscountPlan() {
        return discountPlan;
    }

    /**
     * Sets the discount plan.
     *
     * @param discountPlan the new discount plan
     */
    public void setDiscountPlan(DiscountPlansDto discountPlan) {
        this.discountPlan = discountPlan;
    }

    @Override
    public String toString() {
        return "GetDiscountPlansResponseDto [discountPlan=" + discountPlan + "]";
    }
}