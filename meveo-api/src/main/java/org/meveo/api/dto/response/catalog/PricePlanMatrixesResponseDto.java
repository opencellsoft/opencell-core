package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.PricePlansDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "PricePlanMatrixesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixesResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7527987531474820250L;

	private PricePlansDto pricePlanMatrixes;

	public PricePlansDto getPricePlanMatrixes() {
		if (pricePlanMatrixes == null) {
			pricePlanMatrixes = new PricePlansDto();
		}
		return pricePlanMatrixes;
	}

	public void setPricePlanMatrixes(PricePlansDto pricePlanMatrixes) {
		this.pricePlanMatrixes = pricePlanMatrixes;
	}

	@Override
	public String toString() {
		return "PricePlanMatrixesResponseDto [pricePlanMatrixes=" + pricePlanMatrixes + ", getActionStatus()="
				+ getActionStatus() + "]";
	}

}
