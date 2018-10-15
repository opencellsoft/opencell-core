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
package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class PaymentScheduleInstanceDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleInstanceBalanceDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceBalanceDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The nb schedule paid. */
    private int nbSchedulePaid;
    
    /** The nb schedule incoming. */
    private int nbScheduleIncoming;
    
    /** The sum amount paid. */
    private BigDecimal sumAmountPaid;
    
    /** The sum amount incoming. */
    private BigDecimal sumAmountIncoming;

    /**
     * Instantiates a new payment schedule instance balance dto.
     */
    public PaymentScheduleInstanceBalanceDto() {

    }

    /**
     * Instantiates a new payment schedule instance balance dto.
     *
     * @param nbSchedulePaid the nb schedule paid
     * @param nbScheduleIncoming the nb schedule incoming
     * @param sumAmountPaid the sum amount paid
     * @param sumAmountIncoming the sum amount incoming
     */
    public PaymentScheduleInstanceBalanceDto(int nbSchedulePaid, int nbScheduleIncoming, BigDecimal sumAmountPaid, BigDecimal sumAmountIncoming) {
        this.nbSchedulePaid = nbSchedulePaid;
        this.nbScheduleIncoming = nbScheduleIncoming;
        this.sumAmountPaid = sumAmountPaid;
        this.sumAmountIncoming = sumAmountIncoming;
    }

    /**
     * Gets the nb schedule paid.
     *
     * @return the nbSchedulePaid
     */
    public int getNbSchedulePaid() {
        return nbSchedulePaid;
    }

    /**
     * Sets the nb schedule paid.
     *
     * @param nbSchedulePaid the nbSchedulePaid to set
     */
    public void setNbSchedulePaid(int nbSchedulePaid) {
        this.nbSchedulePaid = nbSchedulePaid;
    }

    /**
     * Gets the nb schedule incoming.
     *
     * @return the nbScheduleIncoming
     */
    public int getNbScheduleIncoming() {
        return nbScheduleIncoming;
    }

    /**
     * Sets the nb schedule incoming.
     *
     * @param nbScheduleIncoming the nbScheduleIncoming to set
     */
    public void setNbScheduleIncoming(int nbScheduleIncoming) {
        this.nbScheduleIncoming = nbScheduleIncoming;
    }

    /**
     * Gets the sum amount paid.
     *
     * @return the sumAmountPaid
     */
    public BigDecimal getSumAmountPaid() {
        return sumAmountPaid;
    }

    /**
     * Sets the sum amount paid.
     *
     * @param sumAmountPaid the sumAmountPaid to set
     */
    public void setSumAmountPaid(BigDecimal sumAmountPaid) {
        this.sumAmountPaid = sumAmountPaid;
    }

    /**
     * Gets the sum amount incoming.
     *
     * @return the sumAmountIncoming
     */
    public BigDecimal getSumAmountIncoming() {
        return sumAmountIncoming;
    }

    /**
     * Sets the sum amount incoming.
     *
     * @param sumAmountIncoming the sumAmountIncoming to set
     */
    public void setSumAmountIncoming(BigDecimal sumAmountIncoming) {
        this.sumAmountIncoming = sumAmountIncoming;
    }

}
