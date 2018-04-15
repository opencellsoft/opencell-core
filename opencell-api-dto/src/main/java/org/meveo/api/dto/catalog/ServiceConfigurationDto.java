package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.crm.custom.CustomFieldValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class ServiceConfigurationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 881323828087615069L;

    /** The code. */
    @NotNull
    @XmlAttribute
    private String code;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    /**
     * Used in the GUI side only.
     */
    @XmlTransient
    @JsonIgnore
    private Map<String, List<CustomFieldValue>> cfValues;

    /** The mandatory. */
    private boolean mandatory;

    /**
     * Tells us that this service is linked to a BusinessServiceModel.
     */
    private boolean instantiatedFromBSM;

    /**
     * Use when matching service template in bsm vs offer.
     */
    private boolean match = false;

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
     * Checks if is mandatory.
     *
     * @return true, if is mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Checks if is instantiated from BSM.
     *
     * @return true, if is instantiated from BSM
     */
    public boolean isInstantiatedFromBSM() {
        return instantiatedFromBSM;
    }

    /**
     * Sets the instantiated from BSM.
     *
     * @param instantiatedFromBSM the new instantiated from BSM
     */
    public void setInstantiatedFromBSM(boolean instantiatedFromBSM) {
        this.instantiatedFromBSM = instantiatedFromBSM;
    }

    /**
     * Checks if is match.
     *
     * @return true, if is match
     */
    public boolean isMatch() {
        return match;
    }

    /**
     * Sets the match.
     *
     * @param match the new match
     */
    public void setMatch(boolean match) {
        this.match = match;
    }

    /**
     * Gets the cf values.
     *
     * @return the cf values
     */
    public Map<String, List<CustomFieldValue>> getCfValues() {
        return cfValues;
    }

    /**
     * Sets the cf values.
     *
     * @param cfValues the cf values
     */
    public void setCfValues(Map<String, List<CustomFieldValue>> cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public String toString() {
        return "ServiceConfigurationDto [code=" + code + ", description=" + description + ", customFields=" + customFields + "]";
    }
}