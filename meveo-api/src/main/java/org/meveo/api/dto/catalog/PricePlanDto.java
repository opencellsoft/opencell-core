package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.PricePlanMatrix;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "PricePlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanDto implements Serializable {

	private static final long serialVersionUID = -9089693491690592072L;

	@XmlAttribute(required = true)
	private String code;

	@XmlElement(required = true)
	private String eventCode;

	private String seller;
	private String country;
	private String currency;
	private BigDecimal minQuantity;
	private BigDecimal maxQuantity;
	private String offerTemplate;

	private Date startSubscriptionDate;
	private Date endSubscriptionDate;
	private Date startRatingDate;
	private Date endRatingDate;

	private Long minSubscriptionAgeInMonth;
	private Long maxSubscriptionAgeInMonth;

	private BigDecimal amountWithoutTax;
	private BigDecimal amountWithTax;

	private int priority;

	private String criteria1;
	private String criteria2;
	private String criteria3;

	private String validityCalendarCode;
	
	public PricePlanDto() {

	}

	public PricePlanDto(PricePlanMatrix e) {
		eventCode = e.getEventCode();
		if (e.getSeller() != null) {
			seller = e.getSeller().getCode();
		}
		if (e.getTradingCountry() != null) {
			country = e.getTradingCountry().getCountryCode();
		}
		if (e.getTradingCurrency() != null) {
			currency = e.getTradingCurrency().getCurrencyCode();
		}
		if (e.getOfferTemplate() != null) {
			offerTemplate = e.getOfferTemplate().getCode();
		}
		minQuantity = e.getMinQuantity();
		maxQuantity = e.getMaxQuantity();
		startSubscriptionDate = e.getStartRatingDate();
		endSubscriptionDate = e.getEndSubscriptionDate();
		startRatingDate = e.getStartRatingDate();
		endRatingDate = e.getEndRatingDate();
		minSubscriptionAgeInMonth = e.getMinSubscriptionAgeInMonth();
		maxSubscriptionAgeInMonth = e.getMaxSubscriptionAgeInMonth();
		amountWithoutTax = e.getAmountWithoutTax();
		amountWithTax = e.getAmountWithTax();
		priority = e.getPriority();
		criteria1 = e.getCriteria1Value();
		criteria2 = e.getCriteria2Value();
		criteria3 = e.getCriteria3Value();
        if (e.getValidityCalendar() != null) {
            validityCalendarCode = e.getValidityCalendar().getCode();
        }
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;
	}

	public BigDecimal getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(BigDecimal maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public String getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(String offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public Date getStartSubscriptionDate() {
		return startSubscriptionDate;
	}

	public void setStartSubscriptionDate(Date startSubscriptionDate) {
		this.startSubscriptionDate = startSubscriptionDate;
	}

	public Date getEndSubscriptionDate() {
		return endSubscriptionDate;
	}

	public void setEndSubscriptionDate(Date endSubscriptionDate) {
		this.endSubscriptionDate = endSubscriptionDate;
	}

	public Date getStartRatingDate() {
		return startRatingDate;
	}

	public void setStartRatingDate(Date startRatingDate) {
		this.startRatingDate = startRatingDate;
	}

	public Date getEndRatingDate() {
		return endRatingDate;
	}

	public void setEndRatingDate(Date endRatingDate) {
		this.endRatingDate = endRatingDate;
	}

	public Long getMinSubscriptionAgeInMonth() {
		return minSubscriptionAgeInMonth;
	}

	public void setMinSubscriptionAgeInMonth(Long minSubscriptionAgeInMonth) {
		this.minSubscriptionAgeInMonth = minSubscriptionAgeInMonth;
	}

	public Long getMaxSubscriptionAgeInMonth() {
		return maxSubscriptionAgeInMonth;
	}

	public void setMaxSubscriptionAgeInMonth(Long maxSubscriptionAgeInMonth) {
		this.maxSubscriptionAgeInMonth = maxSubscriptionAgeInMonth;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCriteria1() {
		return criteria1;
	}

	public void setCriteria1(String criteria1) {
		this.criteria1 = criteria1;
	}

	public String getCriteria2() {
		return criteria2;
	}

	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}

	public String getCriteria3() {
		return criteria3;
	}

	public void setCriteria3(String criteria3) {
		this.criteria3 = criteria3;
	}

	@Override
	public String toString() {
		return "PricePlanDto [code=" + code + ", eventCode=" + eventCode + ", seller=" + seller + ", country="
				+ country + ", currency=" + currency + ", minQuantity=" + minQuantity + ", maxQuantity=" + maxQuantity
				+ ", offerTemplate=" + offerTemplate + ", startSubscriptionDate=" + startSubscriptionDate
				+ ", endSubscriptionDate=" + endSubscriptionDate + ", startRatingDate=" + startRatingDate
				+ ", endRatingDate=" + endRatingDate + ", minSubscriptionAgeInMonth=" + minSubscriptionAgeInMonth
				+ ", maxSubscriptionAgeInMonth=" + maxSubscriptionAgeInMonth + ", amountWithoutTax=" + amountWithoutTax
				+ ", amountWithTax=" + amountWithTax + ", priority=" + priority + ", criteria1=" + criteria1
				+ ", criteria2=" + criteria2 + ", criteria3=" + criteria3 + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public String getValidityCalendarCode() {
        return validityCalendarCode;
    }

    public void setValidityCalendarCode(String validityCalendarCode) {
        this.validityCalendarCode = validityCalendarCode;
    }

}
