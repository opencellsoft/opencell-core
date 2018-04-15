package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class DiscountPlansDto.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlansDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The discount plan. */
    private List<DiscountPlanDto> discountPlan;

    /**
     * Gets the discount plan.
     *
     * @return the discount plan
     */
    public List<DiscountPlanDto> getDiscountPlan() {
        return discountPlan;
    }

    /**
     * Sets the discount plan.
     *
     * @param discountPlan the new discount plan
     */
    public void setDiscountPlan(List<DiscountPlanDto> discountPlan) {
        this.discountPlan = discountPlan;
    }


    @Override
    public String toString() {
        return "DiscountPlansDto [discountPlan=" + discountPlan + "]";
    }
}