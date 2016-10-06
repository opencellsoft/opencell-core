package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetProductChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductChargeTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6452175086333220603L;

	private ProductChargeTemplateDto productChargeTemplate;

	public ProductChargeTemplateDto getProductChargeTemplate() {
		return productChargeTemplate;
	}
	
	public void setProductChargeTemplate(ProductChargeTemplateDto productChargeTemplate) {
		this.productChargeTemplate = productChargeTemplate;
	}

	@Override
	public String toString() {
		return "GetProductChargeTemplateResponseDto [getProductChargeTemplate=" + productChargeTemplate + ", toString()="
				+ super.toString() + "]";
	}

}
