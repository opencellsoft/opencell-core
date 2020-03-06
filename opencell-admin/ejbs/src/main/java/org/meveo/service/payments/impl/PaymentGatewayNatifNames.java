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

/**
 * 
 */
package org.meveo.service.payments.impl;

/**
 * The Enum PaymentGatewayNatifNames.
 *
 * @author anasseh
 */
public enum PaymentGatewayNatifNames {

    /** The ingenico gc. */
    INGENICO_GC("org.meveo.service.payments.impl.IngenicoGatewayPayment"),

    /** The sepa. */
    SEPA("org.meveo.admin.sepa.SepaFile"),

    /** The paynum. */
    PAYNUM("org.meveo.admin.sepa.PaynumFile");

    /** The class name. */
    private String className;

    /**
     * Instantiates a new payment gateway natif names.
     *
     * @param className the class name
     */
    PaymentGatewayNatifNames(String className) {
        this.className = className;
    }

    /**
     * Gets the class name.
     *
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name.
     *
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

}
