package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.DiscountPlanMatrix;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto implements Serializable {

	private static final long serialVersionUID = 3509362441219405146L;

	@XmlAttribute(required = true)
	private String eventCode;

	private Long id;
	private String offerTemplate;
	private Date startSubscriptionDate;
	private Date endSubscriptionDate;
	private String seller;
	private Integer nbPeriod;
	private BigDecimal percent;

	public DiscountPlanDto() {

	}

	public DiscountPlanDto(DiscountPlanMatrix e) {
		id = e.getId();
		eventCode = e.getEventCode();
		offerTemplate = e.getOfferTemplate().getCode();
		startSubscriptionDate = e.getStartSubscriptionDate();
		endSubscriptionDate = e.getEndSubscriptionDate();
		seller = e.getSeller().getCode();
		nbPeriod = e.getNbPeriod();
		percent = e.getPercent();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
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

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public Integer getNbPeriod() {
		return nbPeriod;
	}

	public void setNbPeriod(Integer nbPeriod) {
		this.nbPeriod = nbPeriod;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "DiscountPlanDto [id=" + id + ", eventCode=" + eventCode
				+ ", offerTemplate=" + offerTemplate
				+ ", startSubscriptionDate=" + startSubscriptionDate
				+ ", endSubscriptionDate=" + endSubscriptionDate + ", seller="
				+ seller + ", nbPeriod=" + nbPeriod + ", percent=" + percent
				+ "]";
	}

}
