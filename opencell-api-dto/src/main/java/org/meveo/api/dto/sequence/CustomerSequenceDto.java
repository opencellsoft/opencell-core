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

package org.meveo.api.dto.sequence;

import jakarta.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Customer Sequence value DTO representation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
public class CustomerSequenceDto extends BusinessEntityDto {

	private static final long serialVersionUID = 7614738733647971026L;

	private GenericSequenceDto genericSequence;

	/** Code of the seller */
	@NotNull
    @Schema(description = "Code of the seller")
	private String seller;

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public GenericSequenceDto getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequenceDto genericSequence) {
		this.genericSequence = genericSequence;
	}

}
