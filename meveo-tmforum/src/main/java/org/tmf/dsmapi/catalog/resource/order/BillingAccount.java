package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonSerialize(include=Inclusion.NON_NULL)
public class BillingAccount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1538091079215602417L;
	private String id;
	private String href;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
