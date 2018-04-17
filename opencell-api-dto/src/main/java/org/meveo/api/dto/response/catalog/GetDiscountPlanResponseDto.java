package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetDiscountPlanResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetDiscountPlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDiscountPlanResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The discount plan dto. */
    private DiscountPlanDto discountPlanDto;

    /**
     * Gets the discount plan dto.
     *
     * @return the discount plan dto
     */
    public DiscountPlanDto getDiscountPlanDto() {
        return discountPlanDto;
    }

    /**
     * Sets the discount plan dto.
     *
     * @param discountPlanDto the new discount plan dto
     */
    public void setDiscountPlanDto(DiscountPlanDto discountPlanDto) {
        this.discountPlanDto = discountPlanDto;
    }

    @Override
    public String toString() {
        return "GetDiscountPlanResponseDto [discountPlanDto=" + discountPlanDto + "]";
    }
}