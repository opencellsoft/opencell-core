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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEDINA_REJECTED_CDR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_REJECTED_CDR_SEQ")
public class RejectedCDR extends BaseEntity {

	public enum RejectedCDRFlag {
		REJECTED_FOR_RETRY, 
		MANUAL_RETRY, 
		REJECTED_FINALLY, 
		PROCESSED
	};

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "REASON")
	private String rejectionReason;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTION_DATE")
	private Date date;

	@Column(name = "TICKET_DATA", length = 4000)
	private String ticketData;

	@Enumerated(EnumType.STRING)
	@Column(name = "REJECTED_FLAG")
	private RejectedCDRFlag rejectedFlag;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTicketData() {
		return ticketData;
	}

	public void setTicketData(String ticketData) {
		this.ticketData = ticketData;
	}

	public RejectedCDRFlag getRejectedFlag() {
		return rejectedFlag;
	}

	public void setRejectedFlag(RejectedCDRFlag rejectedFlag) {
		this.rejectedFlag = rejectedFlag;
	}

}
