/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;

/**
 * Bean for EDR data.
 * 
 * @author seb
 * @created Aug 6, 2012
 */

@Entity
@Table(name = "RATING_EDR")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_EDR_SEQ")
public class EDR  extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1278336655583933747L;

	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	/**
	 * the origin batch the EDR comes from (like a CDR file name)
	 */
	@Column(name="ORIGIN_BATCH")
	private String originBatch;
	
	/**
	 * the origin record the EDR comes from (like a CDR magic number)
	 */
	@Column(name="ORIGIN_RECORD")
	private String originRecord;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EVENT_DATE")
	private Date eventDate;

	@Column(name="QUANTITY")
	private BigDecimal quantity;
	
	@Column(name="PARAMETER_1")
	private String parameter1;
	
	@Column(name="PARAMETER_2")
	private String parameter2;
	
	@Column(name="PARAMETER_3")
	private String parameter3;
	
	@Column(name="PARAMETER_4")
	private String parameter4;

	@Enumerated(EnumType.STRING)
	@Column(name="STATUS")
	private EDRStatusEnum status;
	
	@Column(name="REJECT_REASON")
	private String rejectReason;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED")
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED")
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
	
}
