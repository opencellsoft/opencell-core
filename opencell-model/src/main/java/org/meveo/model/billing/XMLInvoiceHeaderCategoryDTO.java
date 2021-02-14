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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLInvoiceHeaderCategoryDTO {

    private String description;
    private String code;
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;
    private BigDecimal amountWithTax = BigDecimal.ZERO;
    private BigDecimal amountTax = BigDecimal.ZERO;
    private Integer sortIndex;

    private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>();

    private Map<Long, RatedTransaction> ratedtransactions = new HashMap<Long, RatedTransaction>();

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
}
