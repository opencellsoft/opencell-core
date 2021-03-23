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

import org.meveo.api.dto.LanguageIsoDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GetLanguagesIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 */
@XmlRootElement(name = "GetLanguagesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguagesIsoResponse extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1697478352703038101L;

    /** The languages. */
    private List<LanguageIsoDto> languages = new ArrayList<>();

    /**
     * Instantiates a new gets the languages iso response.
     */
    public GetLanguagesIsoResponse() {
        super();
    }

    /**
     * Gets the languages.
     *
     * @return the languages
     */
    public List<LanguageIsoDto> getLanguages() {
        return languages;
    }

    /**
     * Sets the languages.
     *
     * @param languages the new languages
     */
    public void setLanguages(List<LanguageIsoDto> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "GetLanguagesIsoResponse [languages=" + languages + "]";
    }
}