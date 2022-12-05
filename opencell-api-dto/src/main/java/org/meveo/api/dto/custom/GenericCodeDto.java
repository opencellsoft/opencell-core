package org.meveo.api.dto.custom;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GenericCodeDto")
@XmlAccessorType(FIELD)
public class GenericCodeDto {

    private String formatEL;
    private String entityClass;
    private String prefixOverride;
    @XmlElement(name = "sequence")
    private SequenceDto sequenceDto;

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
}