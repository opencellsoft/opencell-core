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
package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSequence;

/**
 * The Class SequenceDto.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SequenceDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4763606402719751014L;

    /** The prefix EL. */
    private String prefixEL;
    
    /** The invoice sequence code. */
    private String invoiceSequenceCode;

    /** The sequence size. */
    @Deprecated
    private Integer sequenceSize;

    /** The current invoice nb. */
    @Deprecated
    private Long currentInvoiceNb;

    /**
     * Instantiates a new sequence dto.
     */
    public SequenceDto() {
    }

    /**
     * Instantiates a new sequence dto.
     *
     * @param sequence the sequence
     */
    @Deprecated
    public SequenceDto(InvoiceSequence sequence, String prefixEl) {
        if (sequence != null) {
            this.sequenceSize = sequence.getSequenceSize();
            this.currentInvoiceNb = sequence.getCurrentNumber();
            this.prefixEL = prefixEl;
        }
    }

    /**
     * From dto.
     *
     * @return the sequence
     */
    @Deprecated
    public InvoiceSequence fromDto() {
    	InvoiceSequence sequence = new InvoiceSequence();
        sequence.setSequenceSize(getSequenceSize());
        sequence.setCurrentNumber(getCurrentInvoiceNb());
        return sequence;
    }

    /**
     * Update from dto.
     *
     * @param sequence the sequence
     * @return the sequence
     */
    @Deprecated
    public InvoiceSequence updateFromDto(InvoiceSequence sequence) {
        if (getSequenceSize() != null) {
            sequence.setSequenceSize(getSequenceSize());
        }
        if (getCurrentInvoiceNb() != null) {
            sequence.setCurrentNumber(getCurrentInvoiceNb());
        }
        return sequence;
    }

    /**
     * Gets the prefix EL.
     *
     * @return the prefixEL
     */
    public String getPrefixEL() {
        return prefixEL;
    }

    /**
     * Sets the prefix EL.
     *
     * @param prefixEL the prefixEL to set
     */
    public void setPrefixEL(String prefixEL) {
        this.prefixEL = prefixEL;
    }

    /**
     * Gets the sequence size.
     *
     * @return the sequenceSize
     */
    @Deprecated
    public Integer getSequenceSize() {
        return sequenceSize;
    }

    /**
     * Sets the sequence size.
     *
     * @param sequenceSize the sequenceSize to set
     */
    @Deprecated
    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    /**
     * Gets the current invoice nb.
     *
     * @return the currentInvoiceNb
     */
    @Deprecated
    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    /**
     * Sets the current invoice nb.
     *
     * @param currentInvoiceNb the currentInvoiceNb to set
     */
    @Deprecated
    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }
    
    /**
     * Gets the invoice sequence code.
     * 
	 * @return the invoiceSequenceCode
	 */
	public String getInvoiceSequenceCode() {
		return invoiceSequenceCode;
	}

	/**
	 * Sets the invoice sequence code.
	 * 
	 * @param invoiceSequenceCode the invoiceSequenceCode to set
	 */
	public void setInvoiceSequenceCode(String invoiceSequenceCode) {
		this.invoiceSequenceCode = invoiceSequenceCode;
	}

    @Override
    public String toString() {
        return "SequenceDto [prefixEL=" + prefixEL + ", invoiceSequenceCode=" + invoiceSequenceCode + ", sequenceSize=" + sequenceSize + ", currentInvoiceNb=" + currentInvoiceNb + "]";
    }

}