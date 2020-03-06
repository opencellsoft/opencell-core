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

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Language;

/**
 * The Class LanguageIsoDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "LanguageIso")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageIsoDto extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 725968016559888810L;

	/** The code. */
	@XmlAttribute(required = true)
	private String code;
	
	/** The description. */
	private String description;

	/**
     * Instantiates a new language iso dto.
     */
	public LanguageIsoDto() {

	}


	/**
     * Instantiates a new language iso dto.
     *
     * @param language the language
     */
	public LanguageIsoDto(Language language) {
		code = language.getLanguageCode();
		description = language.getDescriptionEn();
	}

	/**
     * Gets the code.
     *
     * @return the code
     */
	public String getCode() {
		return code;
	}

	/**
     * Sets the code.
     *
     * @param code the new code
     */
	public void setCode(String code) {
		this.code = code;
	}

	/**
     * Gets the description.
     *
     * @return the description
     */
	public String getDescription() {
		return description;
	}

	/**
     * Sets the description.
     *
     * @param description the new description
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LanguageIsoDto [code=" + code + ", description=" + description + "]";
	}

}
