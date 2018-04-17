package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.filter.Filter;

/**
 * The Class FilterDto.
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "Filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The shared. */
    private Boolean shared;

    /** The input xml. */
    private String inputXml;

    /**
     * Gets the shared.
     *
     * @return the shared
     */
    public Boolean getShared() {
        return shared;
    }

    /**
     * Sets the shared.
     *
     * @param shared the new shared
     */
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    /**
     * Gets the input xml.
     *
     * @return the input xml
     */
    public String getInputXml() {
        return inputXml;
    }

    /**
     * Sets the input xml.
     *
     * @param inputXml the new input xml
     */
    public void setInputXml(String inputXml) {
        this.inputXml = inputXml;
    }

    /**
     * To dto.
     *
     * @param filter the filter
     * @return the filter dto
     */
    public static FilterDto toDto(Filter filter) {
        FilterDto dto = new FilterDto();
        dto.setCode(filter.getCode());
        dto.setDescription(filter.getDescription());
        dto.setShared(filter.getShared());
        dto.setInputXml(filter.getInputXml());
        return dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FilterDto [code=" + getCode() + ", description=" + getDescription() + ", shared=" + shared + ", inputXml=" + inputXml + "]";
    }

}
