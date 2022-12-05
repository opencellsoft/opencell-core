/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;

/**
 * Bank operation
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "ar_bank_operation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_bank_operation_seq"), })
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

    @Convert(converter = NumericBooleanConverter.class)
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
