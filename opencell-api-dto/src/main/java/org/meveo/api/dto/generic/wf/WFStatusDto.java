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
package org.meveo.api.dto.generic.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.generic.wf.WFStatus;

/**
 * The Class WFStatusDto
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WFStatusDto extends BusinessEntityDto {

    private static final long serialVersionUID = 6770111692852172654L;

    /** The uuid. */
    @XmlElement(required = false)
    private String uuid;

    public WFStatusDto() {
        super();
    }

    public WFStatusDto(String code, String description) {
        super();
        this.code = code;
        this.description = description;
    }

    public WFStatusDto(WFStatus wfStatus) {
        super(wfStatus);
        this.uuid = wfStatus.getUuid();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "WFStatusDto [uuid=" + uuid + ", id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + "]";
    }

    public WFStatus toWFStatus() {
        WFStatus wfStatus = new WFStatus();
        if (!StringUtils.isBlank(uuid)) {
            wfStatus.setUuid(uuid);
        }
        wfStatus.setId(id);
        wfStatus.setCode(code);
        wfStatus.setDescription(description);

        return wfStatus;
    }
}
