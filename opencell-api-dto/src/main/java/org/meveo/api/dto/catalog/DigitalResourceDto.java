package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.DigitalResource;

@XmlRootElement(name = "DigitalResource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DigitalResourceDto extends EnableBusinessDto {

    private static final long serialVersionUID = 5517448250177253851L;

    private String uri;

    private String mimeType;

    public DigitalResourceDto() {
    }

    public DigitalResourceDto(DigitalResource resource) {
        super(resource);
        this.setUri(resource.getUri());
        this.setMimeType(resource.getMimeType());
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}