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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class InvoiceSequencesDto.
 */
@XmlRootElement(name = "InvoiceSequences")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSequencesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice sequences. */
    private List<InvoiceSequenceDto> invoiceSequences = new ArrayList<InvoiceSequenceDto>();

    /**
     * Instantiates a new invoice sequences dto.
     */
    public InvoiceSequencesDto() {

    }

    /**
     * Gets the invoice sequences.
     *
     * @return the invoiceSequences
     */
    public List<InvoiceSequenceDto> getInvoiceSequences() {
        return invoiceSequences;
    }

    /**
     * Sets the invoice sequences.
     *
     * @param invoiceSequences the invoiceSequences to set
     */
    public void setInvoiceSequences(List<InvoiceSequenceDto> invoiceSequences) {
        this.invoiceSequences = invoiceSequences;
    }

    @Override
    public String toString() {
        return "InvoiceSequencesDto [InvoiceSequences=" + invoiceSequences + "]";
    }

}