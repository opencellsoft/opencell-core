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

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferServiceTemplate;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A mapping between Invoice type, seller and invoice numbering sequence
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "invoiceType.code", "seller.code" })
@Table(name = "billing_seq_invtyp_sell")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "bill_seq_it_sell_seq"), })
public class InvoiceTypeSellerSequence implements IEntity {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    /**
     * Invoice type
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoicetype_id")
    @NotNull
    private InvoiceType invoiceType;

    /**
     * Seller
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    @NotNull
    private Seller seller;

    /**
     * Invoice numbering sequence
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_sequence_id")
    private InvoiceSequence invoiceSequence;

    /**
     * Expression to calculate Invoice number prefix
     */
    @Column(name = "prefix_el", length = 2000)
    @Size(max = 2000)
    private String prefixEL = "";

    public InvoiceTypeSellerSequence() {

    }

    public InvoiceTypeSellerSequence(InvoiceType invoiceType, Seller seller, InvoiceSequence invoiceSequence, String prefixEL) {
        super();
        this.invoiceType = invoiceType;
        this.seller = seller;
        this.invoiceSequence = invoiceSequence;
        this.prefixEL = prefixEL;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public InvoiceSequence getInvoiceSequence() {
        return invoiceSequence;
    }

    public void setInvoiceSequence(InvoiceSequence invoiceSequence) {
        this.invoiceSequence = invoiceSequence;
    }

    public String getPrefixEL() {
        return prefixEL;
    }

    public void setPrefixEL(String prefixEL) {
        this.prefixEL = prefixEL;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OfferServiceTemplate)) {
            return false;
        }

        InvoiceTypeSellerSequence other = (InvoiceTypeSellerSequence) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (invoiceType != null) {
            if (!invoiceType.equals(other.getInvoiceType())) {
                return false;
            }
        } else if (other.getInvoiceType() != null) {
            return false;
        }

        if (seller != null) {
            if (!seller.equals(other.getSeller())) {
                return false;
            }
        } else if (other.getSeller() != null) {
            return false;
        }
        return true;
    }
}