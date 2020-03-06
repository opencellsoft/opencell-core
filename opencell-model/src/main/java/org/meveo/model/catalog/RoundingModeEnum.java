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

package org.meveo.model.catalog;

import java.math.RoundingMode;

/**
 * BigDecimal value rounding mode
 * 
 * @author Andrius Karpavicius
 */
public enum RoundingModeEnum {

    /**
     * The common rounding. Value of &gt;=0.5 is rounded up.
     * 
     * See java.math.RoundingMode.HALF_UP
     */
    NEAREST(RoundingMode.HALF_UP),

    /**
     * If the value is positive, behave as for RoundingMode.DOWN; if negative, behave as for RoundingMode.UP. Note that this rounding mode never increases the calculated value.
     * 
     * See java.math.RoundingMode.FLOOR.
     */
    DOWN(RoundingMode.FLOOR),

    /**
     * If the value is positive, behaves as for RoundingMode.UP; if negative, behaves as for RoundingMode.DOWN. Note that this rounding mode never decreases the calculated value.
     * 
     * See java.math.RoundingMode.CEILING.
     */
    UP(RoundingMode.CEILING);

    /**
     * Rounding mode
     */
    private RoundingMode roundingMode;

    private RoundingModeEnum(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    /**
     * @return Label
     */
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    /**
     * Get BigDecimal value rounding mode, as JAVA understands it
     * 
     * @return BigDecimal value rounding mode in JAVA terms
     */
    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
}