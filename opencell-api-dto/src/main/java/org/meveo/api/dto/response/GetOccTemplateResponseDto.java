package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.OccTemplateDto;

/**
 * The Class GetOccTemplateResponseDto.
 */
@XmlRootElement(name = "GetOccTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOccTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4612709775410582280L;

    /** The occ template. */
    private OccTemplateDto occTemplate;

    /**
     * Gets the occ template.
     *
     * @return the occ template
     */
    public OccTemplateDto getOccTemplate() {
        return occTemplate;
    }

    /**
     * Sets the occ template.
     *
     * @param occTemplate the new occ template
     */
    public void setOccTemplate(OccTemplateDto occTemplate) {
        this.occTemplate = occTemplate;
    }

    @Override
    public String toString() {
        return "GetOccTemplateResponse [occTemplate=" + occTemplate + ", toString()=" + super.toString() + "]";
    }
}