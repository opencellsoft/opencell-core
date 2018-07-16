package org.meveo.model.payments;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Sequence class use when generating RUM.
 * 
 * @author Edward P. Legaspi
 * @LastModifiedVersion 5.2
 */
@Embeddable
public class RumSequence implements Serializable {

	private static final long serialVersionUID = -1964277428044516118L;

	/**
	 * Prefix of RUM.
	 */
	@Pattern(regexp = "^[\\p{Upper}-]{1,16}$")
	@Column(name = "rum_prefix", length = 15)
	@Size(max = 15)
	private String prefix = "";

	/**
	 * Size of the sequence. Maximum allowable for RUM is 35. That means 35 -
	 * prefix.length.
	 */
	@Column(name = "rum_sequence_size")
	private Long sequenceSize = 20L;

	/**
	 * Current value of the sequence.
	 */
	@Column(name = "rum_current_sequence_nb")
	private Long currentSequenceNb = 0L;

	public RumSequence() {

	}

	public RumSequence(String prefix, Long sequenceSize, Long currentSequenceNb) {
		super();
		this.prefix = prefix;
		this.sequenceSize = sequenceSize;
		this.currentSequenceNb = currentSequenceNb;
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
