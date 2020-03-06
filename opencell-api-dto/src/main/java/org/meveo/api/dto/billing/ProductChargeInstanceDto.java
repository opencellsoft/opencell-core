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

import java.math.BigDecimal;

import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;

/**
 * The Class ProductChargeInstanceDto.
 *
 * @author Edward P. Legaspi
 */
public class ProductChargeInstanceDto extends ChargeInstanceDto {

    private static final long serialVersionUID = 2675370510322125454L;

    /** The quantity. */
    protected BigDecimal quantity = BigDecimal.ONE;

    public ProductChargeInstanceDto() {
        super();
    }

    /**
     * Instantiates a new product charge instance dto.
     *
     * @param productChargeInstance the ProductChargeInstance entity
     */
    public ProductChargeInstanceDto(ProductChargeInstance productChargeInstance) {
        super((ChargeInstance) productChargeInstance);
        if (productChargeInstance != null) {
            quantity = productChargeInstance.getQuantity();
        }
    }

}