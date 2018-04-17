package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * The Class ParentEntityDto.
 *
 * @author Tony Alejandro.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParentEntityDto implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute()
    private String description;

    /**
     * Instantiates a new parent entity dto.
     */
    public ParentEntityDto() {
    }

    /**
     * Instantiates a new parent entity dto.
     *
     * @param code the code
     * @param description the description
     */
    public ParentEntityDto(String code, String description) {
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

    @Override
    public String toString() {
        return "ParentEntityDto [code=" + code + ", description=" + description + "]";
    }
}