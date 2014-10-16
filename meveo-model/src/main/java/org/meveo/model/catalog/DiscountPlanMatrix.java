/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.Seller;

@Entity
@Table(name = "CAT_DISCOUNT_PLAN_MATRIX", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"EVENT_CODE", "START_SUBSCRIPTION_DATE", "END_SUBSCRIPTION_DATE", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_MATRIX_SEQ")
public class DiscountPlanMatrix extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "BUSINESS_INTERMEDIARY_ID")
	private Integer businessIntermediaryId;

	@Column(name = "EVENT_CODE", length = 20, nullable = false)
	@Size(max = 20, min = 1)
	private String eventCode;

	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	@JoinColumn(name = "OFFER_ID")
	private OfferTemplate offerTemplate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_SUBSCRIPTION_DATE")
	private Date startSubscriptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_SUBSCRIPTION_DATE")
	private Date endSubscriptionDate;

	@Column(name = "NB_PERIOD")
	private Integer nbPeriod;

	@Column(name = "DISCOUNT_PERCENT", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	@Min(0)
	private BigDecimal percent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELLER_ID")
	private Seller seller;

	public String getEventCode() {
		return eventCode;
	}

	public Integer getBusinessIntermediaryId() {
		return businessIntermediaryId;
	}

	public void setBusinessIntermediaryId(Integer businessIntermediaryId) {
		this.businessIntermediaryId = businessIntermediaryId;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
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

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((eventCode == null) ? 0 : eventCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		DiscountPlanMatrix other = (DiscountPlanMatrix) obj;
		if (eventCode == null) {
			if (other.eventCode != null)
				return false;
		} else if (!eventCode.equals(other.eventCode))
			return false;
		return true;
	}

	@AssertTrue(message = "start date should be before end date")
	public boolean isValidDate() {
		return (this.startSubscriptionDate==null)||(this.endSubscriptionDate==null)||(this.startSubscriptionDate.before(this.endSubscriptionDate));
	}

}
