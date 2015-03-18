/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;

/**
 * Bean for EDR data.
 */
@Entity
@Table(name = "RATING_EDR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_EDR_SEQ")
@NamedQueries({ @NamedQuery(name = "EDR.getEdrsForCache", query = "select CONCAT(p.id,'_',e.originBatch,'_',e.originRecord) from EDR e join e.provider p where e.status= org.meveo.model.rating.EDRStatusEnum.OPEN ORDER BY e.eventDate DESC") })
public class EDR extends BaseEntity {

	private static final long serialVersionUID = 1278336655583933747L;

	public static String EDR_TABLE_ORIGIN = "EDR_TABLE";

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	/**
	 * the origin batch the EDR comes from (like a CDR file name or EDR table)
	 */
	@Column(name = "ORIGIN_BATCH")
	private String originBatch;

	/**
	 * the origin record the EDR comes from (like a CDR magic number)
	 */
	@Column(name = "ORIGIN_RECORD")
	private String originRecord;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVENT_DATE")
	private Date eventDate;

	@Column(name = "QUANTITY", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal quantity;

	@Column(name = "PARAMETER_1")
	private String parameter1;

	@Column(name = "PARAMETER_2")
	private String parameter2;

	@Column(name = "PARAMETER_3")
	private String parameter3;

	@Column(name = "PARAMETER_4")
	private String parameter4;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private EDRStatusEnum status;

	@Column(name = "REJECT_REASON")
	private String rejectReason;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED")
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED")
	private Date lastUpdate;

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getOriginBatch() {
		return originBatch;
	}

	public void setOriginBatch(String originBatch) {
		this.originBatch = originBatch;
	}

	public String getOriginRecord() {
		return originRecord;
	}

	public void setOriginRecord(String originRecord) {
		this.originRecord = originRecord;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}

	public String getParameter4() {
		return parameter4;
	}

	public void setParameter4(String parameter4) {
		this.parameter4 = parameter4;
	}

	public EDRStatusEnum getStatus() {
		return status;
	}

	public void setStatus(EDRStatusEnum status) {
		this.status = status;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "EDR [subscription=" + subscription + ", originBatch="
				+ originBatch + ", originRecord=" + originRecord
				+ ", eventDate=" + eventDate + ", quantity=" + quantity
				+ ", parameter1=" + parameter1 + ", parameter2=" + parameter2
				+ ", parameter3=" + parameter3 + ", parameter4=" + parameter4
				+ ", status=" + status + ", rejectReason=" + rejectReason
				+ ", created=" + created + ", lastUpdate=" + lastUpdate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EDR other = (EDR) obj;
		return this.toString().equals(other.toString());
	}

}
