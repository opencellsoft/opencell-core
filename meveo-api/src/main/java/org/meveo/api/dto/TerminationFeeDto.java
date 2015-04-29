package org.meveo.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Nov 4, 2013
 **/
@XmlRootElement(name = "TerminationFee")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationFeeDto implements Serializable {

	private static final long serialVersionUID = -30747849216793313L;

	private String currencyCode;
	private Date startDate;
	private Date endDate;
	private BigDecimal price;
	private BigDecimal recommendedPrice;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getRecommendedPrice() {
		return recommendedPrice;
	}

	public void setRecommendedPrice(BigDecimal recommendedPrice) {
		this.recommendedPrice = recommendedPrice;
	}

}
