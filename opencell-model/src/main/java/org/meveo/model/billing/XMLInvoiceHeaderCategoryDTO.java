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

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLInvoiceHeaderCategoryDTO {

    private String description;
    private String code;
    private BigDecimal amountWithoutTax = ZERO;
    private BigDecimal amountWithTax = ZERO;
    private BigDecimal amountTax = ZERO;
    private BigDecimal transactionalAmountWithoutTax = ZERO;
    private BigDecimal transactionalAmountWithTax = ZERO;
    private BigDecimal transactionalAmountTax = ZERO;
    private Integer sortIndex;

    private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>();

    private Map<Long, RatedTransaction> ratedtransactions = new HashMap<>();

    public Map<Long, RatedTransaction> getRatedtransactions() {
        return ratedtransactions;
    }

    public void setRatedtransactions(Map<Long, RatedTransaction> ratedtransactions) {
        this.ratedtransactions = ratedtransactions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public void addAmountWithTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (amountWithTax == null) {
                amountWithTax = new BigDecimal("0");
            }
            amountWithTax = amountWithTax.add(amountToAdd);
        }
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (amountWithoutTax == null) {
            amountWithoutTax = new BigDecimal("0");
        }
        amountWithoutTax = amountWithoutTax.add(amountToAdd);
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (amountTax == null) {
                amountTax = new BigDecimal("0");
            }
            amountTax = amountTax.add(amountToAdd);
        }
    }

    public void addTransactionalAmountWithTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (transactionalAmountWithTax == null) {
                transactionalAmountWithTax = new BigDecimal("0");
            }
            transactionalAmountWithTax = transactionalAmountWithTax.add(amountToAdd);
        }
    }

    public void addTransactionalAmountWithoutTax(BigDecimal amountToAdd) {
        if (transactionalAmountWithoutTax == null) {
            transactionalAmountWithoutTax = new BigDecimal("0");
        }
        transactionalAmountWithoutTax = transactionalAmountWithoutTax.add(amountToAdd);
    }

    public void addTransactionalAmountTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (transactionalAmountTax == null) {
                transactionalAmountTax = new BigDecimal("0");
            }
            transactionalAmountTax = transactionalAmountTax.add(amountToAdd);
        }
    }

    public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
        return subCategoryInvoiceAgregates;
    }

    public void setSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
        this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public BigDecimal getTransactionalAmountWithoutTax() {
        return transactionalAmountWithoutTax;
    }

    public void setTransactionalAmountWithoutTax(BigDecimal transactionalAmountWithoutTax) {
        this.transactionalAmountWithoutTax = transactionalAmountWithoutTax;
    }

    public BigDecimal getTransactionalAmountWithTax() {
        return transactionalAmountWithTax;
    }

    public void setTransactionalAmountWithTax(BigDecimal transactionalAmountWithTax) {
        this.transactionalAmountWithTax = transactionalAmountWithTax;
    }

    public BigDecimal getTransactionalAmountTax() {
        return transactionalAmountTax;
    }

    public void setTransactionalAmountTax(BigDecimal transactionalAmountTax) {
        this.transactionalAmountTax = transactionalAmountTax;
    }
}
