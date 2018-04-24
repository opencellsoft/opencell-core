package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBundleTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetBundleTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBundleTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2289076826198378613L;

    /** The bundle template. */
    private BundleTemplateDto bundleTemplate;

    /**
     * Gets the bundle template.
     *
     * @return the bundle template
     */
    public BundleTemplateDto getBundleTemplate() {
        return bundleTemplate;
    }

    /**
     * Sets the bundle template.
     *
     * @param bundleTemplate the new bundle template
     */
    public void setBundleTemplate(BundleTemplateDto bundleTemplate) {
        this.bundleTemplate = bundleTemplate;
    }

    @Override
    public String toString() {
        return "GetBundleTemplateResponseDto [bundleTemplate=" + bundleTemplate + "]";
    }
}