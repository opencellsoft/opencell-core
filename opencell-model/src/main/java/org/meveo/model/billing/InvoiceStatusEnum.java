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

/**
 * Invoice status.
 */
public enum InvoiceStatusEnum {

    /**
     * when invoice created
     */
    CREATED(1, "invoiceStatusEnum.created"),
    
    /**
     * when produced by a job (xml custom, xml, pdf...)
     */
    GENERATED(2, "invoiceStatusEnum.generated"),
    
    /**
     * when exported by a job (email or custom processing)
     */
    SENT(3, "invoiceStatusEnum.sent"),
    
    /**
     *  when fully paid (matched status)
     */
    PAID(4, "invoiceStatusEnum.paid"),
    
    /**
     * when partially paid (unmatched amount >0)
     */
    PPAID(5, "invoiceStatusEnum.ppaid"),
    
    /**
     * when when no payment and due date
     */
    UNPAID(6, "invoiceStatusEnum.unpaid"),
    
    /**
     * when writen off (matched to a write off AO)
     */
    ABANDONED(7, "invoiceStatusEnum.abandonned"),
    
    /**
     * when when refunded (by a credit note through linkedToInvoice )
     */
    REFUNDED(8, "invoiceStatusEnum.refunded"),
    
    /**
     * when when invoice AO is disputed or into dunning active
     */
    DISPUTED(9, "invoiceStatusEnum.disputed"),
    
    /**
     * invoice is computed in draft mode (no invoice number is set)
     */
    DRAFT(10, "invoiceStatusEnum.draft"),
    /**
     * invoice has been rejected without blocker reason by automatic check and is waiting for manual validation,
     */
    SUSPECT(11, "invoiceStatusEnum.suspect"),
    /**
     * invoice has been rejected with blocker reason by automatic check and is waiting for manual validation,
     */
    REJECTED(12, "invoiceStatusEnum.rejected"),
    /**
     * invoice is canceled and won't be further processed during the bulling run
     */
    CANCELED(13, "invoiceStatusEnum.canceled");
    
    private Integer id;
    private String label;

    InvoiceStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;

    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets enum by its id.
     * 
     * @param id of invoice status
     * @return invoice status enum
     */
    public static InvoiceStatusEnum getValue(Integer id) {
        if (id != null) {
            for (InvoiceStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
