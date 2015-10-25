package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.tmf.dsmapi.catalog.resource.TimeRange;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class RelatedParty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8459350471434150398L;
	private String role;
	private String id;
	private String href;
	private String name;
	private TimeRange validFor;
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
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
	public TimeRange getValidFor() {
		return validFor;
	}
	public void setValidFor(TimeRange validFor) {
		this.validFor = validFor;
	}
	
}
