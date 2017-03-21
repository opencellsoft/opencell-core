package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BundleProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleProductTemplateDto implements Serializable {

	private static final long serialVersionUID = 4914322874611290121L;

	private ProductTemplateDto productTemplate;

	private int quantity;

	public ProductTemplateDto getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplateDto productTemplate) {
		this.productTemplate = productTemplate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
