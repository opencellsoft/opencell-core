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

package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.InvoiceSequence;

/**
 * The Class InvoiceSequenceDto.
 * 
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "InvoiceSequence")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSequenceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sequence size. */
    private Integer sequenceSize;

    /** The current invoice nb. */
    private Long currentInvoiceNb;


    /**
     * Instantiates a new invoice type dto.
     */
    public InvoiceSequenceDto() {

    }

    /**
     * Instantiates a new invoice sequence dto.
     *
     * @param invoiceSequence the invoice sequence
     */
    public InvoiceSequenceDto(InvoiceSequence invoiceSequence) {
        super(invoiceSequence);
        this.sequenceSize = invoiceSequence.getSequenceSize();
        this.currentInvoiceNb = invoiceSequence.getCurrentNumber();
    }

    public Integer getSequenceSize() {
		return sequenceSize;
	}

	public void setSequenceSize(Integer sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}

	@Override
    public String toString() {
        return "InvoiceSequenceDto [code=" + getCode() + ", description=" + getDescription() + ", sequenceSize=" + getSequenceSize() + ", sequenceSize=" + getSequenceSize() + "]";
    }
}