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
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.tunnel.HypertextLink;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Ilham CHAFIK
 */
@XmlRootElement(name = "HypertextLink")
@XmlAccessorType(XmlAccessType.FIELD)
public class HypertextLinkDto extends BusinessEntityDto {

    private static final long serialVersionUID = 4346589020131903781L;

    private List<LanguageDescriptionDto> label;

    private List<LanguageDescriptionDto> url;

    private String icon;

    private Boolean displayIcon;

    private String hypertextSectionCode;

    public HypertextLinkDto() {
    }

    public HypertextLinkDto(HypertextLink link) {
        code = link.getCode();
        id = link.getId();

        if (link.getLabel() != null) {
            label = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(link.getLabel());
        }
        if (link.getUrl() != null) {
            url = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(link.getUrl());
        }
        if (link.getIcon() != null) {
            icon = link.getIcon();
        }
        if (link.getDisplayIcon() != null) {
            displayIcon = link.getDisplayIcon();
        }
        if (link.getHypertextSection() != null) {
            hypertextSectionCode = link.getHypertextSection().getCode();
        }
    }

    public HypertextLinkDto(List<LanguageDescriptionDto> label, List<LanguageDescriptionDto> url, String icon, Boolean displayIcon, String hypertextSectionCode) {
        this.label = label;
        this.url = url;
        this.icon = icon;
        this.displayIcon = displayIcon;
        this.hypertextSectionCode = hypertextSectionCode;
    }

    public List<LanguageDescriptionDto> getLabel() {
        return label;
    }

    public void setLabel(List<LanguageDescriptionDto> label) {
        this.label = label;
    }

    public List<LanguageDescriptionDto> getUrl() {
        return url;
    }

    public void setUrl(List<LanguageDescriptionDto> url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getDisplayIcon() {
        return displayIcon;
    }

    public void setDisplayIcon(Boolean displayIcon) {
        this.displayIcon = displayIcon;
    }

    public String getHypertextSectionCode() {
        return hypertextSectionCode;
    }

    public void setHypertextSectionCode(String hypertextSectionCode) {
        this.hypertextSectionCode = hypertextSectionCode;
    }
}
