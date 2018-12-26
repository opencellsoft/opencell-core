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
