package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author TARIK FA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetCommercialOrderDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCommercialOrderDtoResponse extends BaseResponse{

	private CommercialOrderDto commercialOrderDto;

	/**
	 * @return the commercialOrderDto
	 */
	public CommercialOrderDto getCommercialOrderDto() {
		return commercialOrderDto;
	}

	/**
	 * @param commercialOrderDto the commercialOrderDto to set
	 */
	public void setCommercialOrderDto(CommercialOrderDto commercialOrderDto) {
		this.commercialOrderDto = commercialOrderDto;
	}
	

	
	
	
}
