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
package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_subscrip_termin_reason", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_sub_term_reason_seq"), })
public class SubscriptionTerminationReason extends BusinessEntity {

    private static final long serialVersionUID = 8579279870178217508L;

    @Type(type = "numeric_boolean")
    @Column(name = "apply_agreement")
    private boolean applyAgreement;

    @Type(type = "numeric_boolean")
    @Column(name = "apply_reimbursment")
    private boolean applyReimbursment;

    @Type(type = "numeric_boolean")
    @Column(name = "apply_termination_charges")
    private boolean applyTerminationCharges;

    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    public boolean isApplyReimbursment() {
        return applyReimbursment;
    }

    public void setApplyReimbursment(boolean applyReimbursment) {
        this.applyReimbursment = applyReimbursment;
    }

    public boolean isApplyTerminationCharges() {
        return applyTerminationCharges;
    }

    public void setApplyTerminationCharges(boolean applyTerminationCharges) {
        this.applyTerminationCharges = applyTerminationCharges;
    }
}