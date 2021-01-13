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

public enum BillingRunStatusEnum {

    NEW(1, "BillingRunStatusEnum.new"), // the BR just got created
    PREINVOICED(2, "BillingRunStatusEnum.preinvoiced"), // the preinvoicing report have been generated
    PREVALIDATED(3, "BillingRunStatusEnum.prevalidated"), // the preinvoicing report have been validated
    CANCELED(4, "BillingRunStatusEnum.cancelled"), // the BR is cancelled, end of the process
    INVOICES_GENERATED(5, "BillingRunStatusEnum.invoicesGenerated"), // the invoices and postinvoicing report have been generated
    POSTINVOICED(6, "BillingRunStatusEnum.postinvoiced"), // the invoices and postinvoicing report have been generated
    POSTVALIDATED(7, "BillingRunStatusEnum.postvalidated"), // the postinvoicing report have been validated
    VALIDATED(8, "BillingRunStatusEnum.validated"),// the invoices are assigned an invoice number, end of the process
    CANCELLING(9, "BillingRunStatusEnum.cancelling"),
    REJECTED(10, "BillingRunStatusEnum.Rejected");// the billing run or some invoices are rejected and need to be validated/cancelled.

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
