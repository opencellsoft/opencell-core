package org.meveo.api.dto.payment;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

/**
 * RUM Sequence value DTO representation.
 * 
 * @author Edward P. Legaspi
 * @LastModifiedVersion 5.2
 */
public class RumSequenceDto implements Serializable {

	private static final long serialVersionUID = 3562570307981875698L;

	/**
	 * Prefix of RUM.
	 */
	@Pattern(regexp = "^[\\p{Upper}-]{1,16}$")
	private String prefix = "";
	
	/**
	 * Size of the sequence. Maximum allowable for RUM is 35. That means 35 - prefix.length.
	 */
	private Long sequenceSize;
	
	/**
	 * Current value of the sequence.
	 */
	private Long currentSequenceNb = 0L;

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
