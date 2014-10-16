/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.service.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ConsumptionDTO implements Serializable {

    private static final long serialVersionUID = -7172755093920198263L;

    private BigDecimal amountCharged = BigDecimal.ZERO;

    private BigDecimal amountUncharged = BigDecimal.ZERO;

    private Integer consumptionCharged = 0;

    private Integer consumptionUncharged = 0;

    private Integer incomingNationalConsumptionUncharged = 0;

    private Integer incomingNationalConsumptionCharged = 0;

    private Integer outgoingNationalConsumptionUncharged = 0;

    private Integer outgoingNationalConsumptionCharged = 0;

    private Integer incomingRoamingConsumptionUncharged = 0;

    private Integer incomingRoamingConsumptionCharged = 0;

    private Integer outgoingRoamingConsumptionUncharged = 0;

    private Integer outgoingRoamingConsumptionCharged = 0;

    public BigDecimal getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(BigDecimal AmountCharged) {
        this.amountCharged = AmountCharged;
    }

    public Integer getConsumptionCharged() {
        return consumptionCharged;
    }

    public void setConsumptionCharged(Integer ConsumptionCharged) {
        this.consumptionCharged = ConsumptionCharged;
    }

    public Integer getIncomingNationalConsumptionUncharged() {
        return incomingNationalConsumptionUncharged;
    }

    public void setIncomingNationalConsumptionUncharged(Integer incomingNationalConsumptionUncharged) {
        this.incomingNationalConsumptionUncharged = incomingNationalConsumptionUncharged;
    }

    public Integer getIncomingNationalConsumptionCharged() {
        return incomingNationalConsumptionCharged;
    }

    public void setIncomingNationalConsumptionCharged(Integer incomingNationalConsumptionCharged) {
        this.incomingNationalConsumptionCharged = incomingNationalConsumptionCharged;
    }

    public Integer getOutgoingNationalConsumptionUncharged() {
        return outgoingNationalConsumptionUncharged;
    }

    public void setOutgoingNationalConsumptionUncharged(Integer outgoingNationalConsumptionUncharged) {
        this.outgoingNationalConsumptionUncharged = outgoingNationalConsumptionUncharged;
    }

    public Integer getOutgoingNationalConsumptionCharged() {
        return outgoingNationalConsumptionCharged;
    }

    public void setOutgoingNationalConsumptionCharged(Integer outgoingNationalConsumptionCharged) {
        this.outgoingNationalConsumptionCharged = outgoingNationalConsumptionCharged;
    }

    public Integer getIncomingRoamingConsumptionUncharged() {
        return incomingRoamingConsumptionUncharged;
    }

    public void setIncomingRoamingConsumptionUncharged(Integer incomingRoamingConsumptionUncharged) {
        this.incomingRoamingConsumptionUncharged = incomingRoamingConsumptionUncharged;
    }

    public Integer getIncomingRoamingConsumptionCharged() {
        return incomingRoamingConsumptionCharged;
    }

    public void setIncomingRoamingConsumptionCharged(Integer incomingRoamingConsumptionCharged) {
        this.incomingRoamingConsumptionCharged = incomingRoamingConsumptionCharged;
    }

    public Integer getOutgoingRoamingConsumptionUncharged() {
        return outgoingRoamingConsumptionUncharged;
    }

    public void setOutgoingRoamingConsumptionUncharged(Integer outgoingRoamingConsumptionUncharged) {
        this.outgoingRoamingConsumptionUncharged = outgoingRoamingConsumptionUncharged;
    }

    public Integer getOutgoingRoamingConsumptionCharged() {
        return outgoingRoamingConsumptionCharged;
    }

    public void setOutgoingRoamingConsumptionCharged(Integer outgoingRoamingConsumptionCharged) {
        this.outgoingRoamingConsumptionCharged = outgoingRoamingConsumptionCharged;
    }

    public BigDecimal getAmountUncharged() {
        return amountUncharged;
    }

    public void setAmountUncharged(BigDecimal amountUncharged) {
        this.amountUncharged = amountUncharged;
    }

    public Integer getConsumptionUncharged() {
        return consumptionUncharged;
    }

    public void setConsumptionUncharged(Integer consumptionUncharged) {
        this.consumptionUncharged = consumptionUncharged;
    }
}