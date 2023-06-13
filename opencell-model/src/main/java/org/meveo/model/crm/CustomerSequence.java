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

package org.meveo.model.crm;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.Seller;
import org.meveo.model.sequence.GenericSequence;

import java.util.Objects;

/**
 * Customer numbering sequence
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Entity
@ExportIdentifier({ "code", "seller.code" })
@Table(name = "crm_customer_sequence")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_customer_sequence_seq"), })
public class CustomerSequence extends BusinessEntity {

    private static final long serialVersionUID = 181203276349593823L;

    /**
     * Sequence rule
     */
    @Embedded
    private GenericSequence genericSequence = new GenericSequence();

    /**
     * Seller associated to a customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    public GenericSequence getGenericSequence() {
        return genericSequence;
    }

    public void setGenericSequence(GenericSequence genericSequence) {
        this.genericSequence = genericSequence;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CustomerSequence)) return false;
		if (!super.equals(o)) return false;
		CustomerSequence that = (CustomerSequence) o;
		return Objects.equals(getGenericSequence(), that.getGenericSequence()) && Objects.equals(getSeller(), that.getSeller());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getGenericSequence(), getSeller());
	}
}
