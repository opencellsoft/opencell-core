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

package org.meveo.api.sequence;

import jakarta.ejb.Stateless;

import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.model.sequence.GenericSequence;

/**
 * Class for converting the GenericSequence dto to entity and vice versa.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class GenericSequenceApi {

	public static GenericSequence toGenericSequence(GenericSequenceDto source, GenericSequence target) {
		if (target == null) {
			target = new GenericSequence();
		}
		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());

		return target;
	}

	public static GenericSequenceDto fromGenericSequence(GenericSequence source) {
		GenericSequenceDto target = new GenericSequenceDto();

		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());
		if (source.getCurrentSequenceNb() != null) {
			target.setCurrentSequenceNb(source.getCurrentSequenceNb());
		}

		return target;
	}

}
