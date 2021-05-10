package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GroupedAttribute {

	private Attribute attribute;
	
	public GroupedAttribute(org.meveo.model.cpq.Attribute attribute) {
		this.attribute = new Attribute(attribute);
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
	
	
	
}
