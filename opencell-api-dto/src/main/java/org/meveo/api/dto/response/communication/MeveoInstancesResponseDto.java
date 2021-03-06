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

package org.meveo.api.dto.response.communication;

import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * The Class MeveoInstancesResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 */
@XmlRootElement(name = "MeveoInstancesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstancesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5630363416438814136L;

    /** The meveo instances. */
    @XmlElementWrapper(name = "meveoInstances")
    @XmlElement(name = "meveoInstance")
    private List<MeveoInstanceDto> meveoInstances;

    /**
     * Gets the meveo instances.
     *
     * @return the meveo instances
     */
    public List<MeveoInstanceDto> getMeveoInstances() {
        return meveoInstances;
    }

    /**
     * Sets the meveo instances.
     *
     * @param meveoInstances the new meveo instances
     */
    public void setMeveoInstances(List<MeveoInstanceDto> meveoInstances) {
        this.meveoInstances = meveoInstances;
    }

}
