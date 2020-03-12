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

package org.meveo.admin.async;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.Amounts;

/**
 * Amounts to invoice for a given billable entity
 * 
 * @author Andrius Karpavicius
 */
public class AmountsToInvoice implements Serializable {

    private static final long serialVersionUID = 3109687834951882877L;

    /**
     * ID of an entity to invoice
     */
    private Long entityToInvoiceId;

    /**
     * Amounts to invoice
     */
    private Amounts amountsToInvoice;

    /**
     * Constructor
     */
    public AmountsToInvoice() {
    }

    /**
     * Constructor
     * 
     * @param entityToInvoiceId ID of an entity to invoice
     * @param amountsToInvoice Amounts to invoice
     */
    public AmountsToInvoice(Long entityToInvoiceId, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax) {
        this.entityToInvoiceId = entityToInvoiceId;
        this.amountsToInvoice = new Amounts(amountWithoutTax, amountWithTax, amountTax);
    }

    /**
     * Constructor
     * 
     * @param entityToInvoiceId ID of an entity to invoice
     * @param amountsToInvoice Amounts to invoice
     */
    public AmountsToInvoice(Long entityToInvoiceId, Amounts amountsToInvoice) {
        this.entityToInvoiceId = entityToInvoiceId;
        this.amountsToInvoice = amountsToInvoice;
    }

    /**
     * @return ID of an entity to invoice
     */
    public Long getEntityToInvoiceId() {
        return entityToInvoiceId;
    }

    /**
     * @param entityToInvoiceId ID of an entity to invoice
     */
    public void setEntityToInvoiceId(Long entityToInvoiceId) {
        this.entityToInvoiceId = entityToInvoiceId;
    }

    /**
     * @return Amounts to invoice
     */
    public Amounts getAmountsToInvoice() {
        return amountsToInvoice;
    }

    /**
     * @param amountsToInvoice Amounts to invoice
     */
    public void setAmountsToInvoice(Amounts amountsToInvoice) {
        this.amountsToInvoice = amountsToInvoice;
    }
}