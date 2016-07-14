package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OfferProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductTemplateDto implements Serializable {

	private static final long serialVersionUID = 1231940046600480645L;

	private OfferTemplateDto offerTemplate;

	private ProductTemplateDto productTemplate;

	private Boolean mandatory;

	public OfferTemplateDto getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplateDto offerTemplate) {
		this.offerTemplate = offerTemplate;
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
