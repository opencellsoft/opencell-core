package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;

@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto extends BusinessDto {

	private static final long serialVersionUID = 1L;
		
	@Override
	public String toString() {
		return "DiscountPlanDto [code=" + getCode() + ", description=" + getDescription()
				+ "]";
	}

}
