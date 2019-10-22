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

    
    CREATED(1, "invoiceStatusEnum.created"), GENERATED(2, "invoiceStatusEnum.generated"), SENT(3, "invoiceStatusEnum.sent"), PAID(4, "invoiceStatusEnum.paid"), PPAID(5, "invoiceStatusEnum.ppaid"), 
    UNPAID(6, "invoiceStatusEnum.unpaid"), ABANDONED(7, "invoiceStatusEnum.abandonned"), REFUNDED(8, "invoiceStatusEnum.refunded"), DISPUTED(9, "invoiceStatusEnum.disputed");
    
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
