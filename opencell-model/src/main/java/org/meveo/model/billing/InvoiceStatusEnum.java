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
     * invoice entity has been created but incomplete
     */
    NEW(1, "invoiceStatusEnum.new"),
    
    /**
     * invoice is complete but not validated. It can be edited.
     */
    DRAFT(2, "invoiceStatusEnum.draft"),

    /**
     * invoice has been marked as suspect by automatic controls (this status doesn’t block automatic generation)
     */
    SUSPECT(3, "invoiceStatusEnum.suspect"), 

    /**
     * invoice has been rejected by automatic controls (this status block automatic generation)
     */
    REJECTED(4, "invoiceStatusEnum.rejected"), 

    /**
     * invoice has been canceled (all related rated transactions are released. This is a final status)
     */
    CANCELED(5, "invoiceStatusEnum.canceled"),
    
    /**
     * invoice is validated and cannot be edited anymore (this a final status)
     */
    VALIDATED(6, "invoiceStatusEnum.validated");

    
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
