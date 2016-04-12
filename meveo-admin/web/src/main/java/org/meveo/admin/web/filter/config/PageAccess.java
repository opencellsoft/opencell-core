package org.meveo.admin.web.filter.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="page-access")
@XmlAccessorType(XmlAccessType.FIELD)
public class PageAccess {
	
	@XmlElement(name="page")
	private List<Page> pages;
	
	public PageAccess() {
		pages = new ArrayList<>();
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
}
