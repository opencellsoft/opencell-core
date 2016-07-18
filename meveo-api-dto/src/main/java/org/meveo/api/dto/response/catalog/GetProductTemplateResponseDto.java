package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetProductTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = -2801794466203329264L;

	private ProductTemplateDto productTemplateDto;

	public ProductTemplateDto getProductTemplateDto() {
		return productTemplateDto;
	}

	public void setProductTemplateDto(ProductTemplateDto productTemplateDto) {
		this.productTemplateDto = productTemplateDto;
	}

	@Override
	public String toString() {
		return "GetProductTemplateResponseDto [productTemplate=" + productTemplateDto + "]";
	}
}
