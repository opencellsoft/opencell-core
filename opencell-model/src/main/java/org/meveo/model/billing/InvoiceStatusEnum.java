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

import java.util.Arrays;
import java.util.List;

/**
 * Invoice status.
 */
public enum InvoiceStatusEnum {

    
    /**
     * invoice entity has been created but incomplete
     */
    NEW(1, "invoiceStatusEnum.new", new InvoiceStatusEnum[]{null}),

    /**
     * invoice has been marked as suspect by automatic controls (this status doesn’t block automatic generation)
     */
    SUSPECT(2, "invoiceStatusEnum.suspect", new InvoiceStatusEnum[]{NEW}),

    /**
     * invoice has been rejected by automatic controls (this status block automatic generation)
     */
    REJECTED(3, "invoiceStatusEnum.rejected", new InvoiceStatusEnum[]{NEW, SUSPECT}),
    
    /**
     * invoice is complete but not validated. It can be edited.
     */
    DRAFT(4, "invoiceStatusEnum.draft", new InvoiceStatusEnum[]{NEW, SUSPECT, REJECTED}),

    /**
     * invoice has been canceled (all related rated transactions are released. This is a final status)
     */
    CANCELED(5, "invoiceStatusEnum.canceled", new InvoiceStatusEnum[]{NEW, SUSPECT, REJECTED}),
    
    /**
     * invoice is validated and cannot be edited anymore (this a final status)
     */
    VALIDATED(6, "invoiceStatusEnum.validated", new InvoiceStatusEnum[] {null, NEW, DRAFT}),

    SENT(7, "invoiceStatusEnum.sent", new InvoiceStatusEnum[] {null, NEW, VALIDATED}),
    
    /**
     * when when invoice AO is disputed or into dunning active
     */
    DISPUTED(9, "invoiceStatusEnum.disputed", new InvoiceStatusEnum[] {null, NEW, DRAFT});
    
    private Integer id;
    private String label;
    private List<InvoiceStatusEnum> previousStats;

    InvoiceStatusEnum(Integer id, String label, InvoiceStatusEnum[] previousStats) {
        this.id = id;
        this.label = label;
        this.previousStats=Arrays.asList(previousStats);
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

	/**
	 * @return the nextStats
	 */
	public List<InvoiceStatusEnum> getPreviousStats() {
		return previousStats;
	}

}
