package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.filter.Filter;

/**
 * @author Tyshan Shi
 *
 **/
@XmlRootElement(name = "Filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterDto extends EnableBusinessDto {

    private static final long serialVersionUID = 1L;

    private Boolean shared;
    private String inputXml;

    public FilterDto() {

    }

    public FilterDto(Filter filter) {
        super(filter);

        shared = filter.getShared();
        inputXml = filter.getInputXml();
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getInputXml() {
        return inputXml;
    }

    public void setInputXml(String inputXml) {
        this.inputXml = inputXml;
    }

    @Override
    public String toString() {
        return "FilterDto [code=" + getCode() + ", description=" + getDescription() + ", shared=" + shared + ", inputXml=" + inputXml + "]";
    }
}