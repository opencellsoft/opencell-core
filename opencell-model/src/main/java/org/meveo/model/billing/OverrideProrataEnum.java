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

package org.meveo.model.billing;

/**
 * An Enum to override prorata setting in the recurring charge when terminating a subscription
 *
 * @author Horri khalid
 */
public enum OverrideProrataEnum {
    /**
     * Keeps the proata setting set on the recurring charge
     */
    NO_OVERRIDE,

    /**
     * Always prorate
     */
    PRORATA,

    /**
     * Never prorate
     */
    NO_PRORATA;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    /**
     * Determine if prorate should occur based on current enum value
     * 
     * @param defaultProrate Default prorate value in case directive is not to override
     * @return True if proration should occur
     */
    public boolean isToProrate(boolean defaultProrate) {
        switch (this) {
        case NO_OVERRIDE:
            return defaultProrate;
        case PRORATA:
            return true;
        case NO_PRORATA:
            return false;
        default:
            return defaultProrate;
        }
    }

}