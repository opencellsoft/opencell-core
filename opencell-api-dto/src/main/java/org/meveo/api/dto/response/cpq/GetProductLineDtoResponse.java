package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.ProductDto;
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
