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

import org.meveo.model.AuditableEntity;

/**
 * @author anasseh
 * @created 14.02.2011
 */
@Entity
@Table(name = "AR_DDREQUEST_LOT_OP")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DDREQUEST_LOT_OP_SEQ")
public class DDRequestLotOp extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "FROM_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDueDate;

    @Column(name = "TO_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDueDate;

    @Column(name = "DDREQUEST_OP")
    @Enumerated(EnumType.STRING)
    private DDRequestOpEnum ddrequestOp;

    @Column(name = "DDREQUEST_OP_STATUS")
    @Enumerated(EnumType.STRING)
    private DDRequestOpStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DDREQUEST_LOT_ID")
    private DDRequestLOT ddrequestLOT;

    @Column(name = "ERROR_CAUSE")
    private String errorCause;

    /**
     * @return the fromDueDate
     */
    public Date getFromDueDate() {
        return fromDueDate;
    }

    /**
     * @param fromDueDate
     *            the fromDueDate to set
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
     * @param toDueDate
     *            the toDueDate to set
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
     * @param ddrequestOp
     *            the ddrequestOp to set
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
     * @param status
     *            the status to set
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
     * @param ddrequestLOT
     *            the ddrequestLOT to set
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

}
