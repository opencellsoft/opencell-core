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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.DueDateDelayEnum;

/**
 * The Class DueDateDelayDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "DueDateDelay")
@XmlAccessorType(XmlAccessType.FIELD)
public class DueDateDelayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8887054188898878461L;

    /** The delay origin. */
    private DueDateDelayEnum delayOrigin;

    /** The computed delay. */
    private int computedDelay;

    /** The delay EL. */
    private String delayEL;

    /**
     * Gets the delay origin.
     *
     * @return the delay origin
     */
    public DueDateDelayEnum getDelayOrigin() {
        return delayOrigin;
    }

    /**
     * Sets the delay origin.
     *
     * @param delayOrigin the new delay origin
     */
    public void setDelayOrigin(DueDateDelayEnum delayOrigin) {
        this.delayOrigin = delayOrigin;
    }

    /**
     * Gets the computed delay.
     *
     * @return the computed delay
     */
    public int getComputedDelay() {
        return computedDelay;
    }

    /**
     * Sets the computed delay.
     *
     * @param computedDelay the new computed delay
     */
    public void setComputedDelay(int computedDelay) {
        this.computedDelay = computedDelay;
    }

    /**
     * Gets the delay EL.
     *
     * @return the delay EL
     */
    public String getDelayEL() {
        return delayEL;
    }

    /**
     * Sets the delay EL.
     *
     * @param delayEL the new delay EL
     */
    public void setDelayEL(String delayEL) {
        this.delayEL = delayEL;
    }

    @Override
    public String toString() {
        return "DueDateDelayDto [delayOrigin=" + delayOrigin + ", computedDelay=" + computedDelay + ", delayEL=" + delayEL + "]";
    }

}