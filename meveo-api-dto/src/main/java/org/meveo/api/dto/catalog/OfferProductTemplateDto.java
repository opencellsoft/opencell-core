package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferProductTemplate;

@XmlRootElement(name = "OfferProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductTemplateDto implements Serializable {

	private static final long serialVersionUID = 1231940046600480645L;

	private ProductTemplateDto productTemplate;

	private Boolean mandatory;
	
	public OfferProductTemplateDto() {
	}
	
	public OfferProductTemplateDto(OfferProductTemplate offerProductTemplate) {
		this.mandatory = offerProductTemplate.isMandatory();
		this.productTemplate = new ProductTemplateDto(offerProductTemplate.getProductTemplate());
	}

	public ProductTemplateDto getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplateDto productTemplate) {
		this.productTemplate = productTemplate;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

}
