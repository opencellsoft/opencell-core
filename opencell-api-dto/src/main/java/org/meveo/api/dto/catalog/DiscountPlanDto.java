package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.DiscountPlan;

@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto extends EnableBusinessDto {

    private static final long serialVersionUID = 1L;

    public DiscountPlanDto() {
        super();
    }

    public DiscountPlanDto(DiscountPlan discountPlan) {
        super(discountPlan);
    }

    @Override
    public String toString() {
        return "DiscountPlanDto [code=" + getCode() + ", description=" + getDescription() + "]";
    }
}