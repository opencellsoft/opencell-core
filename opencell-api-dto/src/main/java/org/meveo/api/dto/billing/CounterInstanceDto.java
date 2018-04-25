package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.model.billing.CounterInstance;

/**
 * The Class CounterInstanceDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterInstanceDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -72154111229222183L;

    /** The code. */
    @XmlElement(required = true)
    private String code;

    /** The description. */
    @XmlElement(required = false)
    private String description;

    /**
     * Instantiates a new counter instance dto.
     */
    public CounterInstanceDto() {
    }

    /**
     * Instantiates a new counter instance dto.
     *
     * @param counterInstance the counter instance
     */
    public CounterInstanceDto(CounterInstance counterInstance) {
        this.code = counterInstance.getCode();
        this.description = counterInstance.getDescription();
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

    @Override
    public String toString() {
        return "CounterInstanceDto [code=" + code + ", description=" + description + "]";
    }
}