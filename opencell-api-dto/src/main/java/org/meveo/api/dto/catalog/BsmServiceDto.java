package org.meveo.api.dto.catalog;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;

/**
 * The Class BsmServiceDto.
 */
@XmlRootElement(name = "BsmService")
@XmlAccessorType(XmlAccessType.FIELD)
public class BsmServiceDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5612754939639937822L;

    /** The bsm code. */
    @NotNull
    @XmlAttribute(required = true)
    private String bsmCode;

    /** The prefix. */
    @NotNull
    @XmlAttribute(required = true)
    private String prefix;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;
    
    /** The image base64 encoding string. */
    private String imageBase64;
    
    /** The image path. */
    private String imagePath;

    /**
     * Instantiates a new bsm service dto.
     */
    public BsmServiceDto() {

    }

    /**
     * Gets the bsm code.
     *
     * @return the bsmCode
     */
    public String getBsmCode() {
        return bsmCode;
    }

    /**
     * Sets the bsm code.
     *
     * @param bsmCode the bsmCode to set
     */
    public void setBsmCode(String bsmCode) {
        this.bsmCode = bsmCode;
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
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }
    
    /**
     * Gets the image Base64 encoding string.
     *
     * @return the image Base64 encoding string
     */
    public String getImageBase64() {
        return imageBase64;
    }
    
    /**
     * Sets the image Base64 encoding string.
     *
     * @param imageBase64 the image Base64 encoding string
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    
    /**
     * Gets the image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path.
     *
     * @param imagePath the new image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "BsmServiceDto [bsmCode=" + bsmCode + ", prefix=" + prefix + ", customFields=" + customFields + "]";
    }

}