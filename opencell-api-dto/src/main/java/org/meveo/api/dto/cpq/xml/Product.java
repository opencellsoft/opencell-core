package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.cpq.GroupedAttributes;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
	
	@XmlElement
	private String productLine;
	@XmlElement
	private int quantity;
	private GroupedAttribute groupedAttribute;
	private Attribute attribute;
	private CustomFieldDto customField;
	
	
	
	protected Product(org.meveo.model.cpq.Product product, GroupedAttributes groupedAttributes, org.meveo.model.cpq.Attribute attribute, CustomFieldDto customField) {
		super();
		if(product.getProductLine() != null) {
			this.productLine = product.getProductLine().getCode();
		}
//		this.quantity = product.get;
//		this.groupedAttribute = groupedAttribute;
		this.attribute = new Attribute(attribute);
		this.customField = customField;
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
	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the groupedAttribute
	 */
	public GroupedAttribute getGroupedAttribute() {
		return groupedAttribute;
	}
	/**
	 * @param groupedAttribute the groupedAttribute to set
	 */
	public void setGroupedAttribute(GroupedAttribute groupedAttribute) {
		this.groupedAttribute = groupedAttribute;
	}
	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return the customField
	 */
	public CustomFieldDto getCustomField() {
		return customField;
	}
	/**
	 * @param customField the customField to set
	 */
	public void setCustomField(CustomFieldDto customField) {
		this.customField = customField;
	}
	
	
	
}
