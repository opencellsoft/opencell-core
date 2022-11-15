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

package org.meveo.api.dto.tunnel;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.subscriptionTunnel.CustomStyle;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Ilham CHAFIK
 */
@XmlRootElement(name = "Theme")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThemeDto extends BusinessEntityDto {

    /** serial version uid. */
    private static final long serialVersionUID = 475958204852585405L;

    private CustomStyleDto header;

    private CustomStyleDto body;

    private CustomStyleDto footer;

    private Date createdOn;

    public CustomStyleDto getHeader() {
        return header;
    }

    public void setHeader(CustomStyleDto header) {
        this.header = header;
    }

    public CustomStyleDto getBody() {
        return body;
    }

    public void setBody(CustomStyleDto body) {
        this.body = body;
    }

    public CustomStyleDto getFooter() {
        return footer;
    }

    public void setFooter(CustomStyleDto footer) {
        this.footer = footer;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
