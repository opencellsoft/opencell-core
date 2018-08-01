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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "ar_bank_operation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "ar_bank_operation_seq"), })
public class BankOperation extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "code_op", length = 255)
    @Size(max = 255)
	private String codeOp;

	@Column(name = "date_op")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOp;

	@Column(name = "date_val")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateVal;

	@Column(name = "label_1", length = 255)
    @Size(max = 255)
	private String lebel1;

	@Column(name = "label_2", length = 255)
    @Size(max = 255)
	private String lebel2;

	@Column(name = "label_3", length = 255)
    @Size(max = 255)
	private String lebel3;

	@Column(name = "invoice_id", length = 255)
    @Size(max = 255)
	private String invocieId;

	@Column(name = "reference", length = 255)
    @Size(max = 255)
	private String refrence;

	@Column(name = "debit")
	private BigDecimal debit;

	@Column(name = "credit")
	private BigDecimal credit;

	@Type(type="numeric_boolean")
    @Column(name = "is_valid")
	private boolean isValid;

	@Column(name = "error_cause", length = 255)
    @Size(max = 255)
	private String errorMessage;

	@Column(name = "file_name", length = 255)
    @Size(max = 255)
	private String fileName;

	public BankOperation() {
	}

	public String getCodeOp() {
		return codeOp;
	}

	public void setCodeOp(String codeOp) {
		this.codeOp = codeOp;
	}

	public Date getDateOp() {
		return dateOp;
	}

	public void setDateOp(Date dateOp) {
		this.dateOp = dateOp;
	}

	public Date getDateVal() {
		return dateVal;
	}

	public void setDateVal(Date dateVal) {
		this.dateVal = dateVal;
	}

	public String getLebel1() {
		return lebel1;
	}

	public void setLebel1(String lebel1) {
		this.lebel1 = lebel1;
	}

	public String getLebel2() {
		return lebel2;
	}

	public void setLebel2(String lebel2) {
		this.lebel2 = lebel2;
	}

	public String getLebel3() {
		return lebel3;
	}

	public void setLebel3(String lebel3) {
		this.lebel3 = lebel3;
	}

	public String getInvocieId() {
		return invocieId;
	}

	public void setInvocieId(String invocieId) {
		this.invocieId = invocieId;
	}

	public String getRefrence() {
		return refrence;
	}

	public void setRefrence(String refrence) {
		this.refrence = refrence;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
