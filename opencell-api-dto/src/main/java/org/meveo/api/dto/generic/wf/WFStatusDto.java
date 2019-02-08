/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto.generic.wf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.generic.wf.WFStatus;

/**
 * The Class GWFTransitionDto
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WFStatusDto extends BusinessEntityDto {

    private static final long serialVersionUID = 6770111692852172654L;

    /** The uuid. */
    @XmlElement(required = false)
    private String uuid;

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
}
