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

package org.meveo.api.dto.filter;

import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class FilteredListDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "FilteredList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilteredListDto {

    /** The xml input. */
    private String xmlInput;
    
    /** The parameters. */
    private Map<String, String> parameters;
    
    /** The first row. */
    private int firstRow;
    
    /** The number of rows. */
    private int numberOfRows;

    /**
     * Gets the xml input.
     *
     * @return the xml input
     */
    public String getXmlInput() {
        return xmlInput;
    }

    /**
     * Sets the xml input.
     *
     * @param xmlInput the new xml input
     */
    public void setXmlInput(String xmlInput) {
        this.xmlInput = xmlInput;
    }

    /**
     * Gets the first row.
     *
     * @return the first row
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Sets the first row.
     *
     * @param firstRow the new first row
     */
    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Gets the number of rows.
     *
     * @return the number of rows
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Sets the number of rows.
     *
     * @param numberOfRows the new number of rows
     */
    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}