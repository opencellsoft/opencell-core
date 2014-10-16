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
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_ACTION_DUNNING")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_ACTION_DUNNING_SEQ")
public class ActionDunning extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CREATON_DATE")
	@Temporal(TemporalType.DATE)
	private Date creationDate;

	@Column(name = "ACTION_TYPE")
	@Enumerated(EnumType.STRING)
	private DunningActionTypeEnum typeAction;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private DunningActionStatusEnum status;

	@Column(name = "STATUS_DATE")
	@Temporal(TemporalType.DATE)
	private Date statusDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
	private CustomerAccount customerAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECORDED_INVOICE_ID")
	private RecordedInvoice recordedInvoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DUNNING_LOT_ID")
	private DunningLOT dunningLOT;

	@Column(name = "FROM_LEVEL")
	@Enumerated(EnumType.STRING)
	private DunningLevelEnum fromLevel;

	@Column(name = "TO_LEVEL")
	@Enumerated(EnumType.STRING)
	private DunningLevelEnum toLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_PLAN_ITEM_ID")
	private ActionPlanItem actionPlanItem;

	@Column(name = "AMOUNT_DUE")
	private BigDecimal amountDue;

	public ActionDunning() {
	}

	public DunningActionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(DunningActionStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public RecordedInvoice getRecordedInvoice() {
		return recordedInvoice;
	}

	public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
		this.recordedInvoice = recordedInvoice;
	}

	public void setTypeAction(DunningActionTypeEnum typeAction) {
		this.typeAction = typeAction;
	}

	public DunningActionTypeEnum getTypeAction() {
		return typeAction;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setDunningLOT(DunningLOT dunningLOT) {
		this.dunningLOT = dunningLOT;
	}

	public DunningLOT getDunningLOT() {
		return dunningLOT;
	}

	public void setFromLevel(DunningLevelEnum fromLevel) {
		this.fromLevel = fromLevel;
	}

	public DunningLevelEnum getFromLevel() {
		return fromLevel;
	}

	public void setToLevel(DunningLevelEnum toLevel) {
		this.toLevel = toLevel;
	}

	public DunningLevelEnum getToLevel() {
		return toLevel;
	}

	public void setActionPlanItem(ActionPlanItem actionPlanItem) {
		this.actionPlanItem = actionPlanItem;
	}

	public ActionPlanItem getActionPlanItem() {
		return actionPlanItem;
	}

	public void setAmountDue(BigDecimal amountDue) {
		this.amountDue = amountDue;
	}

	public BigDecimal getAmountDue() {
		return amountDue;
	}

}
