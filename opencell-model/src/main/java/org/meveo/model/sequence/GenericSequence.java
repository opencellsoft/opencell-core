package org.meveo.model.sequence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Class use for storing sequence data. This sequence is use by RUM and to generate customer number.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Embeddable
public class GenericSequence implements Serializable {

    private static final long serialVersionUID = -1964277428044516118L;

    /**
     * Prefix of sequence.
     */
    @Pattern(regexp = "^[\\p{Upper}-]{1,256}$")
    @Column(name = "prefix", length = 255)
    @Size(max = 255)
    private String prefix = "";

    /**
     * Size of the sequence. Maximum allowable for the sequence is 35. That means 35 - prefix.length.
     */
    @Column(name = "sequence_size")
    @Max(20L)
    private Long sequenceSize = 20L;

    /**
     * Current value of the sequence. This field is read only and lock read. Updated only when a next sequence value is requested.
     */
    @Column(name = "current_sequence_nb")
    @Size(max = 35)
    private Long currentSequenceNb = 0L;

    public GenericSequence() {

    }

    public GenericSequence(String prefix, Long sequenceSize, Long currentSequenceNb) {
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
