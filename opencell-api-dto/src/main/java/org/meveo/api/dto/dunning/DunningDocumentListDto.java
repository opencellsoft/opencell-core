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

package org.meveo.api.dto.dunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * The Class DunningDocumentListDto.
 * 
 * @author abdelmounaim akadid
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningDocumentListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4086241876387501134L;

    /** The list size. */
    private int listSize;

    /** The DunningDocument. */
    private List<DunningDocumentDto> dunningDocuments;

    /**
     * Gets the dunningDocuments.
     *
     * @return the dunningDocuments
     */
    public List<DunningDocumentDto> getDunningDocuments() {
        if (dunningDocuments == null) {
            dunningDocuments = new ArrayList<DunningDocumentDto>();
        }

        return dunningDocuments;
    }

    /**
     * Sets the dunningDocuments.
     *
     * @param dunningDocuments the new dunningDocuments
     */
    public void setDunningDocuments(List<DunningDocumentDto> dunningDocuments) {
        this.dunningDocuments = dunningDocuments;
    }

    /**
     * Gets the list size.
     *
     * @return the list size
     */
    public int getListSize() {
        return listSize;
    }

    /**
     * Sets the list size.
     *
     * @param listSize the new list size
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "DunningDocumentListDto [dunningDocuments=" + dunningDocuments + "]";
    }

}