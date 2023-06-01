package org.meveo.api.dto.custom;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GenericCodeDto")
@XmlAccessorType(FIELD)
public class GenericCodeDto {

    private String formatEL;
    private String entityClass;
    private String prefixOverride;
    @XmlElement(name = "sequence")
    private SequenceDto sequenceDto;

    @XmlElement(name = "id")
    private Long id;

    public String getFormatEL() {
        return formatEL;
    }

    public void setFormatEL(String formatEL) {
        this.formatEL = formatEL;
    }

    public String getPrefixOverride() {
        return prefixOverride;
    }

    public void setPrefixOverride(String prefixOverride) {
        this.prefixOverride = prefixOverride;
    }

    public SequenceDto getSequence() {
        return sequenceDto;
    }

    public void setSequence(SequenceDto sequenceCode) {
        this.sequenceDto = sequenceCode;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public SequenceDto getSequenceDto() {
        return sequenceDto;
    }

    public void setSequenceDto(SequenceDto sequenceDto) {
        this.sequenceDto = sequenceDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}