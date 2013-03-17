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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEDINA_REJECTED_CDR")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_REJECTED_CDR_SEQ")
public class RejectedCDR extends BaseEntity {
	
	public enum RejectedCDRFlag {REJECTED_FOR_RETRY, MANUAL_RETRY, REJECTED_FINALLY, PROCESSED};

	private static final long serialVersionUID = 1L;
	
	@Column(name ="FILE_NAME")
	private String fileName;
	
	@Column(name = "REASON")
	private String rejectionReason;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTION_DATE")
	private Date date;
	
	@Column(name = "TICKET_DATA", length=4000)
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
