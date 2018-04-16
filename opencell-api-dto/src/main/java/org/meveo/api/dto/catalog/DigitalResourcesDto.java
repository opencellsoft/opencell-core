package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.DigitalResource;

/**
 * The Class DigitalResourcesDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DigitalResource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DigitalResourcesDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5517448250177253851L;

    /** The uri. */
    private String uri;

    /** The mime type. */
    private String mimeType;

    /** The disabled. */
    private boolean disabled;

    /**
     * Instantiates a new digital resources dto.
     */
    public DigitalResourcesDto() {
    }

    /**
     * Instantiates a new digital resources dto.
     *
     * @param resource the resource
     */
    public DigitalResourcesDto(DigitalResource resource) {
        super(resource);
        this.setUri(resource.getUri());
        this.setMimeType(resource.getMimeType());
        this.setDisabled(resource.isDisabled());
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri.
     *
     * @param uri the new uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the mime type.
     *
     * @return the mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the mime type.
     *
     * @param mimeType the new mime type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    @Override
    public String toString() {
        return "DigitalResourcesDto [uri=" + uri + ", mimeType=" + mimeType + ", disabled=" + disabled + "]";
    }

}