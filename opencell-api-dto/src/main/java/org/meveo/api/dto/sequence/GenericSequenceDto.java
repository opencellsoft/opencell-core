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

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.meveo.model.sequence.GenericSequence;

/**
 * Sequence value DTO representation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class GenericSequenceDto implements Serializable {

	private static final long serialVersionUID = 3562570307981875698L;

	/**
	 * Prefix of sequence.
	 */
	@Pattern(regexp = "^[\\p{Upper}-]{1,16}$")
	private String prefix = "";

	/**
	 * Size of the sequence. Maximum allowable for RUM is 35. That means 35 -
	 * prefix.length.
	 */
	private Long sequenceSize;

	/**
	 * Current value of the sequence.
	 */
	private Long currentSequenceNb = 0L;

	public GenericSequenceDto() {

	}

	public GenericSequenceDto(GenericSequence sequence) {
		prefix = sequence.getPrefix();
		sequenceSize = sequence.getSequenceSize();
		currentSequenceNb = sequence.getCurrentSequenceNb();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getSequenceSize() {
		return sequenceSize;
	}

	public void setSequenceSize(Long sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	public Long getCurrentSequenceNb() {
		return currentSequenceNb;
	}

	public void setCurrentSequenceNb(Long currentSequenceNb) {
		this.currentSequenceNb = currentSequenceNb;
	}

}
