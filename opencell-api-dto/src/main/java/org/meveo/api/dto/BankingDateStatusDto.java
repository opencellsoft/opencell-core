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

import java.io.Serializable;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class BankingDateStatusDto.
 *
 * @author hznibar
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class BankingDateStatusDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;
    
    /** The date to check. */
    private Date date;
    
    /** The date status: true: if it's a bank working day, false if it's a weekend or holiday. */
    private Boolean isWorkingDate;
    
    
    
    
    /**
     * Instantiates a new banking date status dto.
     */
    public BankingDateStatusDto() {
        super();
    }
    
    /**
     * Instantiates a new banking date status dto.
     *
     * @param date the date
     * @param isWorkingDate the is working date
     */
    public BankingDateStatusDto(Date date, Boolean isWorkingDate) {
        super();
        this.date = date;
        this.isWorkingDate = isWorkingDate;
    }




    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }


    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }


    public Boolean isWorkingDate() {
        return isWorkingDate;
    }


    public void setIsWorkingDate(Boolean isWorkingDate) {
        this.isWorkingDate = isWorkingDate;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BankingDateStatusDto [date=" + date + ", isWorkingDate=" + isWorkingDate + "]";
    }
}