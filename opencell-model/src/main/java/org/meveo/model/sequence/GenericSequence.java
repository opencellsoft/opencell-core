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

package org.meveo.model.sequence;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

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
