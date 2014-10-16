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
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_DDREQUEST_LOT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DDREQUEST_LOT_SEQ")
public class DDRequestLOT extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "RETURN_FILE_NAME")
	private String returnFileName;

	@Column(name = "SEND_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate;

	@Column(name = "INVOICE_NUMBER")
	private Integer invoicesNumber;

	
	@Column(name = "IS_PAYMENT_CREATED")
	private boolean paymentCreated;

	@Column(name = "INVOICE_AMOUNT", precision = 23, scale = 12)
	private BigDecimal invoicesAmount;

	@OneToMany(mappedBy = "ddRequestLOT", fetch = FetchType.LAZY)
	private List<RecordedInvoice> invoices = new ArrayList<RecordedInvoice>();

	@OneToMany(mappedBy = "ddRequestLOT", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
	
	@Column(name = "RETURN_STATUS_CODE")
	private String returnStatusCode;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public void setInvoices(List<RecordedInvoice> invoices) {
		this.invoices = invoices;
	}

	public List<RecordedInvoice> getInvoices() {
		return invoices;
	}

	public Integer getInvoicesNumber() {
		return invoicesNumber;
	}

	public void setInvoicesNumber(Integer invoicesNumber) {
		this.invoicesNumber = invoicesNumber;
	}

	public BigDecimal getInvoicesAmount() {
		return invoicesAmount;
	}

	public void setInvoicesAmount(BigDecimal invoicesAmount) {
		this.invoicesAmount = invoicesAmount;
	}



	public boolean isPaymentCreated() {
		return paymentCreated;
	}

	public void setPaymentCreated(boolean paymentCreated) {
		this.paymentCreated = paymentCreated;
	}

	public void setDdrequestItems(List<DDRequestItem> ddrequestItems) {
		this.ddrequestItems = ddrequestItems;
	}

	public List<DDRequestItem> getDdrequestItems() {
		return ddrequestItems;
	}

	public String getReturnStatusCode() {
		return returnStatusCode;
	}

	public void setReturnStatusCode(String returnStatusCode) {
		this.returnStatusCode = returnStatusCode;
	}

	public String getReturnFileName() {
		return returnFileName;
	}

	public void setReturnFileName(String returnFileName) {
		this.returnFileName = returnFileName;
	}

	
	

}
