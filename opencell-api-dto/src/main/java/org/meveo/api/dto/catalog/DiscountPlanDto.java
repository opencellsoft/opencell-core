package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.DiscountPlan;

/**
 * The Class DiscountPlanDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new DiscountPlanDto
     */
    public DiscountPlanDto() {
        super();
    }

    /**
     * Convert DiscountPlan JPA entity to DTO
     * 
     * @param discountPlan Entity to convert
     */
    public DiscountPlanDto(DiscountPlan discountPlan) {
        super(discountPlan);
    }

    @Override
    public String toString() {
        return "DiscountPlanDto [code=" + getCode() + ", description=" + getDescription() + "]";
    }
}