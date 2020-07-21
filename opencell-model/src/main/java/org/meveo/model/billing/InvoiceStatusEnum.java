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
    DISPUTED(9, "invoiceStatusEnum.disputed");
    
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
