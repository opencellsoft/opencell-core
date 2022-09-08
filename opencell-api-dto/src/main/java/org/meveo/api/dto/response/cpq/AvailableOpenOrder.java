package org.meveo.api.dto.response.cpq;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "QuoteOfferDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailableOpenOrder extends BusinessEntityDto {

	private static final long serialVersionUID = 5719570552966744772L;

	private Long openOrderid;
	
	@Schema(description = "Open Order number")
	private String openOrderNumber;

	@Schema
	private Date startDate;

	private String externalReference;

	private List<Long> products;

	private List<Long> articles;

	public Long getOpenOrderid() {
		return openOrderid;
	}

	public void setOpenOrderid(Long openOrderid) {
		this.openOrderid = openOrderid;
	}

	public String getOpenOrderNumber() {
		return openOrderNumber;
	}

	public void setOpenOrderNumber(String openOrderNumber) {
		this.openOrderNumber = openOrderNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public List<Long> getProducts() {
		return products;
	}

	public void setProducts(List<Long> products) {
		this.products = products;
	}

	public List<Long> getArticles() {
		return articles;
	}

	public void setArticles(List<Long> articles) {
		this.articles = articles;
	}
	
}
