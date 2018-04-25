package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class OccTemplatesDto.
 */
@XmlRootElement(name = "OCCTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8214042837650403747L;

    /** The occ template. */
    private List<OccTemplateDto> occTemplate;

    /**
     * Gets the occ template.
     *
     * @return the occ template
     */
    public List<OccTemplateDto> getOccTemplate() {
        if (occTemplate == null) {
            occTemplate = new ArrayList<>();
        }
        return occTemplate;
    }

    /**
     * Sets the occ template.
     *
     * @param occTemplate the new occ template
     */
    public void setOccTemplate(List<OccTemplateDto> occTemplate) {
        this.occTemplate = occTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OccTemplatesDto [occTemplate=" + occTemplate + "]";
    }
}