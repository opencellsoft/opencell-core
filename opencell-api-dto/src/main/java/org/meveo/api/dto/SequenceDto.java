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
 * @author Edward P. Legaspi
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SequenceDto extends BaseDto {

    private static final long serialVersionUID = 4763606402719751014L;
    private String prefixEL;
    private Integer sequenceSize;
    private Long currentInvoiceNb;

    public SequenceDto() {
    }

    public SequenceDto(Sequence sequence) {
	if (sequence != null) {
	    this.prefixEL = sequence.getPrefixEL();
	    this.sequenceSize = sequence.getSequenceSize();
	    this.currentInvoiceNb = sequence.getCurrentInvoiceNb();
	}
    }

    public Sequence fromDto() {
	Sequence sequence = new Sequence();
	sequence.setPrefixEL(getPrefixEL());
	sequence.setSequenceSize(getSequenceSize());
	sequence.setCurrentInvoiceNb(getCurrentInvoiceNb());
	return sequence;
    }

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
     * @return the prefixEL
     */
    public String getPrefixEL() {
	return prefixEL;
    }

    /**
     * @param prefixEL
     *            the prefixEL to set
     */
    public void setPrefixEL(String prefixEL) {
	this.prefixEL = prefixEL;
    }

    /**
     * @return the sequenceSize
     */
    public Integer getSequenceSize() {
	return sequenceSize;
    }

    /**
     * @param sequenceSize
     *            the sequenceSize to set
     */
    public void setSequenceSize(Integer sequenceSize) {
	this.sequenceSize = sequenceSize;
    }

    /**
     * @return the currentInvoiceNb
     */
    public Long getCurrentInvoiceNb() {
	return currentInvoiceNb;
    }

    /**
     * @param currentInvoiceNb
     *            the currentInvoiceNb to set
     */
    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
	this.currentInvoiceNb = currentInvoiceNb;
    }

    @Override
    public String toString() {
	return "SequenceDto [prefixEL=" + prefixEL + ", sequenceSize=" + sequenceSize + ", currentInvoiceNb=" + currentInvoiceNb + "]";
    }

}