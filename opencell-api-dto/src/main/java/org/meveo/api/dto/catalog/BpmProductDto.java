package org.meveo.api.dto.catalog;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldDto;

/**
 * The Class BpmProductDto.
 *
 * @author Edward P. Legaspi
 * @since 28 Nov 2017
 */
@XmlRootElement(name = "BpmProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmProductDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5612754939639937822L;

    /** The bpm code. */
    @NotNull
    @XmlAttribute(required = true)
    private String bpmCode;

    /**
     * Will be the code of the newly created ProductTemplate.
     */
    @NotNull
    @XmlElement(required = true)
    private String prefix;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    /**
     * Gets the bpm code.
     *
     * @return the bpm code
     */
    public String getBpmCode() {
        return bpmCode;
    }

    /**
     * Sets the bpm code.
     *
     * @param bpmCode the new bpm code
     */
    public void setBpmCode(String bpmCode) {
        this.bpmCode = bpmCode;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the new prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}