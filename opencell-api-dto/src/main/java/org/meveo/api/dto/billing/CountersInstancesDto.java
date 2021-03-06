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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class CountersInstancesDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CountersInstancesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 49018302870831847L;

    /** The counter instance. */
    private List<CounterInstanceDto> counterInstance;

    /**
     * Gets the counter instance.
     *
     * @return the counter instance
     */
    public List<CounterInstanceDto> getCounterInstance() {
        if (counterInstance == null) {
            counterInstance = new ArrayList<CounterInstanceDto>();
        }

        return counterInstance;
    }

    /**
     * Sets the counter instance.
     *
     * @param counterInstance the new counter instance
     */
    public void setCounterInstance(List<CounterInstanceDto> counterInstance) {
        this.counterInstance = counterInstance;
    }

    @Override
    public String toString() {
        return "CountersInstancesDto [counterInstance=" + counterInstance + "]";
    }

}