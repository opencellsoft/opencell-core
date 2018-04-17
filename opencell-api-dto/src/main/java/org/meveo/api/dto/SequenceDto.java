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
package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Sequence;

/**
 * The Class SequenceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SequenceDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4763606402719751014L;

    /** The prefix EL. */
    private String prefixEL;

    /** The sequence size. */
    private Integer sequenceSize;

    /** The current invoice nb. */
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
    public SequenceDto(Sequence sequence) {
        if (sequence != null) {
            this.prefixEL = sequence.getPrefixEL();
            this.sequenceSize = sequence.getSequenceSize();
            this.currentInvoiceNb = sequence.getCurrentInvoiceNb();
        }
    }

    /**
     * From dto.
     *
     * @return the sequence
     */
    public Sequence fromDto() {
        Sequence sequence = new Sequence();
        sequence.setPrefixEL(getPrefixEL());
        sequence.setSequenceSize(getSequenceSize());
        sequence.setCurrentInvoiceNb(getCurrentInvoiceNb());
        return sequence;
    }

    /**
     * Update from dto.
     *
     * @param sequence the sequence
     * @return the sequence
     */
    public Sequence updateFromDto(Sequence sequence) {
        if (!StringUtils.isBlank(getPrefixEL())) {
            sequence.setPrefixEL(getPrefixEL());
        }
        if (getSequenceSize() != null) {
            sequence.setSequenceSize(getSequenceSize());
        }
        if (getCurrentInvoiceNb() != null) {
            sequence.setCurrentInvoiceNb(getCurrentInvoiceNb());
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
    public Integer getSequenceSize() {
        return sequenceSize;
    }

    /**
     * Sets the sequence size.
     *
     * @param sequenceSize the sequenceSize to set
     */
    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    /**
     * Gets the current invoice nb.
     *
     * @return the currentInvoiceNb
     */
    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    /**
     * Sets the current invoice nb.
     *
     * @param currentInvoiceNb the currentInvoiceNb to set
     */
    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }

    @Override
    public String toString() {
        return "SequenceDto [prefixEL=" + prefixEL + ", sequenceSize=" + sequenceSize + ", currentInvoiceNb=" + currentInvoiceNb + "]";
    }

}