package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OCCTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplatesDto implements Serializable {

    private static final long serialVersionUID = -8214042837650403747L;

    private List<OccTemplateDto> occTemplate;

    public List<OccTemplateDto> getOccTemplate() {
        if(occTemplate == null){
            occTemplate = new ArrayList<>();
        }
        return occTemplate;
    }

    public void setOccTemplate(List<OccTemplateDto> occTemplate) {
        this.occTemplate = occTemplate;
    }

    @Override
    public String toString() {
        return "OccTemplatesDto [occTemplate=" + occTemplate + "]";
    }
}