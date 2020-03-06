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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class InvoiceCategoryDTO {
    private String description;
    private String code;
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;
    private BigDecimal amountWithTax = BigDecimal.ZERO;
    LinkedHashMap<String, InvoiceSubCategoryDTO> invoiceSubCategoryDTOMap = new LinkedHashMap<String, InvoiceSubCategoryDTO>();

    private int rounding;
    private RoundingMode roundingMode;

    /**
     * Instantiates a new invoice category DTO with rounding and roundingMode config values.
     *
     * @param rounding the rounding
     * @param roundingMode the rounding mode
     */
    public InvoiceCategoryDTO(Integer rounding, RoundingMode roundingMode) {
        this.rounding = rounding != null ? rounding : 2;
        this.roundingMode = roundingMode != null ? roundingMode : RoundingMode.HALF_UP;
    }

    /**
     * Instantiates a new invoice category DTO with default rounding and roundingMode values.
     */
    public InvoiceCategoryDTO() {
        this(2, RoundingMode.HALF_UP);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmountWithoutTax() {
        return this.amountWithoutTax.setScale(this.rounding, this.roundingMode);
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public LinkedHashMap<String, InvoiceSubCategoryDTO> getInvoiceSubCategoryDTOMap() {
        return this.invoiceSubCategoryDTOMap;
    }

    public void setInvoiceSubCategoryDTOMap(LinkedHashMap<String, InvoiceSubCategoryDTO> invoiceSubCategoryDTOMap) {
        this.invoiceSubCategoryDTOMap = invoiceSubCategoryDTOMap;
    }

    public List<InvoiceSubCategoryDTO> getInvoiceSubCategoryDTOList() {
        return new ArrayList<InvoiceSubCategoryDTO>(this.invoiceSubCategoryDTOMap.values());
    }

    public BigDecimal getAmountWithTax() {
        return this.amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public void addAmountWithTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (this.amountWithTax == null) {
                this.amountWithTax = new BigDecimal("0");
            }
            this.amountWithTax = this.amountWithTax.add(amountToAdd);
        }
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (this.amountWithoutTax == null) {
            this.amountWithoutTax = new BigDecimal("0");
        }
        this.amountWithoutTax = this.amountWithoutTax.add(amountToAdd);
    }
}
