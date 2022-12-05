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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class MeveoInstanceResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 7:08:28 AM
 */
@XmlRootElement(name = "MeveoInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9151837082569910954L;
    
    /** The meveo instance. */
    private MeveoInstanceDto meveoInstance;

    /**
     * Gets the meveo instance.
     *
     * @return the meveo instance
     */
    public MeveoInstanceDto getMeveoInstance() {
        return meveoInstance;
    }

    /**
     * Sets the meveo instance.
     *
     * @param meveoInstance the new meveo instance
     */
    public void setMeveoInstance(MeveoInstanceDto meveoInstance) {
        this.meveoInstance = meveoInstance;
    }
}
