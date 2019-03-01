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

public enum BillingRunStatusEnum {

    NEW(1, "BillingRunStatusEnum.new"), // the BR just got created
    PREINVOICED(2, "BillingRunStatusEnum.preinvoiced"), // the preinvoicing report have been generated
    PREVALIDATED(3, "BillingRunStatusEnum.prevalidated"), // the preinvoicing report have been validated
    CANCELLED(4, "BillingRunStatusEnum.cancelled"), // the BR is cancelled, end of the process
    POSTINVOICED(5, "BillingRunStatusEnum.postinvoiced"), // the invoices and postinvoicing report have been generated
    POSTVALIDATED(6, "BillingRunStatusEnum.postvalidated"), // the postinvoicing report have been validated
    VALIDATED(7, "BillingRunStatusEnum.validated"),// the invoices are assigned an invoice number, end of the process
    CANCELLING(8, "BillingRunStatusEnum.cancelling");

    private Integer id;
    private String label;

    BillingRunStatusEnum(Integer id, String label) {
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
     * @param id id of billing run status
     * @return instance of BillingRunStatusEnum
     */
    public static BillingRunStatusEnum getValue(Integer id) {
        if (id != null) {
            for (BillingRunStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
