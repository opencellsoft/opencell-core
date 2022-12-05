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

package org.meveo.api.dto.response.payment;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class DDRequestLotOpsResponseDto.
 *
 * @author TyshanaShi(tyshan@manaty.net)
 */
@XmlRootElement(name = "DDRequestLotOpsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 317999006133708067L;

    /** The ddrequest lot ops. */
    @XmlElementWrapper(name = "ddrequestLotOps")
    @XmlElement(name = "ddrequestLotOp")
    private List<DDRequestLotOpDto> ddrequestLotOps;

    /**
     * Gets the ddrequest lot ops.
     *
     * @return the ddrequest lot ops
     */
    public List<DDRequestLotOpDto> getDdrequestLotOps() {
        return ddrequestLotOps;
    }

    /**
     * Sets the ddrequest lot ops.
     *
     * @param ddrequestLotOps the new ddrequest lot ops
     */
    public void setDdrequestLotOps(List<DDRequestLotOpDto> ddrequestLotOps) {
        this.ddrequestLotOps = ddrequestLotOps;
    }

}
