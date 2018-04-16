package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.OccTemplatesDto;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "GetOccTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOccTemplatesResponseDto extends SearchResponse {

    private static final long serialVersionUID = 4612709775410582280L;

    private OccTemplatesDto occTemplates;

    public OccTemplatesDto getOccTemplates() {
        if(occTemplates == null) {
            occTemplates = new OccTemplatesDto();
        }
        
        return occTemplates;
    }

    public void setOccTemplates(OccTemplatesDto occTemplates) {
        this.occTemplates = occTemplates;
    }

    @Override
    public String toString() {
        return "GetOccTemplatesResponse [occTemplates=" + occTemplates + ", toString()=" + super.toString()
                + "]";
    }

}
