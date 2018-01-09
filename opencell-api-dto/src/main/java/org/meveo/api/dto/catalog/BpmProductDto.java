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
 * @author Edward P. Legaspi
 * @since 28 Nov 2017
 **/
@XmlRootElement(name = "BpmProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpmProductDto extends BaseDto {

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

    public String getBpmCode() {
        return bpmCode;
    }

    public void setBpmCode(String bpmCode) {
        this.bpmCode = bpmCode;
    }

    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
