package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOfferTemplateCategoryResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private OfferTemplateCategoryDto offerTemplateCategory;

	public OfferTemplateCategoryDto getOfferTemplateCategory() {
		return offerTemplateCategory;
	}

	public void setOfferTemplateCategory(OfferTemplateCategoryDto offerTemplateCategory) {
		this.offerTemplateCategory = offerTemplateCategory;
	}

	@Override
	public String toString() {
		return "GetOfferTemplateCategoryResponseDto [offerTemplateCategory=" + offerTemplateCategory + "]";
	}

}
