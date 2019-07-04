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