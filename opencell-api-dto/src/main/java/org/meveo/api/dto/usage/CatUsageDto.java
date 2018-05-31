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
 * The Class CatUsageDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CatUsage")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatUsageDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The code. */
    private String code;
    
    /** The description. */
    private String description;

    /** The list sub cat usage. */
    @XmlElementWrapper
    @XmlElement(name = "subCatUsage")
    List<SubCatUsageDto> listSubCatUsage = new ArrayList<SubCatUsageDto>();

    /**
     * Instantiates a new cat usage dto.
     */
    public CatUsageDto() {

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
     * Gets the list sub cat usage.
     *
     * @return the listSubCatUsage
     */
    public List<SubCatUsageDto> getListSubCatUsage() {
        return listSubCatUsage;
    }

    /**
     * Sets the list sub cat usage.
     *
     * @param listSubCatUsage the listSubCatUsage to set
     */
    public void setListSubCatUsage(List<SubCatUsageDto> listSubCatUsage) {
        this.listSubCatUsage = listSubCatUsage;
    }

    /**
     * Gets the serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "CatUsageDto [code=" + code + ", description=" + description + ", listSubCatUsage=" + listSubCatUsage + "]";
    }
}