package org.meveo.admin.web.filter.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Page {
	
	@XmlAttribute(name="view-id")
	private String viewId;
	@XmlElement(name="constraint")
	private List<String> constraints;
	@XmlElement(name="param")
	private List<Param> parameters;
	
	public Page() {
		constraints = new ArrayList<>();
		parameters = new ArrayList<>();
	}
	
	public String getViewId() {
		return viewId;
	}
	
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	
	public List<String> getConstraints() {
		return constraints;
	}
	
	public void setConstraints(List<String> constraints) {
		this.constraints = constraints;
	}


	public List<Param> getParameters() {
		return parameters;
	}

	public void setParameters(List<Param> parameters) {
		this.parameters = parameters;
	}
}
