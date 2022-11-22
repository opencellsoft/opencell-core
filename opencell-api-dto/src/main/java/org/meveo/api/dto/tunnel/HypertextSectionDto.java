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
import org.meveo.model.subscriptionTunnel.HypertextLink;
import org.meveo.model.subscriptionTunnel.HypertextSection;

import java.util.List;

/**
 * @author Ilham CHAFIK
 */
public class HypertextSectionDto extends BusinessEntityDto {

    /** serial version uid. */
    private static final long serialVersionUID = -6153600300132384523L;

    private List<LanguageDescriptionDto> label;

    private List<HypertextLinkDto> links;

    private String customStyleCode;

    public HypertextSectionDto() {
    }

    public HypertextSectionDto(HypertextSection section) {
        code = section.getCode();
        id = section.getId();

        if (section.getLabel() != null) {
            label = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(section.getLabel());
        }
        if (section.getLinks() != null) {
            for (HypertextLink link: section.getLinks()) {
                links.add(new HypertextLinkDto(link));
            }
        }
    }

    public HypertextSectionDto(List<LanguageDescriptionDto> label, List<HypertextLinkDto> links, String customStyleCode) {
        this.label = label;
        this.links = links;
        this.customStyleCode = customStyleCode;
    }

    public List<LanguageDescriptionDto> getLabel() {
        return label;
    }

    public void setLabel(List<LanguageDescriptionDto> label) {
        this.label = label;
    }

    public List<HypertextLinkDto> getLinks() {
        return links;
    }

    public void setLinks(List<HypertextLinkDto> links) {
        this.links = links;
    }

    public String getCustomStyleCode() {
        return customStyleCode;
    }

    public void setCustomStyleCode(String customStyleCode) {
        this.customStyleCode = customStyleCode;
    }
}
