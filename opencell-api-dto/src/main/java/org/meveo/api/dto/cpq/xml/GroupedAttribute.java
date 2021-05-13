package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.cpq.QuoteAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class GroupedAttribute {

	private QuoteAttribute attribute;
	
	public GroupedAttribute(org.meveo.model.cpq.QuoteAttribute attribute) { 
      this.attribute=attribute;
	}

	public QuoteAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(QuoteAttribute attribute) {
		this.attribute = attribute;
	}
 
	
	
	
	
}
