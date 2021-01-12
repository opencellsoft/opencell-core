package org.meveo.model.sequence;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.CustomGenericEntityCode;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "generic_sequence")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "generic_sequence_seq"), })
@Inheritance(strategy = TABLE_PER_CLASS)
public class Sequence extends BusinessEntity {

    private static final long serialVersionUID = -1964277428044516329L;

    /**
     * SEQUENCE "Sequence" (a classical sequence from 0 to infinity),
     * NUMERIC "Random number", ALPHA_UP "Random string uppercase", UUID "Universal Unique IDentifier",
     * REGEXP "Regular expression" (random string from a regexp)
     */
    @Column(name = "sequence_type")
    @Enumerated(STRING)
    private SequenceTypeEnum sequenceType;


    /**
     *  Size of the generated number
     *  (padded with zeros for SEQUENCE and NUMERIC)
     */
    @Column(name = "sequence_size")
    private Integer sequenceSize;

    /**
     * Current value of the sequence. This field is read only and lock read. Updated only when a next sequence value is requested.
     * used only if sequenceType=SEQUENCE
     */
    @Column(name = "current_number")
    private Long currentNumber = 0L;

    /**
     * Generate random string from a Regular expression
     * used only if sequenceType=REGEXP
     */
    @Column(name = "sequence_pattern", length = 2000)
    @Size(max = 2000)
    private String sequencePattern;

    @OneToMany(mappedBy = "sequence")
    private List<CustomGenericEntityCode> genericEntityCodes = new ArrayList<>();

    public Sequence() { }

    public Sequence(SequenceTypeEnum sequenceType, Integer sequenceSize, Long currentNumber, String sequencePattern) {
        this.sequenceType = sequenceType;
        this.sequenceSize = sequenceSize;
        this.currentNumber = currentNumber;
        this.sequencePattern = sequencePattern;
    }

    public Sequence(Integer sequenceSize, Long currentNumber) {
        this.sequenceSize = sequenceSize;
        this.currentNumber = currentNumber;
    }

    public Integer getSequenceSize() {
        return sequenceSize;
    }

    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    public SequenceTypeEnum getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(SequenceTypeEnum sequenceType) {
        this.sequenceType = sequenceType;
    }

    public Long getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Long currentNumber) {
        this.currentNumber = currentNumber;
    }

    public String getSequencePattern() {
        return sequencePattern;
    }

    public void setSequencePattern(String sequencePattern) {
        this.sequencePattern = sequencePattern;
    }

    public List<CustomGenericEntityCode> getGenericEntityCodes() {
        return genericEntityCodes;
    }

    public void setGenericEntityCodes(List<CustomGenericEntityCode> genericEntityCodes) {
        this.genericEntityCodes = genericEntityCodes;
    }
}