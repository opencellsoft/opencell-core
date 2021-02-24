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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Contains amounts with and without tax
 * 
 * @author Andrius Karpavicius
 * @since 5.0.1
 */
public class Amounts implements Serializable, Cloneable {

    private static final long serialVersionUID = 9184599956127715623L;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax = BigDecimal.ZERO;

    /**
     * Tax amount
     */
    private BigDecimal amountTax = BigDecimal.ZERO;

    /**
     * Tax applied
     */
    private Tax tax;

    /**
     * Instantiate
     */
    public Amounts() {
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     */
    public Amounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
        this(amountWithoutTax, amountWithTax, null);
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Tax amount
     */
    public Amounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax) {
        super();

        if (amountWithoutTax == null) {
            this.amountWithoutTax = BigDecimal.ZERO;
        } else {
            this.amountWithoutTax = amountWithoutTax;
        }

        if (amountWithTax == null) {
            this.amountWithTax = BigDecimal.ZERO;
        } else {
            this.amountWithTax = amountWithTax;
        }

        if (amountTax == null) {
            this.amountTax = BigDecimal.ZERO;
        } else {
            this.amountTax = amountTax;
        }
    }

    /**
     * Instantiate with given amounts
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Tax amount
     * @param tax Tax applied
     */
    public Amounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, Tax tax) {
        this(amountWithoutTax, amountWithTax, amountTax);
        this.tax = tax;
    }

    /**
     * @return Amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * @param amountWithTax Amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * @return Amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax Amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * @return Tax amount
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * @param amountTax Tax amount
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Get one of the amounts requested
     * 
     * @param isWithTax Should return amount with tax
     * @return Amount with tax if isWithTax=true or amount without tax if isWithTax=false
     */
    public BigDecimal getAmount(boolean isWithTax) {
        if (isWithTax) {
            return amountWithTax;
        } else {
            return amountWithoutTax;
        }
    }

    /**
     * @return Tax applied
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * @param tax Tax applied
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "amountWithTax=" + amountWithTax + ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax;
    }

    /**
     * Add given amounts
     * 
     * @param amountWithoutTaxToAdd Amount without tax
     * @param amountWithTaxToAdd Amount with tax
     */
    public void addAmounts(BigDecimal amountWithoutTaxToAdd, BigDecimal amountWithTaxToAdd, BigDecimal amountTaxToAdd) {

        if (amountWithoutTaxToAdd != null) {
            this.amountWithoutTax = this.amountWithoutTax.add(amountWithoutTaxToAdd);
        }

        if (amountWithTaxToAdd != null) {
            this.amountWithTax = this.amountWithTax.add(amountWithTaxToAdd);
        }

        if (amountTaxToAdd != null) {
            this.amountTax = this.amountTax.add(amountTaxToAdd);
        }
    }

    /**
     * Add given amounts
     * 
     * @param amountsToAdd Amounts to add
     */
    public void addAmounts(Amounts amountsToAdd) {
        if (amountsToAdd == null) {
            return;
        }
        this.amountWithoutTax = this.amountWithoutTax.add(amountsToAdd.getAmountWithoutTax());
        this.amountWithTax = this.amountWithTax.add(amountsToAdd.getAmountWithTax());
        this.amountTax = this.amountTax.add(amountsToAdd.getAmountTax());
    }

    /**
     * Calculate derived amounts: amount with or without tax depending if its enterprise configuration based on other two amounts
     * 
     * @param isEnterprise If true, will recalculate amountWithTax as in amountWithoutTax - amountTax. If false will recalculate amountWithoutTax as in amountWithTax - amountTax
     */
    public void calculateDerivedAmounts(boolean isEnterprise) {
        if (isEnterprise) {
            amountWithTax = amountWithoutTax.add(amountTax);
        } else {
            amountWithoutTax = amountWithTax.subtract(amountTax);
        }
    }

    /**
     * Get a copy of the object
     *
     * @return A copy of an object
     */
    @Override
    public Amounts clone() {
        return new Amounts(amountWithoutTax, amountWithTax, amountTax, tax);
    }

    /**
     * Negate amounts.
     *
     * @return a nagated amounts
     */
    public Amounts negate() {
        this.amountWithoutTax = this.amountWithoutTax.negate();
        this.amountWithTax = this.amountWithTax.negate();
        this.amountTax = this.amountTax.negate();
        return this;
    }
}