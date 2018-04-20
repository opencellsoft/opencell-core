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
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "ar_ddrequest_lot_op")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_ddrequest_lot_op_seq"), })
public class DDRequestLotOp extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "from_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDueDate;

    @Column(name = "to_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDueDate;

    @Column(name = "ddrequest_op")
    @Enumerated(EnumType.STRING)
    private DDRequestOpEnum ddrequestOp;

    @Column(name = "ddrequest_op_status")
    @Enumerated(EnumType.STRING)
    private DDRequestOpStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddrequest_lot_id")
    private DDRequestLOT ddrequestLOT;

    @Column(name = "error_cause", length = 255)
    @Size(max = 255)
    private String errorCause;

    @Column(name = "file_format")
    @Enumerated(EnumType.STRING)
    private DDRequestFileFormatEnum fileFormat;

    /**
     * @return the fromDueDate
     */
    public Date getFromDueDate() {
        return fromDueDate;
    }

    /**
     * @param fromDueDate the fromDueDate to set
     */
    public void setFromDueDate(Date fromDueDate) {
        this.fromDueDate = fromDueDate;
    }

    /**
     * @return the toDueDate
     */
    public Date getToDueDate() {
        return toDueDate;
    }

    /**
     * @param toDueDate the toDueDate to set
     */
    public void setToDueDate(Date toDueDate) {
        this.toDueDate = toDueDate;
    }

    /**
     * @return the ddrequestOp
     */
    public DDRequestOpEnum getDdrequestOp() {
        return ddrequestOp;
    }

    /**
     * @param ddrequestOp the ddrequestOp to set
     */
    public void setDdrequestOp(DDRequestOpEnum ddrequestOp) {
        this.ddrequestOp = ddrequestOp;
    }

    /**
     * @return the status
     */
    public DDRequestOpStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(DDRequestOpStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the ddrequestLOT
     */
    public DDRequestLOT getDdrequestLOT() {
        return ddrequestLOT;
    }

    /**
     * @param ddrequestLOT the ddrequestLOT to set
     */
    public void setDdrequestLOT(DDRequestLOT ddrequestLOT) {
        this.ddrequestLOT = ddrequestLOT;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getErrorCause() {
        return errorCause;
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
