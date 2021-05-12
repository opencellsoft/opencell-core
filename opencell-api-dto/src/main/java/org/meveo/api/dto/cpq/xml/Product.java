package org.meveo.api.dto.cpq.xml;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.quote.QuoteProduct;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
	
	@XmlElement
	private String productLine;
	@XmlElement
	private BigDecimal quantity;  
	private List<Attribute> attributes;
	private CustomFieldsDto customFields;
	
	
	
	public  Product(QuoteProduct quoteProduct , CustomFieldsDto customFields) {
		super();
		if(quoteProduct.getProductVersion().getProduct().getProductLine() != null) {
			this.productLine = quoteProduct.getProductVersion().getProduct().getProductLine().getCode();
		}
	    this.quantity = quoteProduct.getQuantity();
		this.customFields = customFields;
	}
	/**
	 * @return the productLine
	 */
	public String getProductLine() {
		return productLine;
	}
	/**
	 * @param productLine the productLine to set
	 */
	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}
	
 
	
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
	
	
	
	
	
}
