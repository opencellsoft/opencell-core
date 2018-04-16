package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.BusinessEntity;

/**
 * The Class BusinessEntityDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "BusinessEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessEntityDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The code. */
    @XmlAttribute()
    private String code;

    /** The description. */
    @XmlAttribute()
    private String description;

    /**
     * Instantiates a new business entity dto.
     */
    public BusinessEntityDto() {
    }

    /**
     * Instantiates a new business entity dto.
     *
     * @param entity the entity
     */
    public BusinessEntityDto(BusinessEntity entity) {
        this.code = entity.getCode();
        this.description = entity.getDescription();
    }

    /**
     * Instantiates a new business entity dto.
     *
     * @param code the code
     * @param description the description
     */
    public BusinessEntityDto(String code, String description) {
        this.code = code;
        this.description = description;
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
     * @param code the new code
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
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
