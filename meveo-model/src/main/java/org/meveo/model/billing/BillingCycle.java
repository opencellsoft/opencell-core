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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.Calendar;

/**
 * Billing cycle.
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_CYCLE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CYCLE_SEQ")
public class BillingCycle extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "BILLING_TEMPLATE_NAME", nullable = true)
	@Size(max = 50, min = 0)
	private String billingTemplateName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CALENDAR")
	private Calendar calendar;

    @Column(name = "TRANSACTION_DATE_DELAY")
    private Integer transactionDateDelay;
    
    //used to compute the invoice date from date of billing run
    @Column(name = "INVOICE_DATE_PRODUCTION_DELAY")
    private Integer invoiceDateProductionDelay;
    
    //used for immediate invoicing by oneshot charge
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

	public Integer getTransactionDateDelay() {
        return transactionDateDelay;
    }

    public void setTransactionDateDelay(Integer transactionDateDelay) {
        this.transactionDateDelay = transactionDateDelay;
    }

    public Integer getInvoiceDateProductionDelay() {
        return invoiceDateProductionDelay;
    }

    public void setInvoiceDateProductionDelay(Integer invoiceDateProductionDelay) {
        this.invoiceDateProductionDelay = invoiceDateProductionDelay;
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

	public Date getNextCalendarDate(Date subscriptionDate, Date date) {

		return calendar != null ? calendar.nextCalendarDate(date) : null;
	}

	public Date getNextCalendarDate(Date subscriptionDate) {
		calendar.setInitDate(subscriptionDate);
		return calendar != null ? calendar.nextCalendarDate(new Date()) : null;
	}

}
