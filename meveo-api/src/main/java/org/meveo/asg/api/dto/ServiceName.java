package org.meveo.asg.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 * @since Dec 10, 2013
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceName", propOrder = { "name" })
public class ServiceName {
	@XmlElement(name = "Name")
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
