package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.OccTemplatesDto;

/**
 * The Class GetOccTemplatesResponseDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "GetOccTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOccTemplatesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4612709775410582280L;

    /** The occ templates. */
    private OccTemplatesDto occTemplates;

    /**
     * Gets the occ templates.
     *
     * @return the occ templates
     */
    public OccTemplatesDto getOccTemplates() {
        if (occTemplates == null) {
            occTemplates = new OccTemplatesDto();
        }

        return occTemplates;
    }

    /**
     * Sets the occ templates.
     *
     * @param occTemplates the new occ templates
     */
    public void setOccTemplates(OccTemplatesDto occTemplates) {
        this.occTemplates = occTemplates;
    }

    @Override
    public String toString() {
        return "GetOccTemplatesResponse [occTemplates=" + occTemplates + ", toString()=" + super.toString() + "]";
    }
}