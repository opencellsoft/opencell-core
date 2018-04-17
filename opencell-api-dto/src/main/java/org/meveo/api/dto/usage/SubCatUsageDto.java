package org.meveo.api.dto.usage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class SubCatUsageDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "SubCatUsage")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCatUsageDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The code. */
    private String code;
    
    /** The description. */
    private String description;

    /** The list usage. */
    @XmlElementWrapper
    @XmlElement(name = "usage")
    List<UsageDto> listUsage = new ArrayList<UsageDto>();

    /**
     * Instantiates a new sub cat usage dto.
     */
    public SubCatUsageDto() {
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list usage.
     *
     * @return the listUsage
     */
    public List<UsageDto> getListUsage() {
        return listUsage;
    }

    /**
     * Sets the list usage.
     *
     * @param listUsage the listUsage to set
     */
    public void setListUsage(List<UsageDto> listUsage) {
        this.listUsage = listUsage;
    }

    @Override
    public String toString() {
        return "SubCatUsageDto [code=" + code + ", description=" + description + ", listUsage=" + listUsage + "]";
    }
}