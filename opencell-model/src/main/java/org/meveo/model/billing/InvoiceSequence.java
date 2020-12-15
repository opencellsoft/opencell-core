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

package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.sequence.Sequence;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "billing_seq_invoice")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "bill_seq_invoice_seq"), })
@NamedQueries({ @NamedQuery(name = "InvoiceSequence.currentInvoiceNb", query = "select max(currentNumber) from InvoiceSequence i where i.code=:invoiceSequenceCode") })
public class InvoiceSequence extends Sequence {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @OneToMany(mappedBy = "invoiceSequence", fetch = FetchType.LAZY)
    private List<InvoiceType> invoiceTypes;

    @OneToMany(mappedBy = "invoiceSequence", fetch = FetchType.LAZY)
    private List<InvoiceTypeSellerSequence> invoiceTypeSellerSequences;

    /**
     * A previously invoiceNb held by this sequence, usually less by one, unless numbers were reserved by more than one
     */
    @Transient
    private Long previousInvoiceNb = 0L;

    public InvoiceSequence() {
    }

    public InvoiceSequence(Integer sequenceSize, Long currentInvoiceNb) {
        super(sequenceSize, currentInvoiceNb);
    }

    public List<InvoiceType> getInvoiceTypes() {
        if (invoiceTypes == null) {
            invoiceTypes = new ArrayList<>();
        }
        return invoiceTypes;
    }

    public void setInvoiceTypes(List<InvoiceType> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
    }

    public List<InvoiceTypeSellerSequence> getInvoiceTypeSellerSequences() {
        return invoiceTypeSellerSequences;
    }

    public void setInvoiceTypeSellerSequences(List<InvoiceTypeSellerSequence> invoiceTypeSellerSequences) {
        this.invoiceTypeSellerSequences = invoiceTypeSellerSequences;
    }

    public Long getPreviousInvoiceNb() {
        return previousInvoiceNb;
    }

    public void setPreviousInvoiceNb(Long previousInvoiceNb) {
        this.previousInvoiceNb = previousInvoiceNb;
    }
}