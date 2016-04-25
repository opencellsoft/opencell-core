/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_DDREQUEST_LOT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DDREQUEST_LOT_SEQ")
public class DDRequestLOT extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME", length = 255)
    @Size(max = 255)
	private String fileName;
	
	@Column(name = "RETURN_FILE_NAME", length = 255)
    @Size(max = 255)
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

	@OneToMany(mappedBy = "ddRequestLOT", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
	
	@Column(name = "RETURN_STATUS_CODE", length = 255)
    @Size(max = 255)
	private String returnStatusCode;

	@Column(name = "REJECTED_CAUSE", length = 255)
    @Size(max = 255)
	private String rejectedCause;
	
	@Column(name = "REJECTED_INVOICES")
	private Integer rejectedInvoices;
	
	@Column(name = "FILE_FORMAT")
	@Enumerated(EnumType.STRING)
	private DDRequestFileFormatEnum fileFormat;
	
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

	public String getRejectedCause() {
		return rejectedCause;
	}

	public void setRejectedCause(String rejectedCause) {
		this.rejectedCause = rejectedCause;
	}

	public Integer getRejectedInvoices() {
		return rejectedInvoices;
	}

	public void setRejectedInvoices(Integer rejectedInvoices) {
		this.rejectedInvoices = rejectedInvoices;
	}

	/**
	 * @return the fileFormat
	 */
	public DDRequestFileFormatEnum getFileFormat() {
		return fileFormat;
	}

	/**
	 * @param fileFormat the fileFormat to set
	 */
	public void setFileFormat(DDRequestFileFormatEnum fileFormat) {
		this.fileFormat = fileFormat;
	}

	

	
	

}
