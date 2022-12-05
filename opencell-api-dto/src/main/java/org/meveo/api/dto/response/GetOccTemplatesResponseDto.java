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

package org.meveo.api.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.OccTemplatesDto;

/**
 * The Class GetOccTemplatesResponseDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "GetOccTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOccTemplatesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4612709775410582280L;

    /** The occ templates. */
    private OccTemplatesDto occTemplates;

    /**
     * Gets the occ templates.
     *
     * @return the occ templates
     */
    public OccTemplatesDto getOccTemplates() {
        if (occTemplates == null) {
            occTemplates = new OccTemplatesDto();
        }

        return occTemplates;
    }

    /**
     * Sets the occ templates.
     *
     * @param occTemplates the new occ templates
     */
    public void setOccTemplates(OccTemplatesDto occTemplates) {
        this.occTemplates = occTemplates;
    }

    @Override
    public String toString() {
        return "GetOccTemplatesResponse [occTemplates=" + occTemplates + ", toString()=" + super.toString() + "]";
    }
}