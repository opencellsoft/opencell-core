/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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