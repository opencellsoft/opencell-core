package org.tmf.dsmapi.commons;

import java.math.BigDecimal;

/**
 *
 * @author bahman.barzideh
 *
 */
public class OutputUtilities {

    private OutputUtilities() {
    }

    public static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

}
