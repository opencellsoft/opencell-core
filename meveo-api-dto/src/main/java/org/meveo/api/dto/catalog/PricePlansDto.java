package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "PricePlans")
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlansDto implements Serializable {

	private static final long serialVersionUID = 4354099345909112263L;

	private List<PricePlanDto> pricePlanMatrix;

	public List<PricePlanDto> getPricePlanMatrix() {
		return pricePlanMatrix;
	}

	public void setPricePlanMatrix(List<PricePlanDto> pricePlanMatrix) {
		this.pricePlanMatrix = pricePlanMatrix;
	}

	@Override
	public String toString() {
		return "PricePlansDto [pricePlanMatrix=" + pricePlanMatrix + "]";
	}

}
