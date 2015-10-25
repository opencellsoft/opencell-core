package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonSerialize(include=Inclusion.NON_NULL)
public class ProductCharacteristic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3077606313766009585L;
	private String name;
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
