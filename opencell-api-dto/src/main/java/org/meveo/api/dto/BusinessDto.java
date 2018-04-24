package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.BusinessEntity;


/**
 * The dto for business entities.
 * 
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BusinessDto extends AuditableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4451119256601996946L;

    /** The id. */
    @XmlAttribute()
    protected Long id;

    /** The code. */
    // @Pattern(regexp = "^[@A-Za-z0-9_\\.-]+$")
    @XmlAttribute(required = true)
    protected String code;

    /** The description. */
    @XmlAttribute()
    protected String description;

    /** The updated code. */
    protected String updatedCode;

    /**
     * Instantiates a new business dto.
     */
    public BusinessDto() {

    }

    /**
     * Instantiates a new business dto.
     *
     * @param businessEntity the BusinessEntity entity
     */
    public BusinessDto(BusinessEntity businessEntity) {
        if (businessEntity != null) {
            code = businessEntity.getCode();
            description = businessEntity.getDescription();
        }
    }

    /**
     * Gets the updated code.
     *
     * @return the updated code
     */
    public String getUpdatedCode() {
        return updatedCode;
    }

    /**
     * Sets the updated code.
     *
     * @param updatedCode the new updated code
     */
    public void setUpdatedCode(String updatedCode) {
        this.updatedCode = updatedCode;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId(){
        return id;
    }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
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
