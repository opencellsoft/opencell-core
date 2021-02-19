package org.meveo.api.dto.custom;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import static org.meveo.model.sequence.SequenceTypeEnum.fromValue;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.sequence.Sequence;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Sequence")
@XmlAccessorType(FIELD)
public class SequenceDto extends BaseEntityDto {

    private SequenceType sequenceType;
    private String code;
    private Integer size;
    private Long currentNumber;
    private String pattern;

    public SequenceType getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Long currentNumber) {
        this.currentNumber = currentNumber;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static Sequence from(SequenceDto sequenceDto) {
        Sequence sequence = new Sequence();
        sequence.setCurrentNumber(sequenceDto.getCurrentNumber());
        sequence.setSequencePattern(sequenceDto.getPattern());
        sequence.setSequenceSize(sequenceDto.getSize());
        sequence.setSequenceType(fromValue(sequenceDto.getSequenceType().name()));
        sequence.setCode(sequenceDto.getCode());
        return sequence;
    }

    public static SequenceDto from(Sequence sequence) {
        SequenceDto dto = new SequenceDto();
        dto.setCurrentNumber(sequence.getCurrentNumber());
        dto.setPattern(sequence.getSequencePattern());
        dto.setSequenceType(SequenceType.fromValue(sequence.getSequenceType().name()));
        dto.setSize(sequence.getSequenceSize());
        dto.setCode(sequence.getCode());
        return dto;
    }
}