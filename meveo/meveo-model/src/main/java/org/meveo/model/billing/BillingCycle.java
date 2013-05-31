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
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.Calendar;

/**
 * Billing cycle.
 */
@Entity
@Table(name = "BILLING_CYCLE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CYCLE_SEQ")
public class BillingCycle extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "BILLING_TEMPLATE_NAME", nullable = false)
	@Size(max = 50, min = 1)
	@NotNull
	private String billingTemplateName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CALENDAR")
	private Calendar calendar;

	@Column(name = "INVOICE_DATE_DELAY")
	private Integer invoiceDateDelay;

	@Column(name = "DUE_DATE_DELAY")
	private Integer dueDateDelay;

	@OneToMany(mappedBy = "billingCycle", fetch = FetchType.LAZY)
	private List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

	public String getBillingTemplateName() {
		return billingTemplateName;
	}

	public void setBillingTemplateName(String billingTemplateName) {
		this.billingTemplateName = billingTemplateName;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public Integer getInvoiceDateDelay() {
		return invoiceDateDelay;
	}

	public void setInvoiceDateDelay(Integer invoiceDateDelay) {
		this.invoiceDateDelay = invoiceDateDelay;
	}

	public Integer getDueDateDelay() {
		return dueDateDelay;
	}

	public void setDueDateDelay(Integer dueDateDelay) {
		this.dueDateDelay = dueDateDelay;
	}

	public List<BillingAccount> getBillingAccounts() {
		return billingAccounts;
	}

	public void setBillingAccounts(List<BillingAccount> billingAccounts) {
		this.billingAccounts = billingAccounts;
	}

	public Date getNextCalendarDate(Date date) {

		return calendar != null ? calendar.nextCalendarDate(date) : null;
	}

	public Date getNextCalendarDate() {

		return calendar != null ? calendar.nextCalendarDate(new Date()) : null;
	}

}
