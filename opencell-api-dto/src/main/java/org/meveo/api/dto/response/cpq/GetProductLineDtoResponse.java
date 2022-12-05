package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductLineDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductLineDtoResponse extends BaseResponse{

	private ProductLineDto productLineDto;

	public GetProductLineDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}
	/**
	 * @return the productLineDto
	 */
	public ProductLineDto getProductLineDto() {
		return productLineDto;
	}

	/**
	 * @param productLineDto the productLineDto to set
	 */
	public void setProductLineDto(ProductLineDto productLineDto) {
		this.productLineDto = productLineDto;
	}

	
	
}
