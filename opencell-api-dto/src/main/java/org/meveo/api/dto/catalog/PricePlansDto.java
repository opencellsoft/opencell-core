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

package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class PricePlansDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlansDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4354099345909112263L;

    /** The price plan matrix. */
    private List<PricePlanMatrixDto> pricePlanMatrix = new ArrayList<>();

    /**
     * Gets the price plan matrix.
     *
     * @return the price plan matrix
     */
    public List<PricePlanMatrixDto> getPricePlanMatrix() {
        return pricePlanMatrix;
    }

    /**
     * Sets the price plan matrix.
     *
     * @param pricePlanMatrix the new price plan matrix
     */
    public void setPricePlanMatrix(List<PricePlanMatrixDto> pricePlanMatrix) {
        this.pricePlanMatrix = pricePlanMatrix;
    }

    @Override
    public String toString() {
        return "PricePlansDto [pricePlanMatrix=" + pricePlanMatrix + "]";
    }

}