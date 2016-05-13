package org.meveo.admin.web.filter.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="page-access")
@XmlAccessorType(XmlAccessType.FIELD)
public class PageAccess {
	
	@XmlAttribute(name="path")
	private String path;
	
	@XmlElement(name="page")
	private List<Page> pages;
	
	public PageAccess() {
		pages = Collections.synchronizedList(new ArrayList<Page>());
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
}
