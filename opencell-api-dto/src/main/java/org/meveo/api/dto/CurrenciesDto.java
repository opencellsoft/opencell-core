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

package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 * The Class CurrenciesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Currencies")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrenciesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7446426816621551014L;

    /** The currency. */
    private List<CurrencyDto> currency;

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public List<CurrencyDto> getCurrency() {
        if (currency == null)
            currency = new ArrayList<CurrencyDto>();
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(List<CurrencyDto> currency) {
        this.currency = currency;
    }

}
